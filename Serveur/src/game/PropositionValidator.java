package game;

import game.Scrabble.Phase;
import game.exceptions.WordPlacementException;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import core.MotherBrain;


public class PropositionValidator implements Runnable {
	private Lock lock;
	private Condition hasToValidate;

	private Scrabble scrabble;
	private ArrayList<Proposition> toValidate;

	private MotherBrain motherBrain;
	
	
	public PropositionValidator(Scrabble scrabble, MotherBrain motherBrain) {
		lock = new ReentrantLock();
		hasToValidate = lock.newCondition();
		toValidate = new ArrayList<>();
		this.scrabble = scrabble;
		this.motherBrain = motherBrain;
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
		
		while(true) {
			lock.lock();
			try {
				if (!toValidate.isEmpty()) {
					currentProposition = toValidate.remove(0);
					try {
						scrabble.getGameStateLock().lock();
						scrabble.proposeBoard(currentProposition);
						if (scrabble.getCurrentPhase() == Phase.REC) {
							currentProposition.getClient().sendRVALIDE();
							scrabble.interruptGameLoop();
							
						} else if (scrabble.getCurrentPhase() == Phase.SOU) {
							currentProposition.getClient().sendSVALIDE();
							if (motherBrain.getActiveClients() == scrabble.getPropositionCount()) 
								scrabble.interruptGameLoop();
						}
					} catch (WordPlacementException e) {
						currentProposition.getClient().envoyerMessage("RINVALIDE", e.getMessage() + ":" + e.getWhy()+"");
					} finally {
						scrabble.getGameStateLock().unlock();
					}
					
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
