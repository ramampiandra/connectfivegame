package com.server;

import java.util.List;

public class GameState {
	
	private List<String> userNames;
	
	private List<String> userColorPieces;
	
	private String turn;
	
	private String [][] stateGame;
	
	//0, end game
	private int status;
	
	private String errorMessage;
	
	private String winner;

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public String[][] getStateGame() {
		return stateGame;
	}

	public void setStateGame(String[][] stateGame) {
		this.stateGame = stateGame;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<String> getUserNames() {
		return userNames;
	}

	public void setUserNames(List<String> userNames) {
		this.userNames = userNames;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<String> getUserColorPieces() {
		return userColorPieces;
	}

	public void setUserColorPieces(List<String> userColorPieces) {
		this.userColorPieces = userColorPieces;
	}

	
	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getColorPieceFromUserId(String userId) {
		String color = "";
		for(int i = 0; i < userNames.size(); i++) {
			if(userId.equals(userNames.get(i))) {
				color = userColorPieces.get(i);
				break;
			}
		}
		return color;
	}
	
	
}
