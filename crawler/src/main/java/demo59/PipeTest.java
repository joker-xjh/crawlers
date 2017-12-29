package demo59;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipeTest {

	public static void main(String[] args) {
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream();
		try {
			out.connect(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sender sender = new Sender(out);
		Receiver receiver = new Receiver(in);
		new Thread(sender).start();
		new Thread(receiver).start();

	}
	
	static class Sender implements Runnable{
		PipedOutputStream out;
		public Sender(PipedOutputStream out) {
			this.out = out;
		}
		@Override
		public void run() {
			String message = "kobebryant";
			try (PipedOutputStream temp = out){
				out.write(message.getBytes("utf-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static class Receiver implements Runnable {
		PipedInputStream in;
		public Receiver(PipedInputStream in) {
			this.in = in;
		}
		byte[] buffer = new byte[100];
		@Override
		public void run() {
			try (PipedInputStream temp = in){
				int len = temp.read(buffer);
				String message = new String(buffer, 0, len, "utf-8");
				System.out.println("messge: "+message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
