import java.io.IOException;
import java.net.ServerSocket;

import core.MotherBrain;


public class Main {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Pas assez d'arguents.");
			System.out.println("cmd <port:int>");
		}
		
		
		int port = Integer.parseInt(args[0]);
		
		try {
			ServerSocket socket;
			socket = new ServerSocket(port);
			MotherBrain mb = new MotherBrain(8, socket);
			Thread threadMB = new Thread(mb);
			threadMB.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
