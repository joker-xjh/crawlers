package ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerSession implements Runnable {
	
	private Socket ctrlSocket;
	private Socket dataSocket;
	
	private Scanner ctrlScanner;
	private Scanner dataScanner;
	
	private PrintWriter ctrlWriter;
	private PrintWriter dataWriter;
	
	private byte[] buf = new byte[1024];
	
	public ServerSession(Socket ctrl, Socket data) {
		ctrlSocket = ctrl;
		dataSocket = data;
		try {
			ctrlScanner = new Scanner(ctrlSocket.getInputStream());
			dataScanner = new Scanner(dataSocket.getInputStream());
			ctrlWriter = new PrintWriter(ctrlSocket.getOutputStream(), true);
			dataWriter = new PrintWriter(dataSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		String cmd = ctrlScanner.nextLine().trim();
		while(!cmd.equals("QUIT")) {
			System.out.println("收到命令:"+cmd);
			switch(cmd) {
			case "GET":
				if(doGet()) {
					ctrlWriter.println("OK");
				}
				else {
					ctrlWriter.println("ERROR");
				}
				break;
			case "PUT":
				if(doPut()) {
					ctrlWriter.println("OK");
				}
				else {
					ctrlWriter.println("ERROR");
				}
				break;
			case "LIST":
				doList();
				break;
			default:
					System.out.println("无效的命令");
					ctrlWriter.println("INVALID");
					break;
			}
			cmd = ctrlScanner.nextLine().trim();
		}
	}
	
	
	private boolean doGet() {
		boolean result = false;
		String fileName = dataScanner.nextLine().trim();
		File file = new File(FTPServer.getSystemPath(fileName));
		if(file.exists()) {
			long size = file.length();
			dataWriter.println(size);
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
					BufferedOutputStream out = new BufferedOutputStream(dataSocket.getOutputStream())){
				int read = -1;
				while((read = in.read(buf, 0, buf.length)) != -1) {
					out.write(buf, 0, read);
				}
				out.flush();
				System.out.println("传输文件:"+file+"成功");
				result = true;
			} catch (IOException e) {
				System.out.println("传输文件:"+file+"失败");
				e.printStackTrace();
			}
		}
		else {
			ctrlWriter.println(false);
			System.out.println("文件:"+file+"不存在");
		}
		return result;
	}
	
	private boolean doPut() {
		boolean result = false;
		String fileName = dataScanner.nextLine().trim();
		System.out.println(fileName);
		File file = new File(FTPServer.getSystemPath(fileName));
		if(file.exists())
			file.delete();
		long size = dataScanner.nextLong();
		try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				BufferedInputStream in = new BufferedInputStream(dataSocket.getInputStream())){
			int receive = 0;
			while(receive < size) {
				int temp = in.read(buf, 0, buf.length);
				out.write(buf, 0, temp);
				receive += temp;
			}
			out.flush();
			result = true;
			System.out.println("收到客户端上传的文件:"+fileName);
		} catch (IOException e) {
			System.out.println("文件传输错误");
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	private void doList() {
		printFile(FTPServer.getSystemPath(""), 0);
		dataWriter.println("$");
	}
	
	private void printFile(String dir, int depth) {
		File directory = new File(dir);
		File[] subs = directory.listFiles();
		for(File sub : subs) {
			if(sub.isDirectory())
				printFile(sub.getAbsolutePath(), depth+1);
			else {
				for(int i=0; i<depth; i++)
					dataWriter.print(" ");
				dataWriter.println(sub.getName());
			}
		}
	}
	
	
	
	
	
	
	
	

}
