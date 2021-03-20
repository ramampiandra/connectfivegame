package client;

public class MainApplication {

	public static void main(String[] args) {
		
		ClientGame client = new ClientGame();
		try {
			client.initGame();	
			client.startGame();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.closeResources();
		}
	}

}
