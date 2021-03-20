package com.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpHandlerController {
	
	@Autowired
	private KeepAlive keepAlive;
	
	@GetMapping("/init")
	public GameState createGame(@RequestParam(required = true, name = "userId") String userId,
			@RequestParam("colorPiece") String colorPiece) {
		GameLogic gameLogic = GameLogic.getInstance();
		gameLogic.initState(userId, colorPiece);
		return gameLogic.getGameState();
	}
	
	@GetMapping("/state")
	public GameState createGame() {
		return GameLogic.getInstance().getGameState();
	}
	
	@GetMapping("/game")
	public GameState game(@RequestParam(required = true, name = "userId") String userId,
						  @RequestParam(required = true, name = "column") int column) {
		return GameLogic.getInstance().game(userId, column);
	}
	
	@GetMapping("/keepAlive")
	public String keepAlive(@RequestParam(required = true, name = "userId") String userId) {
		keepAlive.addOrUpdateKeepAlive(userId);
		return "Keep Alive Ok";
	}
	
	@GetMapping("/deleteKeepAlive")
	public String deleteKeepAlive(@RequestParam(required = true, name = "userId") String userId) {
		keepAlive.deleteKeepAlive(userId);
		return "deleteKeepAlive";
	}

}
