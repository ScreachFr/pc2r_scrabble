package game;

import game.Pouch.EmptyPouchException;
import game.WordPlacementException.Why;
import game.pouches.RandomPouch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
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
	 * 		proposé ou les nouveaux mots qu'il engendre n'existe pas. Ou alors ta tentative de triche viens
	 * 		de lamentablement échouer. 
	 */
	public void propose(String proposedBoard) throws WordPlacementException {
		char[][] proposition = toArray(proposedBoard);
		
		if (!isPropositionValid(proposition))
			throw new WordPlacementException(Why.INVALID_PROPOSITION);
		
		ArrayList<ProposedLetter> newLetters = findNewLetters(proposition);
		
		// Proposition sans nouvelle lettre.
		if (newLetters.isEmpty())
			throw new WordPlacementException(Why.INVALID_PROPOSITION);
		
		boolean isVertical = isPropositionVertical(newLetters);
		
		if (!checkHoles(newLetters, proposition, isVertical)) 
			throw new WordPlacementException(Why.INVALID_PROPOSITION);
		
		
		
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
	public ArrayList<ProposedLetter> findNewLetters(char[][] proposition) {
		ArrayList<ProposedLetter> result = new ArrayList<>();
		
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (board[i][j] == NULL_CHAR && proposition[i][j] != NULL_CHAR)
					result.add(new ProposedLetter(proposition[i][j], i, j));
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
	public boolean isPropositionVertical(ArrayList<ProposedLetter> newLetters) throws WordPlacementException {
		boolean sameX = true;
		boolean sameY = true;
		int lastX = -1;
		int lastY = -1;
		
		for (ProposedLetter c : newLetters) {
			if (lastX == -1)
				lastX = c.getX();
			else if (lastX != c.getX())
				sameX = false;
			
			if (lastY == -1)
				lastY = c.getY();
			else if (lastY != c.getY())
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
	
	/**
	 * Vérifie s'il y a des trous dans les mots qui sont engendrés par les nouvelles lettres.
	 * @param letters - Lettres proposées par le client.
	 * @return - Le placement est-il valide ?
	 * XXX pas testée.
	 */
	private boolean checkHoles(ArrayList<ProposedLetter> letters, char[][] proposedBoard, boolean direction) {
		int min = Integer.MAX_VALUE, max = -1;
		
		if (direction) { // Vertical
			
			for (ProposedLetter p : letters) {
				if (p.getY() > max)
					max = p.getY();
				if (p.getY() < min)
					min = p.getY();
			}
			
			int constX = letters.get(0).getX();
			
			
			for (int i = min; i < max; i++) {
				if (proposedBoard[constX][i] == NULL_CHAR)
					return false;
			}
			
		} else { // Horizontal
			for (ProposedLetter p : letters) {
				if (p.getX() > max)
					max = p.getX();
				if (p.getX() < min)
					min = p.getX();
			}
			
			int constY = letters.get(0).getY();
			
			
			for (int i = min; i < max; i++) {
				if (proposedBoard[i][constY] == NULL_CHAR)
					return false;
			}
		
		}
		
		return true;
	}
	
	
	public ArrayList<Pair<String, ProposedLetter[]>> findNewWords(ArrayList<ProposedLetter> newLetters, boolean direction, char[][] proposedBoard) {
		ArrayList<Pair<String, ProposedLetter[]>> newWords = new ArrayList<>();
		int min = Integer.MAX_VALUE, max = -1;
		if (direction) {
			for (ProposedLetter p : newLetters) {
				if (p.getY() > max)
					max = p.getY();
				if (p.getY() < min)
					min = p.getY();
			}
			int constX = newLetters.get(0).getX();
			for (ProposedLetter letter : newLetters) {
				if (constX != 0 && board[constX - 1][letter.getY()] != NULL_CHAR ||
						constX != BOARD_SIZE - 1 && board[constX + 1][letter.getY()] != NULL_CHAR) {
					ProposedLetter[] l = { letter };
					newWords.add(new Pair<String, ProposedLetter[]>(findHorizontalWord(l, letter.getY()), l));
				}
			}
			newWords.add(new Pair<String, ProposedLetter[]>(findVerticalWord(
					(ProposedLetter[]) newLetters.toArray(new ProposedLetter[newLetters.size()]), x, y)), constX), newLetters));
		}
		return newWords;
	}
	
	private String findVerticalWord(ProposedLetter[] l, int x) {
		String newWord = "";
		boolean isNewWord = false;
		ProposedLetter curLetter;
		for (int y = 0 ; y < BOARD_SIZE ; y++) {
			curLetter = findLetter(l, x, y);
			if (board[x][y] == NULL_CHAR && curLetter == null) {
				if (isNewWord)
					return newWord;
				newWord = "";
			}
			else if (board[x][y] != NULL_CHAR)
				newWord += board[x][y];
			else {
				newWord += curLetter.getLetter();
				isNewWord = true;
			}
		}
		return newWord;
	}

	private String findHorizontalWord(ProposedLetter[] l, int y) {
		String newWord = "";
		boolean isNewWord = false;
		ProposedLetter curLetter;
		for (int x = 0 ; x < BOARD_SIZE ; x++) {
			curLetter = findLetter(l, x, y);
			if (board[x][y] == NULL_CHAR && curLetter == null) {
				if (isNewWord)
					return newWord;
				newWord = "";
			}
			else if (board[x][y] != NULL_CHAR)
				newWord += board[x][y];
			else {
				newWord += curLetter.getLetter();
				isNewWord = true;
			}
		}
		return newWord;
	}
	
	private ProposedLetter findLetter(ProposedLetter[] l, int x, int y) {
		for (ProposedLetter proposedLetter : l) {
			if (proposedLetter.getX() == x && proposedLetter.getY() == y)
				return proposedLetter;
		}
		return null;
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
		ArrayList<ProposedLetter> letters = new ArrayList<ProposedLetter>();
		
		letters.add(new ProposedLetter('A', 1, 3));
		letters.add(new ProposedLetter('B', 5, 3));
		letters.add(new ProposedLetter('C', 3, 30));
		letters.add(new ProposedLetter('D', 1, 3));
		
		
	}
}
