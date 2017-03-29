package game;

import game.Pouch.EmptyPouchException;
import game.pouches.RandomPouch;

import java.util.ArrayList;
import java.util.Arrays;

public class Scrabble {
	private final static int BOARD_SIZE = 15;
	private final static int DRAW_SIZE = 7;
	
	private Pouch pouch;
	private WordChecker wordChecker;
	private char[][] board;
	
	//cache
	private String boardState;
	private String currentDraw;
	
	public Scrabble(Pouch pouch, WordChecker wordChecker) {
		this.pouch = pouch;
		this.wordChecker = wordChecker;
		board = new char[BOARD_SIZE][BOARD_SIZE];
		initBoard();
		refreshBoardState();
		pouch.resetLetters();
	}
	
	/**
	 * Met toutes les cases du plateau de jeu à '0'.
	 */
	private void initBoard() {
		Arrays.stream(board).forEach(b -> Arrays.fill(b, '0'));
	}
	
	public void refreshBoardState() {
		String newState = "";
		
		for (char[] cs : board) {
			for (char c : cs) {
				newState += c;
			}
		}
		
		boardState = newState;
	}
	
	public String getBoardState() {
		return boardState;
	}
	
	public String getBoardStateForHumans() {
		String result = "";
		
		for (int i = 0 ; i < BOARD_SIZE; i++) 
			result += boardState.substring(i, i + BOARD_SIZE) + "\n";
		
		return result;
	}
	
	public void drawLetters() throws EmptyPouchException {
		currentDraw = pouch.easyLetterPick(DRAW_SIZE);
	}
	
	public String getCurrentDraw() {
		return currentDraw;
	}
	
}
