package game;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ScoreByLetterFr { // On pourra mettre une interface si on veut, pour pouvoir jouer dans d'autres langues facilement

	private static final Map<Character, Integer> scoreLetterMap;
    static {
        Map<Character, Integer> aMap = new TreeMap<>(); // TODO : remplacer par une structure prenant en compte les fréquences d'usage des lettres en français
        aMap.put('A', 1);								// pondérer par la fréquence de tirage initiale de la lettre K
        aMap.put('B', 3);
        aMap.put('C', 3);
        aMap.put('D', 2);
        aMap.put('E', 1);
        aMap.put('F', 4);
        aMap.put('G', 2);
        aMap.put('H', 4);
        aMap.put('I', 1);
        aMap.put('J', 8);
        aMap.put('K', 10);
        aMap.put('L', 1);
        aMap.put('M', 2);
        aMap.put('N', 1);
        aMap.put('O', 1);
        aMap.put('P', 3);
        aMap.put('Q', 8);
        aMap.put('R', 1);
        aMap.put('S', 1);
        aMap.put('T', 1);
        aMap.put('U', 1);
        aMap.put('V', 4);
        aMap.put('W', 10);
        aMap.put('X', 10);
        aMap.put('Y', 10);
        aMap.put('Z', 10);
        scoreLetterMap = Collections.unmodifiableMap(aMap);
    }
	
	public static int letterPoints(char letter) {
		return scoreLetterMap.get(letter);
	}
}
