package game;

public class ProposedLetter {
	private Character letter;
	private int x, y;
	
	public ProposedLetter(Character letter, int x, int y) {
		this.letter = letter;
		this.x = x;
		this.y = y;
	}

	public Character getLetter() {
		return letter;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
}
