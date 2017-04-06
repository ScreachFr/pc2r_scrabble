import java.io.IOException;
import java.net.ServerSocket;

import core.MotherBrain;


public class Main {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Pas assez d'arguments.");
			System.out.println("cmd <port:int>");
			return;
		}
		
		
		int port = Integer.parseInt(args[0]);
		
		try {
			System.out.println("Launching server....");
			ServerSocket socket;
			socket = new ServerSocket(port);
			MotherBrain mb = new MotherBrain(8, socket);
			Thread threadMB = new Thread(mb);
			threadMB.start();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
