package core;

import game.Proposition;
import game.Scrabble;
import game.exceptions.PropositionException;
import game.pouches.FrenchPouch;
import game.pouches.RandomPouch;
import game.wordchecker.DumbWordChecker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MotherBrain implements Runnable {
	private ExecutorService pool;
	private int playerLimit;
	private ServerSocket socket;
	private ThreadBrodcaster broadcaster;
	private Thread threadBroadcaster;
	private ArrayList<ThreadClient> threadsClient;

	private Scrabble scrabble;

	private Lock clientListLock;
	private int playerCount;
	
	private boolean useOnlineDico;

	public MotherBrain(int playerLimit, ServerSocket socket) {
		this.playerLimit = playerLimit;
		this.socket = socket;
		this.pool = Executors.newFixedThreadPool(playerLimit);
		this.threadsClient = new ArrayList<ThreadClient>();
		this.broadcaster = new ThreadBrodcaster(threadsClient);
		threadBroadcaster = new Thread(broadcaster);
		clientListLock = new ReentrantLock();
		playerCount = 0;
		
		useOnlineDico = false;
	}
	
	public MotherBrain(int playerLimit, ServerSocket socket, boolean userOnlineDico) {
		this(playerLimit, socket);
		this.useOnlineDico = userOnlineDico;
	}
	

	@Override
	public void run() {
		threadBroadcaster.start();
		initScrabble();

		Socket crtSocket;
		while (true) { //TODO
			try {
				crtSocket = socket.accept();

				if (threadsClient.size() >= playerLimit) {
					refuserClient(crtSocket);
				} else {
					ThreadClient client = new ThreadClient(crtSocket, this);
					threadsClient.add(client);
					pool.execute(client);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void refuserClient(Socket socket) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());

			out.println("REFUS/");
			out.flush();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean reserverUsername(String username, ThreadClient threadClient) {
		clientListLock.lock();
		try {
			for (ThreadClient tc : threadsClient) {
				if (username.equals(tc.getUsername()))
					return false;
			}

			if (!scrabble.isUserNameAvailable(username))
				return false;

			if (!scrabble.hasPlayers()) 
				scrabble.startGame();

			scrabble.addPlayer(username);

			threadClient.setUsername(username);
			playerCount++;

			return true;
		} finally {
			clientListLock.unlock();
		}
	}

	public boolean hasActiveClient() {
		clientListLock.lock();
		try {
			return playerCount >= 1;
		} finally {
			clientListLock.unlock();
		}

	}
	
	public int getActuveClient() {
		clientListLock.lock();
		try {
			return playerCount;
		} finally {
			clientListLock.unlock();
		}
	}

	public void initScrabble() {
		scrabble = new Scrabble(new FrenchPouch(), new DumbWordChecker(), this);
	}

	public synchronized String[] sessionState() {
		return scrabble.getCompleteGameState();
	}

	public void signalNouveauUser(String username) {
		broadcaster.broadcast("CONNECTE", username);
	}

	public void deconnexionClient(ThreadClient threadClient, String username) {
		broadcaster.broadcast("DECONNEXION", username);
		clientListLock.lock();
		try {
			threadsClient.remove(threadClient);

			scrabble.removePlayer(username);
			playerCount--;
			if (playerCount == 0) 
				scrabble.stopGame();
		} finally {
			clientListLock.unlock();
		}
	}

	public void envoiMessage(String msg, String username) {
		broadcaster.broadcast("RECEPTION", msg, username);
	}

	public void envoiMessagePrive(String msg, String emetteur, String destinataire) {
		broadcaster.broadcastPrive("PRECEPTION", destinataire, new String[]{msg, emetteur});
	}

	public void submitProposition(ThreadClient client, String propositionString) throws PropositionException {
		Proposition p = new Proposition(client, propositionString, System.currentTimeMillis());

		scrabble.submitProposition(p);
	}

	public void broadcastFinRecherche() {
		broadcaster.broadcast("RFIN");
	}

	public void broadcastFinSoumission() {
		broadcaster.broadcast("SFIN");
	}

	public void broadcastNewSession() {
		broadcaster.broadcast("SESSION");
	}

	public void broadcastNewRound(String board, String draw) {
		broadcaster.broadcast("TOUR", board, draw);
	}

	//RATROUVE/user/
	public void broadcastRATROUVE(String user) {
		broadcaster.broadcast("RATROUVE", user);
	}


	//BILAN/mot/vainqueur/scores/
	public void broadcastBilan(String mot, String vainqueur, String scores) {
		broadcaster.broadcast("BILAN", mot, vainqueur, scores);
	}


}
