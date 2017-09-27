package demo5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
	
	public static void main(String[] args) throws SocketException {
		String host = "localhost";
		DatagramSocket socket = new DatagramSocket();
	    MessageReceiver r = new MessageReceiver(socket);
	    MessageSender s = new MessageSender(socket, host);
	    Thread rt = new Thread(r);
	    Thread st = new Thread(s);
	    rt.start(); st.start();
	}

}


class MessageReceiver implements Runnable {
	private DatagramSocket socket;
	private byte[] buff;
	
	public MessageReceiver(DatagramSocket socket) {
		this.socket = socket;
		buff = new byte[1024];
	}

	@Override
	public void run() {
		while(true) {
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			try {
				socket.receive(packet);
				String receive = new String(buff, 0, packet.getLength(), "UTF-8");
				System.out.println(receive);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}









class MessageSender implements Runnable{
	
	public static final int PORT = ChatServer.PORT;
	private DatagramSocket socket;
	
	private String hostName;
	
	
	public MessageSender(DatagramSocket socket, String hostname) {
		this.socket = socket;
		this.hostName = hostname;
	}
	
	public void send(String message) throws Exception {
		InetAddress address = InetAddress.getByName(hostName);
		byte[] data = message.getBytes("UTF-8");
		DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);
		socket.send(packet);
	}
	
	
	

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String line = scanner.nextLine();
			if(line.equals("bye"))
				break;
			try {
				send(line);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		scanner.close();
	}
	
}
