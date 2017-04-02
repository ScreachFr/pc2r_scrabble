package game;

import core.ThreadClient;

public class Proposition {
	public ThreadClient client;
	public String proposition;
	public long timestamp;
	
	public Proposition(ThreadClient client, String proposition, long timestamp) {
		this.client = client;
		this.proposition = proposition;
		this.timestamp = timestamp;
	}
	
	public ThreadClient getClient() {
		return client;
	}
	
	public String getProposition() {
		return proposition;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
}
