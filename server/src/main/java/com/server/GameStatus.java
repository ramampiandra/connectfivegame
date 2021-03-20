package com.server;

public interface GameStatus {
	
	public static final int WAITING_PLAYER = 0;
	
	public static final int ABORT_GAME = 1;
	
	public static final int GAME_IN_PROGRESS = 2;
	
	public static final int GAME_OVER = 3;
	
	public static final int GAME_DRAW = 4;
	
	public static final int RESTART_GAME = 5;


}
