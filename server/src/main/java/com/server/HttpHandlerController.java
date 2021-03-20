package com.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class manages the http request sends by the clien
 * The class receives the http request and call the GameLogic to process the request
 * @author modis
 *
 */
@RestController
public class HttpHandlerController {
	
	@Autowired
	private KeepAlive keepAlive;
	
	/**
	 * Http request to init the game
	 * @param userId
	 * @param colorPiece
	 * @return
	 */
	@GetMapping("/init")
	public GameState createGame(@RequestParam(required = true, name = "userId") String userId,
			@RequestParam("colorPiece") String colorPiece) {
		GameLogic gameLogic = GameLogic.getInstance();
		gameLogic.initState(userId, colorPiece);
		return gameLogic.getGameState();
	}
	
	/**
	 * Http request to get the state of the game
	 * @return
	 */
	@GetMapping("/state")
	public GameState createGame() {
		return GameLogic.getInstance().getGameState();
	}
	
	/**
	 * Http request to move and play the game
	 * @param userId
	 * @param column
	 * @return
	 */
	@GetMapping("/game")
	public GameState game(@RequestParam(required = true, name = "userId") String userId,
						  @RequestParam(required = true, name = "column") int column) {
		return GameLogic.getInstance().game(userId, column);
	}
	
	/**
	 * Http request to receive the keep alive sending by the client
	 * @param userId
	 * @return
	 */
	@GetMapping("/keepAlive")
	public String keepAlive(@RequestParam(required = true, name = "userId") String userId) {
		keepAlive.addOrUpdateKeepAlive(userId);
		return "Keep Alive Ok";
	}
	
	/**
	 * Http request to delete the player's gameState after receiving the url
	 * @param userId
	 * @return
	 */
	@GetMapping("/deleteKeepAlive")
	public String deleteKeepAlive(@RequestParam(required = true, name = "userId") String userId) {
		keepAlive.deleteKeepAlive(userId);
		return "deleteKeepAlive";
	}

}
