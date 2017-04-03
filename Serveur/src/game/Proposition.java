package game;

import core.ThreadClient;

public class Proposition {
	private ThreadClient client;
	private String proposition;
	private long timestamp;
	
	private boolean isValidated;
	
	private char[][] parsedBoard;
	private int score;
	
	public Proposition(ThreadClient client, String proposition, long timestamp) {
		this.client = client;
		this.proposition = proposition;
		this.timestamp = timestamp;
		isValidated = false;
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
	
	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}
	
	public boolean isValidated() {
		return isValidated;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return score;
	}
	
	public char[][] getParsedBoard() {
		return parsedBoard;
	}
	
	public void setParsedBoard(char[][] parsedBoard) {
		this.parsedBoard = parsedBoard;
	}
	
}
