package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class DataLinkHandler implements Runnable {
	
	private ServerSocket dataServerSocket;
	
	private Scanner scanner;
	
	public DataLinkHandler(int port) {
		try {
			dataServerSocket = new ServerSocket(port);
			System.out.println("数据连接已经在 " + dataServerSocket.getInetAddress() +
					" 端口: " + dataServerSocket.getLocalPort()+"开启");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true) {
			Socket dataSocket = null;
			Socket ctrlSocket = null;
			try {
				dataSocket = dataServerSocket.accept();
				System.out.println("数据连接 接收到 来自 客户端 " + dataSocket.getInetAddress() +
						" 端口: " + dataSocket.getPort()+"的连接请求");
				dataSocket.setSoTimeout(5000);
				scanner = new Scanner(dataSocket.getInputStream());
				long id = scanner.nextLong();
				ctrlSocket = IPManger.connetingMap.get(id);
				if(ctrlSocket == null) {
					System.out.println("客户端并未连接到服务端的控制连接 " +
							dataSocket.getInetAddress() + " 端口: " + dataSocket.getPort());
					dataSocket.close();
				}
				else {
					System.out.println("客户端数据连接成功 " +
							dataSocket.getInetAddress() + " 端口 " + dataSocket.getPort());
					new Thread(new ServerSession(ctrlSocket, dataSocket));
				}
				scanner.close();
			} catch (IOException e) {
				System.out.println("数据连接出错");
				e.printStackTrace();
			}
		}
	}

}
