package game;

import game.Pouch.EmptyPouchException;
import game.WordPlacementException.Why;
import game.pouches.RandomPouch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class Scrabble {
	private final static int BOARD_SIZE = 15;
	private final static int DRAW_SIZE = 7;
	private final static Character NULL_CHAR = '0';
	
	
	private Pouch pouch;
	private WordChecker wordChecker;
	private char[][] board;
	private ArrayList<Character> currentDraw;
	
	//Trucs qui doivent être thread safe.
	//TODO Ajouter une liste de proposition. Une proposition doit avoir un état de jeu, un timestamp et des points(?).
	
	
	//cache
	private String boardState;
	private String lazyCurrentDraw;
	
	
	public Scrabble(Pouch pouch, WordChecker wordChecker) {
		this.pouch = pouch;
		this.wordChecker = wordChecker;
		board = new char[BOARD_SIZE][BOARD_SIZE];
		currentDraw = new ArrayList<Character>();
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
	
	/**
	 * Rafraichit le cache d'état du plateau de jeu.
	 */
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
	
	/**
	 * Car pouvoir observer l'état du plateau en faisant en sorte que ça ressemble à un plateau c'est cool pour debug.
	 */
	public String getBoardStateForHumans() {
		String result = "";
		
		for (int i = 0 ; i < BOARD_SIZE; i++) 
			result += boardState.substring(i, i + BOARD_SIZE) + "\n";
		
		return result;
	}
	
	/**
	 * Permet de proposer un réponse.
	 * @param proposedBoard - Réponse sous forme d'état du plateau de jeu avec les nouvelles lettres.
	 * @throws WordPlacementException - La proposition n'est pas bonne. Cela peut vouloir dire que le mot
	 * 		proposé ou les nouveaux mots qu'il engendre n'existe pas. Ou alors t'a tentative de triche viens
	 * 		de lamentablement échouer. 
	 */
	public void propose(String proposedBoard) throws WordPlacementException {
		char[][] proposition = toArray(proposedBoard);
		
		if (!isPropositionValid(proposition))
			throw new WordPlacementException(Why.INVALID_PROPOSITION);
		
		HashMap<Character, Pair<Integer, Integer>> newLetters = findNewLetters(proposition);
		
		// Proposition sans nouvelle lettre.
		if (newLetters.keySet().isEmpty())
			throw new WordPlacementException(Why.INVALID_PROPOSITION);
		
		//TODO : check sens, suite de lettre sans troue.
		
	}
	
	
	/**
	 * Vérifie si une proposition ne modifie pas les lettres déja placées sur le plateau de jeu.
	 * @param proposition - Proposition du client à verifier.
	 * @return - La proposition est-elle valide ?
	 * XXX Ça a l'air de fonctionner.
	 */
	public boolean isPropositionValid(char[][] proposition) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] != NULL_CHAR && board[i][j] != proposition[i][j]) 
					return false; // Ancien board altéré. Tricheur...
			}
		}
		
		return true;
	}
	
	/**
	 * Trouves les lettres ajoutées et les retournes accompagnées de leur position.
	 * @param proposition - Proposition du client. Là où nous allons chercher de nouvelles lettres.
	 * @return - Liste des nouvelles lettres accompagnées de leurs coordonnées.
	 */
	public HashMap<Character, Pair<Integer, Integer>> findNewLetters(char[][] proposition) {
		HashMap<Character, Pair<Integer, Integer>> result = new HashMap<>();
		
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (board[i][j] == NULL_CHAR && proposition[i][j] != NULL_CHAR)
					result.put(proposition[i][j], new Pair<Integer, Integer>(i, j));
			}
		}
		
		return result; 
	}
	
	/**
	 * Permet de savoir si l'ajout est vartical.
	 * @param newLetters - Lettres ajoutées au plateau.
	 * @return - L'ajout est-il dans le sens vertical ?
	 * @throws WordPlacementException - État des lettre incohérent. Encore une tentative de triche...
	 * XXX Pas testé!
	 */
	public boolean isPropositionVertical(HashMap<Character, Pair<Integer, Integer>> newLetters) throws WordPlacementException {
		boolean sameX = true;
		boolean sameY = true;
		int lastX = -1;
		int lastY = -1;
		Pair<Integer, Integer> currentPair;
		
		for (Character c : newLetters.keySet()) {
			currentPair = newLetters.get(c);
			
			if (lastX == -1)
				lastX = currentPair.getKey();
			else if (lastX != currentPair.getKey())
				sameX = false;
			
			if (lastY == -1)
				lastY = currentPair.getValue();
			else if (lastY != currentPair.getValue())
				sameY = false;
			
		}
		
		
		if (!sameX && !sameY)
			throw new WordPlacementException(Why.INVALID_PROPOSITION);
		
		return (sameX) ? false : true;
	}
	
	/**
	 * Vérifie si les lettres ajoutées par la proposition sont dans le draw actuel.
	 * @param letters - Lettres ajoutées par la proposition.
	 * @return - L'ajout de ces lettres est-il possible ?
	 */
	public boolean isProposedLettersValid(HashMap<Character, Pair<Integer, Integer>> letters) {
		ArrayList<Character> lazyList = new ArrayList<Character>();
		lazyList.addAll(currentDraw);
		
		for (Character c : letters.keySet()) {
			if (lazyList.contains(c)) 
				lazyList.remove(c);
			else
				return false;
		}
		
		return true; 
	}
	
	private boolean validateMultipleWords(ArrayList<String> words) {
		return words.stream().allMatch(w -> wordChecker.isWordValid(w));
	}
	
	public void drawLetters() throws EmptyPouchException {
		currentDraw.clear();
		currentDraw.addAll(pouch.pickLetters(DRAW_SIZE));
		
		lazyCurrentDraw = "";
		for (Character c : currentDraw) 
			lazyCurrentDraw += c;
		
	}
	
	public String getLazyCurrentDraw() {
		return lazyCurrentDraw;
	}
	
	// XXX pour debug, rien d'autre!
	public void setBoard(char[][] board) {
		this.board = board;
	}
	
	
	
	
	
	/**
	 * Convertie une String en tableau de char.
	 * @param rawProposition - Chaine à convertir.
	 * @return - Conversion de la chaine.
	 * @throws WordPlacementException - Tentative de triche ou erreur du client.
	 */
	public static char[][] toArray(String rawProposition) throws WordPlacementException {
		if (rawProposition.length() != BOARD_SIZE * BOARD_SIZE) 
			throw new WordPlacementException(Why.INVALID_PROPOSITION);
		
		char[][] result = new char[BOARD_SIZE][BOARD_SIZE];
		
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (isValidChar(rawProposition.charAt(i * j))) 
					result[i][j] = rawProposition.charAt(i * j);
				else
					throw new WordPlacementException(Why.INVALID_PROPOSITION);
			}
		}
		
		return result;
	}
	
	public static boolean isValidChar(char c) {
		return c == NULL_CHAR || (c >= 'A' && c <= 'Z');
	}
	
	public static void main(String[] args) {
		Scrabble s = new Scrabble(new RandomPouch(42), null);
		
		char[][] b1 = new char[][]{{'0', '0', '0'}, {'A', 'B', 'C'}, {'0', '0', '0'}};
		char[][] b2 = new char[][]{{'0', '0', '0'}, {'0', '0', '0'}, {'0', '0', '0'}};
		
		s.setBoard(b2);
		
		System.out.println(s.isPropositionValid(b1));

		
	}
}
