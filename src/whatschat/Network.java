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
	
	private JedisDB jedis = new JedisDB();
	
	public void connectBroadcast() {
		try {
			broadcastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
			broadcastSocket = new MulticastSocket(PORT);
			broadcastSocket.joinGroup(broadcastGroup);
			System.out.println("Connected to Broadcast");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendBroadcastMessage(String msg) {
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, broadcastGroup, PORT);
			broadcastSocket.send(dgpSend);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public MulticastSocket getBroadcastSocket() {
		return broadcastSocket;
	}
	
	public void connectChatGroup(String ip) {
		try {
			chatGroup = InetAddress.getByName(ip);
			chatSocket = new MulticastSocket(PORT);
			chatSocket.joinGroup(chatGroup);
			System.out.println("Connected to Chat Group " + chatGroup.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendChatMessage(String msg, String name) {
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, chatGroup, PORT);
			chatSocket.send(dgpSend);
			jedis.addMessage(name, msg);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public MulticastSocket getChatSocket() {
		return chatSocket;
	}
}
