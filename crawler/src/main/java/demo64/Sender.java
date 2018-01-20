package demo64;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
	
	private DatagramSocket datagramSocket;
	private InetAddress inetAddress;
	private int port;
	private byte[] data;
	
	public Sender(DatagramSocket datagramSocket, InetAddress inetAddress, int port, byte[] data) {
		this.datagramSocket = datagramSocket;
		this.inetAddress = inetAddress;
		this.port = port;
		this.data = data;
	}
	
	public void send() throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
		datagramSocket.send(packet);
	}

}
