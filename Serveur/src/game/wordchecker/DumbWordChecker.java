package game.wordchecker;

import game.WordChecker;

public class DumbWordChecker implements WordChecker {

	@Override
	public boolean isWordValid(String word) {
		return true; // Plus teubé que ça tu meurs.
	}

}
