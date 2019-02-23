package whatschat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Network {

	private final String MULTICAST_ADDRESS = "230.1.1.1";
	private final int PORT = 6789;
	
	private MulticastSocket broadcastSocket = null;
	private InetAddress broadcastGroup = null;
	
	private MulticastSocket chatSocket = null;
	private InetAddress chatGroup = null;
	
	public void connectBroadcast() {
		try {
			broadcastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
			broadcastSocket = new MulticastSocket(PORT);
			broadcastSocket.joinGroup(broadcastGroup);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void connectChat(String ip) {
		try {
			chatGroup = InetAddress.getByName(ip);
			chatSocket = new MulticastSocket(PORT);
			chatSocket.joinGroup(chatGroup);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public MulticastSocket getBroadcastSocket() {
		return broadcastSocket;
	}
	
	public void sendBroadcastMessage(String msg) {
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, broadcastGroup, PORT);
			broadcastSocket.send(dgpSend);
			System.out.println("Broadcast " + msg);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendChatMessage(String msg) {
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, broadcastGroup, PORT);
			broadcastSocket.send(dgpSend);
			System.out.println("Chat " + msg);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
