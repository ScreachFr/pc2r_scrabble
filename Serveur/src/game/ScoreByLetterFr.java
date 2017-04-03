package game;

public class ScoreByLetterFr { // On pourra mettre une interface si on veut, pour pouvoir jouer dans d'autres langues facilement

	public static int letterPoints(char letter) {
		switch (letter) {
			case 'A':
				return 1;
			case 'B':
				return 2; // To Be Continued
		}
		return 0; // ne devrait pas arriver...
	}
}
