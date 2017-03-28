package core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MotherBrain implements Runnable {
	
	private ExecutorService pool;
	private int playerLimit;
	private ServerSocket socket;
	private ThreadBrodcaster broadcaster;
	private Thread threadBroadcaster;
	private ArrayList<ThreadClient> threadsClient;
	
	public MotherBrain(int playerLimit, ServerSocket socket) {
		this.playerLimit = playerLimit;
		this.socket = socket;
		this.pool = Executors.newFixedThreadPool(playerLimit);
		this.threadsClient = new ArrayList<ThreadClient>();
		this.broadcaster = new ThreadBrodcaster(threadsClient);
		threadBroadcaster = new Thread(broadcaster);
	}

	@Override
	public void run() {
		threadBroadcaster.start();
		
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
					System.out.println("Ajout threadClient");
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

	public synchronized boolean reserverUsername(String username, ThreadClient threadClient) {
		for (ThreadClient tc : threadsClient) {
			if (username.equals(tc.getUsername()))
				return false;
		}
		
		threadClient.setUsername(username);
		return true;
	}

	public String[] etatSession() {
		// TODO Auto-generated method stub
		String session[] = {"TODO" };
		return session;
	}

	public void signalNouveauUser(String username) {
		broadcaster.broadcast("CONNECTE", username);
	}

	public void deconnexionClient(ThreadClient threadClient, String username) {
		broadcaster.broadcast("DECONNEXION", username);
		threadsClient.remove(threadClient);
	}

	public void envoiMessage(String msg, String username) {
		broadcaster.broadcast("RECEPTION", msg, username);
	}

	public void envoiMessagePrive(String msg, String emetteur, String destinataire) {
		broadcaster.broadcastPrive("PRECEPTION", destinataire, new String[]{msg, emetteur});
	}
	
}
