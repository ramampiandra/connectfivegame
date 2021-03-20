package client;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is the logic of the client game
 * @author claire
 *
 */
public class ClientGame {
	
	private GameState gameState;
	
	private String userId, colorPiece;
	
	public static final int GAME_OVER = 3;
	
	public static final int GAME_DRAW = 4;
	
	public static final int ABORT_GAME = 1;
	
	Scanner scanner = null;
	
	//Thread to run the keepAlive
	private ExecutorService executorService;
	
	public static AtomicBoolean isProgramTerminate = new AtomicBoolean(false);
	
	/**
	 * Initialize the game by entering the user name and the color of piece
	 * Give error when the user name is already exist
	 * Or there are already two players in progress in the game 
	 * @throws Exception
	 */
	public void initGame() throws Exception {
		String error = null;
		boolean isSecondPlayerToConnect = true;
		do {
			while(userId == null 
					|| userId.isEmpty()
					|| (userId != null && userId.contains(" "))) {
				scanner = new Scanner(System.in);
				System.out.println("Please insert your valid userId game (no space)");
				userId = scanner.nextLine();
			}
			
			stateGame();
			
			//The first player (player one) should execute on this if
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
		
		//Run KeepAlive to inform the server that the client is still running
		executorService = Executors.newFixedThreadPool(1);
		executorService.execute(new KeepAlive(userId));
		
		System.out.println("");

	}
	
	/**
	 * Return the state of GameState to the client
	 * @throws Exception
	 */
	public void stateGame() throws Exception {
		gameState = HttpClientHandler.getData("/state");
	}
	
	/**
	 * This method manages the logic of starting game 
	 * Also manage the waiting player
	 * @throws Exception
	 */
	public void startGame() throws Exception {
		System.out.println("Waiting for the second player ...\n");
		//We neet to wait until the game has two ready players and the game is not yet abort
		while(gameState.getUserNames().size() < 2 && gameState.getStatus() != ABORT_GAME) {
			//Make http request to get the new GameState
			stateGame();
			Thread.sleep(500);
		}
		
		System.out.println("*** CONNECT 5 GAME *** \n");
		
		int waitingTurn = 0;
		
		//Get out the loop when the game is over or the game is draw
		while(!Arrays.asList(GAME_DRAW, GAME_OVER,ABORT_GAME).contains(gameState.getStatus())) {
			//Run this if bloc if it's the player turn
			if(userId.equals(gameState.getTurn())) {
				gameState.printBoardGame();
				waitingTurn = 0;
				int column = 0;
				while(column < 1 || column > 9) {
					System.out.println("It’s your turn " + userId + ", please enter column (1-9):");
					column = scanner.nextInt();
				}
				gameState = HttpClientHandler.getData("/game?userId=" + userId + "&column=" + column);
			} else { //Else if run this one if it's not his turn
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
			System.out.println("The winner is : " + gameState.getWinner() + ", color : " + colorPiece);
		} else if (gameState.getStatus() == GAME_OVER) {
			gameState.printBoardGame();
			System.out.println("The game is draw, no winner");
		} else {
			System.out.println("Abort the program");
			isProgramTerminate.set(true);
		}
	}
	
	/**
	 * Terminate the thread 
	 * Call the deleteKeepAlive url to inform the server that 
	 */
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
	
	/**
	 * This methode terminate the thread used by KeepAlive.class
	 * @param pool
	 */
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
