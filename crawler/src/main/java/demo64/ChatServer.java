package demo64;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatServer extends Thread {
	
	public static final int PORT = 6666;
	private static final int BUFF_SIZE = 1024;
	private DatagramSocket socket;
	private List<InetAddress> inetAddresses;
	private List<Integer> ports;
	private Set<String> users;
	
	public ChatServer() throws SocketException {
		socket = new DatagramSocket(PORT);
		inetAddresses = new ArrayList<>();
		ports = new ArrayList<>();
		users = new HashSet<>();
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[BUFF_SIZE];
		while(true) {
			try {
				Arrays.fill(buffer, (byte)0);
				DatagramPacket packet = new DatagramPacket(buffer, BUFF_SIZE);
				socket.receive(packet);
				
				String content = new String(packet.getData(), 0, packet.getLength());
				InetAddress inetAddress = packet.getAddress();
				String id = inetAddress.toString() + ":" + packet.getPort();
				
				if(!users.contains(id)) {
					users.add(id);
					inetAddresses.add(inetAddress);
					ports.add(packet.getPort());
				}
				
				System.out.println(id+":"+content);
				
				byte[] data = (id + ":" + content).getBytes();
				for(int i=0; i<inetAddresses.size(); i++) {
					packet = new DatagramPacket(data, data.length, inetAddresses.get(i), ports.get(i));
					socket.send(packet);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	public static void main(String[] args) {
		ChatServer server = null;
		try {
			server = new ChatServer();
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		server.start();
	}
	
	
	
	
	
	
	

}
