package game;

import java.util.ArrayList;

import core.ThreadClient;

public class Proposition {
	private ThreadClient client;
	private String proposition;
	private long timestamp;
	
	private boolean isValidated;
	
	private char[][] parsedBoard;
	private int score;
	private ArrayList<String> foundWords;
	private ArrayList<Character> usedLetters;
	
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
	
	public ArrayList<String> getFoundWords() {
		return foundWords;
	}
	
	public void addFoundWord(String word) {
		if (foundWords == null) 
			foundWords = new ArrayList<>();
			
		foundWords.add(word);
		
	}
	
	public void addFoundWord(ArrayList<String> words) {
		if (foundWords == null) 
			foundWords = new ArrayList<>();
			
		foundWords.addAll(words);
		
	}
	
	public void addUsedLetter(Character c) {
		if (usedLetters == null) {
			usedLetters = new ArrayList<>();
		}

		usedLetters.add(c);
	}
	
	public ArrayList<Character> getUsedLetters() {
		return usedLetters;
	}
	
}
