package core;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadBrodcaster implements Runnable {
	private Lock lock;
	private Condition hasToSend;

	private ArrayList<ThreadClient> threadsClient;
	private ArrayList<Message> msgsToSend;

	public ThreadBrodcaster(ArrayList<ThreadClient> threadsClient) {
		this.threadsClient = threadsClient;
		this.msgsToSend = new ArrayList<Message>();
		lock = new ReentrantLock();
		hasToSend = lock.newCondition();
	}

	public void broadcast(String domaine, String...args) {
		msgsToSend.add(new Message(domaine, args));
		lock.lock();
		try {
			hasToSend.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void run() {
		while(true) {
			lock.lock();
			try {
				if (!msgsToSend.isEmpty()) {
					sendMessage();
				} else {
					try {
						hasToSend.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}

	private void sendMessage() {
		Message toSend = msgsToSend.remove(0);
		if (toSend == null)
			return;
		
		if (toSend.isPrivate()) {
			for (ThreadClient tc : threadsClient) {
				if (tc.isConnected() && tc.getUsername().equals(toSend.getDest()))
					tc.envoyerMessage(toSend.getDomaine(), toSend.getArgs());
			}

		} else {
			for (ThreadClient tc : threadsClient) {
				if (tc.isConnected())
					tc.envoyerMessage(toSend.getDomaine(), toSend.getArgs());
			}
		}
	}

	public void broadcastPrive(String domaine, String destinataire, String[] content) {
		msgsToSend.add(new Message(domaine, destinataire, content));
		lock.lock();
		try {
			hasToSend.signalAll();
		} finally {
			lock.unlock();
		}
	}


}
