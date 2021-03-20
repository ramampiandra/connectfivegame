package client;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientGame {
	
	private GameState gameState;
	
	private String userId, colorPiece;
	
	public static final int GAME_OVER = 3;
	
	public static final int GAME_DRAW = 4;
	
	public static final int ABORT_GAME = 1;
	
	Scanner scanner = null;
	
	private ExecutorService executorService;
	
	public static AtomicBoolean isProgramTerminate = new AtomicBoolean(false);
	
	public void initGame() throws Exception {
		String error = null;
		boolean isSecondPlayerToConnect = true;
		do {
			while(userId == null 
					|| userId.isEmpty()) {
				scanner = new Scanner(System.in);
				System.out.println("Please insert your userId game");
				userId = scanner.nextLine();
			}
			
			stateGame();
			
			//Player one
			if(gameState == null) {
				isSecondPlayerToConnect = false;
				while(colorPiece == null 
						|| userId.isEmpty()
						|| colorPiece != null && !Arrays.asList("X", "O").contains(colorPiece)) {
					scanner = new Scanner(System.in);
					System.out.println("Select your color Pieces among (X or O) ");
					colorPiece = scanner.nextLine();
				}
			}
			gameState = HttpClientHandler.getData("/init?userId=" + userId + "&colorPiece=" + colorPiece);
			error = gameState.getErrorMessage();
			if(error != null && !error.isEmpty()) {
				System.out.println(error);
				userId = "";
				colorPiece  = "";
			}
		} while(error != null && !error.isEmpty());
		
		System.out.println("You are connected with clientID " + userId);
		System.out.println("Your color piece is   " + (isSecondPlayerToConnect ? gameState.getUserColorPieces().get(1) 
																					: colorPiece));
		
		//Run KeepAlive
		executorService = Executors.newFixedThreadPool(1);
		executorService.execute(new KeepAlive(userId));
		
		System.out.println("");

	}
	
	public void stateGame() throws Exception {
		gameState = HttpClientHandler.getData("/state");
	}
	
	public void startGame() throws Exception {
		System.out.println("Waiting for the second player ...\n");
		while(gameState.getUserNames().size() < 2 && gameState.getStatus() != ABORT_GAME) {
			stateGame();
			Thread.sleep(2000);
		}
		
		System.out.println("*** CONNECT 5 GAME *** \n");
		
		int waitingTurn = 0;
		
		while(!Arrays.asList(GAME_DRAW, GAME_OVER,ABORT_GAME).contains(gameState.getStatus())) {
			if(userId.equals(gameState.getTurn())) {
				gameState.printBoardGame();
				waitingTurn = 0;
				int column = 0;
				while(column < 1 || column > 9) {
					System.out.println("It�s your turn " + userId + ", please enter column (1-9):");
					column = scanner.nextInt();
				}
				gameState = HttpClientHandler.getData("/game?userId=" + userId + "&column=" + column);
			} else {
				if(waitingTurn == 0) {
					gameState.printBoardGame();
					System.out.println("Waiting for " + gameState.getTurn() + " turn");
					waitingTurn++;
				}
				stateGame();
				//Thread.sleep(50);
			}
		}
		if(gameState.getStatus() == GAME_OVER) {
			gameState.printBoardGame();
			System.out.println("The winner is : " + gameState.getWinner());
		} else if (gameState.getStatus() == GAME_OVER) {
			gameState.printBoardGame();
			System.out.println("The game is draw, no winner");
		} else {
			System.out.println("Abort the program");
			isProgramTerminate.set(true);
		}
	}
	
	public void closeResources() {
		try {
			scanner.close();
			isProgramTerminate.set(true);
			shutdownAndAwaitTermination(executorService);
			
			//Delete au State and resource in Server
			HttpClientHandler.getData("/deleteKeepAlive?userId=" + userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void shutdownAndAwaitTermination(ExecutorService pool) {
	   pool.shutdown(); 
	   try {
		   if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
		       pool.shutdownNow(); 
		       if (!pool.awaitTermination(2, TimeUnit.SECONDS))
		           System.err.println("Pool did not terminate");
		   }
		} catch (InterruptedException ie) {
		    pool.shutdownNow();
		    Thread.currentThread().interrupt();
		}
	}

}
