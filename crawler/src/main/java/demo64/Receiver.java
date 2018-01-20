package demo64;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver implements Runnable {
	
	private DatagramSocket datagramSocket;
	private ExecutorService executorService;
	private byte[] buffer;
	private volatile boolean running;
	
	public Receiver(DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
		executorService = Executors.newSingleThreadExecutor();
		buffer = new byte[2048];
	}
	
	public void start() {
		running = true;
		executorService.submit(this);
	}
	
	public void shutdown() {
		running = false;
		executorService.shutdown();
	}

	@Override
	public void run() {
		while(running) {
			try {
				DatagramPacket datagramPacket = receive();
				//处理数据
				System.out.println(datagramPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private DatagramPacket receive() throws IOException {
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		datagramSocket.receive(datagramPacket);
		return datagramPacket;
	}
	
	

}
