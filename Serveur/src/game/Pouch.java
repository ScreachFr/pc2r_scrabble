package game;

import java.util.ArrayList;
import java.util.Random;

public abstract class Pouch {
	protected Random rng;
	protected ArrayList<Character> letters;
	private int size;
	public Pouch(int size) {
		letters = new ArrayList<Character>();
		rng = new Random();
		resetLetters();
		
		this.size = size;
	}
	
	
	public ArrayList<Character> pickLetters(int drawSize) throws EmptyPouchException {
		if (drawSize > letters.size())
			throw new EmptyPouchException();
		
		ArrayList<Character> result = new ArrayList<Character>();
		
		for (int i = 0; i < drawSize; i++) {
			result.add(letters.remove(rng.nextInt(letters.size())));
		}
		return result;
	}
	
	public String easyLetterPick(int drawSize) throws EmptyPouchException {
		ArrayList<Character> draw = pickLetters(drawSize);
		String result = "";
		
		for (Character c : draw) {
			result += c;
		}
		
		return result;
	}
	
	public void putLettersBack(ArrayList<Character> letters) {
		this.letters.addAll(letters);
	}
	
	public int getSize() {
		return size;
	}
	
	public abstract void resetLetters();
	
	public class EmptyPouchException extends Exception {
		private static final long serialVersionUID = -2607701448148463946L;
	}
	
}
