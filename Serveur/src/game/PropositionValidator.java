package game;

import game.exceptions.WordPlacementException;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class PropositionValidator implements Runnable {
	private Lock lock;
	private Condition hasToValidate;

	private Scrabble scrabble;
	private ArrayList<Proposition> toValidate;

	
	
	public PropositionValidator(Scrabble scrabble) {
		lock = new ReentrantLock();
		hasToValidate = lock.newCondition();
		toValidate = new ArrayList<>();
		this.scrabble = scrabble;
	}
	

	public void submit(Proposition propositionToValidate) {
		toValidate.add(propositionToValidate);
		lock.lock();
		try {
			hasToValidate.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void run() {
		Proposition currentProposition;
		
		while(true) { //TODO
			lock.lock();
			try {
				if (!toValidate.isEmpty()) {
					currentProposition = toValidate.remove(0);
					try {
						scrabble.proposeBoard(currentProposition);
						
						currentProposition.getClient().envoyerMessage("RVALIDE");
					} catch (WordPlacementException e) {
						currentProposition.getClient().envoyerMessage("RINVALIDE", e.getWhy()+"");
					}
					
					//Do stuff here.
				} else {
					try {
						hasToValidate.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}

}
