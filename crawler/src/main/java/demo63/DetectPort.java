package demo63;

import java.io.IOException;
import java.net.ServerSocket;

public class DetectPort {

	public static void main(String[] args) {
		int port = 0;
		while(port < 1024) {
			try (ServerSocket serverSocket = new ServerSocket(port)){
				System.out.println("端口:"+port+"没有占用");
			} catch (IOException e) {
				System.out.println("端口:"+port+"被占用");
			}
			port++;
		}

	}

}
