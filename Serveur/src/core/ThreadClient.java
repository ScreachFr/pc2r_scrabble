package core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadClient implements Runnable {
	private static int staticId = 0;
	
	private Socket socket;
	private String username;
	private MotherBrain motherBrain;
	private PrintWriter writer;
	private boolean finie = false;
	private int id;
	
	public ThreadClient(Socket socket, MotherBrain motherBrain) {
		this.socket = socket;
		username = null;
		this.motherBrain = motherBrain;
		id = staticId++;
	}

	@Override
	public void run() {
		System.out.println(socket.getInetAddress() + " : tentative connexion.");
		
		try {
			Scanner scan = new Scanner(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream());
			
			while (!finie) {
				try {
					traiterCommande(scan.nextLine());
					
				} catch (Exception e) { //Deconnecte proprement en cas d'erreur.
					finie = true;
					
					if (isConnected()) {
						motherBrain.deconnexionClient(this, getUsername());
					}
					
				}
			}
			
			scan.close();
			writer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Au revoir " + username);
	}

	private void traiterCommande(String nextLine) {
		String[] args = nextLine.split("/");
		if (args.length == 0) {
			envoyerMessage("ERREUR", "tooFewArgs");
		} else if (!isConnected()) {
			switch (args[0]) {
			case "CONNEXION":
				connexionClient(args);
				break;
			default:
				System.out.println("Commande Inconnue ou non disponible");
			}
		} else {
			switch (args[0]) {
			case "SORT":
				deconnexionClient(args);
				break;
			case "TROUVE":
				propositionMotClient(args);
				break;
			case "ENVOI":
				messageChat(args);
				break;
			case "PENVOI":
				messagePriveChat(args);
				break;
			default:
				System.out.println("Commande Inconnue.");// j'ai du supprimer le smiley, il cassait mon eclipse :(
			}
		}
	}

	private void messagePriveChat(String[] args) {
		if (args.length >= 3) {
			motherBrain.envoiMessagePrive(args[2], username, args[1]);
		}
		else {
			System.out.println("Pas de message a envoye");
			envoyerMessage("ERREUR", "envoi", "tooFewArgs");
		}
	}

	private void messageChat(String[] args) {
		if (args.length >= 2) {
			motherBrain.envoiMessage(args[1], username);
		}
		else {
			System.out.println("Pas de message a envoye");
			envoyerMessage("ERREUR", "envoi", "tooFewArgs");
		}
	}

	private void propositionMotClient(String[] args) {
		// TODO Auto-generated method stub

	}

	private void deconnexionClient(String[] args) {
		if (args.length >= 2 && username.equals(args[1])) {
			motherBrain.deconnexionClient(this, username);
			finie = true;
		}
		else if (args.length < 2) { // protocole = SORT/user
			System.out.println("Deconnexion non prise en compte");
			envoyerMessage("ERREUR", "deconnexion", "tooFewArgs");
		}
		else {
			System.out.println("Deconnexion non prise en compte");
			envoyerMessage("ERREUR", "deconnexion", "mauvaisUsername");
		}
	}

	private void connexionClient(String[] args) {
		System.out.println("Connexion Client");
		if (args.length < 2)  {
			refusConnexion("Pas assez d'arguments");
			return;
		}
		String username = args[1];
		if (motherBrain.reserverUsername(username, this)) {
			bienvenueClient();
			motherBrain.signalNouveauUser(username);
		} else {
			refusConnexion("Erreur lors de la réservation du pseudonyme");
		}
	}

	private void bienvenueClient() {
		String[] pTSPT = motherBrain.sessionState();
		envoyerMessage("BIENVENUE", pTSPT);
	}

	private void refusConnexion() {
		envoyerMessage("REFUS");
	}

	private void refusConnexion(String why) {
		envoyerMessage("REFUS", why);
	}
	
	public synchronized void envoyerMessage(String domaine, String...strings) {
		String chaine = String.join("/", strings);
		if (strings == null || strings.length == 0)
			this.writer.println(domaine + "/");
		else
			this.writer.println(domaine + "/" + chaine + "/");
		writer.flush();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isConnected() {
		return username != null;
	}
	
	public int getId() {
		return id;
	}

}
