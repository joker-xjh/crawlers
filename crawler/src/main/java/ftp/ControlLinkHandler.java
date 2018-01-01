package ftp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ControlLinkHandler implements Runnable{
	
	private static Random random;
	
	static {
		random = new Random();
	}
	
	private ServerSocket controlServerSocket;
	
	private PrintWriter writer;
	
	public ControlLinkHandler(int port) throws IOException {
		controlServerSocket = new ServerSocket(port, 10);
		System.out.println("控制连接已经建立在:"+controlServerSocket.getInetAddress()+" 端口:"+controlServerSocket.getLocalPort());
	}
	
	

	@Override
	public void run() {
		while(true) {
			Socket socket = null;
			try {
				socket = controlServerSocket.accept();
				System.out.println("控制连接 接收到 来自 客户端"
						+ socket.getInetAddress() + " 端口: " + socket.getPort()+"的连接请求");
				socket.setSoTimeout(5000);
				writer = new PrintWriter(socket.getOutputStream(), true);
				Object[] args = new Object[2];
				if(IPManger.canConnect(socket, args)) {
					long id = random.nextLong();
					writer.println(true);
					writer.println(id);
					IPManger.connetingMap.put(id, socket);
				}
				else {
					writer.println(false);
					writer.println(args[0].toString());
					writer.println(args[1].toString());
					socket.close();
				}
				
			} catch (IOException e) {
				throw new IllegalStateException("客户端连接时出现IO错误:" + e);
			}
		}
		
	}

}
