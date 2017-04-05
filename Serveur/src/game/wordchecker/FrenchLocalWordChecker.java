package game.wordchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;

import game.WordChecker;

public class FrenchLocalWordChecker implements WordChecker {
	private final static String path = "dico/ODS5.txt";

	private HashSet<String> cache;

	public FrenchLocalWordChecker() throws FileNotFoundException {
		cache = new HashSet<String>();

		loadDico();
	}


	private void loadDico() throws FileNotFoundException {
		Scanner sc = new Scanner(new BufferedReader(new FileReader(new File(path))));

		while(sc.hasNextLine())  {
			cache.add(sc.nextLine());
		}

		sc.close();
	}

	@Override
	public boolean isWordValid(String word) {
		return cache.contains(word);
	}

}
