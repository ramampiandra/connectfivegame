package client;

/**
 * The KeepAlive class sends regularly (every 2s)
 * Request to the server to inform that it's still alive
 * @author claire
 *
 */
public class KeepAlive implements Runnable {
	
	private String userId;
	
	public KeepAlive(String userId) {
		this.userId = userId;
	}

	@Override
	public void run() {
		try {
			while(!ClientGame.isProgramTerminate.get()){  
				HttpClientHandler.getData("/keepAlive?userId=" + userId);
				Thread.sleep(2000);
			}  
		} catch (Exception e) {
			System.out.println("Erreur lors de l'envoi de timeout " + e.getMessage());
		}
	}

}
