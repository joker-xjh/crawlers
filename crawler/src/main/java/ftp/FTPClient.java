package ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class FTPClient {
	
	private static String fileDir = "F://client-directory/";
	private static Scanner ctrlScanner;
	private static PrintWriter ctrlWriter;
	private static Scanner dataScanner;
	private static PrintWriter dataWriter;
	private static byte[] buf = new byte[1024];
	private static Scanner userScanner;
	
	private static Socket dataSocket;
	private static Socket ctrlSocket;
	
	public static void main(String[] args) {
		if (args==null||args.length != 2) {
            System.out.println("格式 ipAddress port");
            return;
        }
		initPath();
		Inet4Address address = null;
		try {
			address = (Inet4Address) Inet4Address.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println("未知主机名");
			e.printStackTrace();
			return;
		}
		Integer port = Integer.parseInt(args[1]);
		try {
			ctrlSocket = new Socket(address, port);
			ctrlScanner = new Scanner(ctrlSocket.getInputStream());
			ctrlWriter = new PrintWriter(ctrlSocket.getOutputStream());
			boolean success = ctrlScanner.nextBoolean();
			if(!success) {
				System.out.println("连接失败");
				ctrlSocket.close();
				ctrlScanner.close();
				ctrlWriter.close();
				return;
			}
			System.out.println("连接成功");
			System.out.println("控制连接已经建立在 " + ctrlSocket.getInetAddress() + " 端口: " + ctrlSocket.getPort());
			long id = ctrlScanner.nextLong();
			dataSocket = new Socket(address, port+1);
			dataScanner = new Scanner(dataSocket.getInputStream());
			dataWriter = new PrintWriter(dataSocket.getOutputStream());
			System.out.println("数据连接成功,客户端IP: " + dataSocket.getInetAddress() + " 端口: " + dataSocket.getPort());
			dataWriter.println(id);
			userScanner = new Scanner(System.in);
			String line = userScanner.nextLine();
			while(!line.equals("QUIT")) {
				String[] cmds = line.split(" ");
				String cmd = cmds[0];
				switch(cmd) {
				case "GET" :
					
				}
			}
			ctrlWriter.println("QUIT");
			ctrlSocket.close();
			ctrlScanner.close();
			ctrlWriter.close();
			dataSocket.close();
			dataScanner.close();
			dataWriter.close();
		} catch (IOException e) {
			
		}
		
		
	}
	
	private static void initPath(){
        File dir=new File(fileDir);
        if (!dir.exists()){
            dir.mkdir();
        }

    }
	
	
	private static void doList() {
		ctrlWriter.println("LIST");
		System.out.println("FTP服务端目录:");
		String fileName = dataScanner.nextLine();
		while(!fileName.equals("$")) {
			System.out.println(fileName);
			fileName = dataScanner.nextLine();
		}
	}
	
	
	private static boolean doGet(String fileName) {
		boolean result = false;
		File file = new File(fileDir+fileName);
		ctrlWriter.println("GET "+fileName);
		long size = dataScanner.nextLong();
		long receive = 0;
		try (BufferedInputStream in = new BufferedInputStream(dataSocket.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))){
			while(receive < size) {
				int temp = in.read(buf, 0, buf.length);
				out.write(buf, 0, temp);
				receive += temp;
			}
			out.flush();
			
		} catch (IOException e) {
			
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	


}
