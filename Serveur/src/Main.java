import java.io.IOException;
import java.net.ServerSocket;

import core.MotherBrain;


public class Main {

	public static void main(String[] args) {
		try {
			ServerSocket socket;
//			socket = new ServerSocket((int) (Math.random() * Byte.MAX_VALUE) + 1000);
			socket = new ServerSocket(42666);
			MotherBrain mb = new MotherBrain(8, socket);
			Thread threadMB = new Thread(mb);
			threadMB.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
