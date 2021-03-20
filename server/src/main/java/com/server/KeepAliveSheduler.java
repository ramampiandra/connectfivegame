package com.server;

import java.util.Iterator;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeepAliveSheduler {
	
	@Autowired
	private KeepAlive keepAlive;
	
	private static final long KEEP_ALIVE_TIME = 4000;
	
	@Scheduled(fixedDelay = 2000)
	public void checkIfAtLeastOneClientIsDeconnected() {
		if(GameLogic.getInstance().getGameState() != null
				&& GameLogic.getInstance().getGameState().getStatus() != GameStatus.ABORT_GAME) {
			for (Iterator<Entry<String, Long>> iter = keepAlive.getKeepAliveValues().entrySet().iterator(); iter.hasNext(); ) {
			    Entry<String, Long> entry = iter.next();
				long userConnectTime = entry.getValue();
				long diff = System.currentTimeMillis() - userConnectTime;
				//System.out.println("diff " + diff + "  userId " + entry.getKey());
				if(diff > KEEP_ALIVE_TIME) {
					GameLogic.getInstance().getGameState().setStatus(GameStatus.ABORT_GAME);
					
					//Force to delete the keepAlive
					keepAlive.deleteKeepAlive(entry.getKey());
					break;
				}
			}
		}
		
	}

}
