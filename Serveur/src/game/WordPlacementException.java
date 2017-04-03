package game;

public class WordPlacementException extends Exception {
	private static final long serialVersionUID = 2300881216568196378L;
	
	private Why why;
	
	public WordPlacementException(Why why) {
		this.why = why;
	}
	
	public Why getWhy() {
		return why;
	}
	
	
	public enum Why {
		INVALID_BOARD, // Tentative d'arnaque. On a changé des lettres.
		INVALID_PROPOSITION, // Proposition au mauvais format.
		WRONG_LETTERS, // Tentative d'arnaque. On a essayé d'utiliser des lettres qui ne sont pas en nombre suffisant dans le draw actuel.
		WRONG_WORD, // Un des mots créés n'existe pas
		OTHER; // Parce que.
	}
}
