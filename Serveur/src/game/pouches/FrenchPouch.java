package game.pouches;

import java.util.HashMap;

import game.Pouch;

public class FrenchPouch extends Pouch {
	private HashMap<Character, Integer> letterEff;
	
	public FrenchPouch() {
		super(102);
		
		letterEff = new HashMap<Character, Integer>();
		
		letterEff.put('A', 9);
		letterEff.put('B', 2);
		letterEff.put('C', 2);
		letterEff.put('D', 3);
		letterEff.put('E', 15);
		letterEff.put('F', 2);
		letterEff.put('G', 2);
		letterEff.put('H', 2);
		letterEff.put('I', 8);
		letterEff.put('J', 1);
		letterEff.put('K', 1);
		letterEff.put('L', 5);
		letterEff.put('M', 3);
		letterEff.put('N', 6);
		letterEff.put('O', 6);
		letterEff.put('P', 2);
		letterEff.put('Q', 1);
		letterEff.put('R', 6);
		letterEff.put('S', 6);
		letterEff.put('T', 6);
		letterEff.put('U', 6);
		letterEff.put('V', 2);
		letterEff.put('W', 1);
		letterEff.put('X', 1);
		letterEff.put('Y', 1);
		letterEff.put('Z', 1);
		
		
	}
	
	
	@Override
	public void resetLetters() {
		letterEff.keySet().forEach(k -> {
			for (int i = 0; i < letterEff.get(k); i++) {
				letters.add(k);
			}
		});
	}

}
