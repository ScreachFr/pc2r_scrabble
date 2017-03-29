package game.pouches;

import game.Pouch;

public class RandomPouch extends Pouch {

	//XXX : NE PAS JOUER AVEC CE POUCH. Enfin si ton délire c'est de jouer au scrabble
	// ou encore pire jouer au scrabble avec que des consonnes...
	
	
	public RandomPouch(int size) {
		super(size);
	}
	
	@Override
	public void resetLetters() {
		letters.clear();
		for(int i = 0; i < getSize(); i++) {
			letters.add(new Character((char) (rng.nextInt('Z' - 'A') + 'A')));
		}
	}
	
}
