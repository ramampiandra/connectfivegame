package com.server;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class KeepAlive {
	
	ConcurrentHashMap<String, Long> keepAliveValues;
	
	public KeepAlive() {
		keepAliveValues = new ConcurrentHashMap<>();
	}
	
	public void addOrUpdateKeepAlive(String userId) {
		keepAliveValues.put(userId, System.currentTimeMillis());
	}
	
	public void deleteKeepAlive(String userId) {
		if(keepAliveValues.containsKey(userId)) {
			keepAliveValues.remove(userId);
		}
		if(keepAliveValues.isEmpty()) {
			GameLogic.getInstance().cleatGameData();
		}
	}

	public ConcurrentHashMap<String, Long> getKeepAliveValues() {
		return keepAliveValues;
	}

}
