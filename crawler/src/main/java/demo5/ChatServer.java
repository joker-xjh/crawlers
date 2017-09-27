package demo5;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatServer  extends Thread{
	
	public static final int PORT = 8888;
	private static final int BUFFER = 1024;
	
	private DatagramSocket socket;
	
	private List<InetAddress> clientAddresses;
	
	private List<Integer> clientPorts;
	
	private Set<String> existingClient;
	
	public ChatServer() throws SocketException {
		clientAddresses = new ArrayList<>();
		clientPorts = new ArrayList<>();
		existingClient = new HashSet<>();
		socket = new DatagramSocket();
	}
	
	@Override
	public void run() {
		byte[] buff = new byte[BUFFER];
		while(true) {
			try {
				Arrays.fill(buff, (byte)0);
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				socket.receive(packet);
				String content = new String(buff,0, packet.getLength() ,"UTF-8");
				InetAddress clientAddress = packet.getAddress();
				int clientPort = packet.getPort();
				String id = clientAddress.toString() +","+clientPort;
				if(!existingClient.contains(id)) {
					existingClient.add(id);
					clientAddresses.add(clientAddress);
					clientPorts.add(clientPort);
				}
				System.out.println(id + " : " + content);
				byte[] data = (id + " : " + content).getBytes("UTF-8");
				for(int i=0; i<clientAddresses.size(); i++) {
					InetAddress address = clientAddresses.get(i);
					int port = clientPorts.get(i);
					packet = new DatagramPacket(data, data.length, address, port);
					socket.send(packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new ChatServer().start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

}
