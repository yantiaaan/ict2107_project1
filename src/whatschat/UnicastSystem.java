package whatschat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class UnicastSystem extends Thread {
	DatagramSocket myUnicastDS;
	DatagramPacket myUnicastDP;
	
	User user = new User();
	UnicastSystem() {
		start();
	}

	/*public void run() {
		while (true) {
			try {

				byte b[] = new byte[100];
				DatagramPacket myRecivedDP = new DatagramPacket(b, b.length);

				myUnicastDS.receive(myRecivedDP);
				System.out.println(
						"RECEIVED UNICAST :" + new String(myRecivedDP.getData(), 0, myRecivedDP.getLength()));
				String senderID = user.getUser(myRecivedDP.getPort());
				appendTextBox(
						senderID + " Whispers : " + new String(myRecivedDP.getData(), 0, myRecivedDP.getLength()));

			} catch (Exception e) {

			}
		}

	}*/
}