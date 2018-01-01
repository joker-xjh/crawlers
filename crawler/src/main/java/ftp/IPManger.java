package ftp;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IPManger {
	
	public static Map<Long, Socket> connetingMap = new ConcurrentHashMap<>();
	
	public static boolean canConnect(Socket socket, Object[] args) {
		//可以实现过滤
		return true;
	}

}
