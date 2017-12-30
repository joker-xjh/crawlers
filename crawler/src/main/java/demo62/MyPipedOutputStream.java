package demo62;

import java.io.IOException;
import java.io.OutputStream;

public class MyPipedOutputStream extends OutputStream{
	
	private MyPipedInputStream in;
	
	public MyPipedOutputStream(MyPipedInputStream in) throws IOException {
		connect(in);
	}
	
	public synchronized void connect(MyPipedInputStream in) throws IOException {
		if(in == null)
			throw new NullPointerException();
		else if(this.in != null || in.connected)
			throw new IOException("Already connected");
		this.in = in;
		in.connected = true;
		in.in = -1;
		in.out = 0;
	}

	@Override
	public void write(int b) throws IOException {
		if(in == null)
			throw new IOException("Pipe not connected");
		in.receive(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (in == null) {
            throw new IOException("Pipe not connected");
        } else if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                   ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
		in.receive(b, off, len);
	}
	
	@Override
	public void flush() throws IOException {
		if(in != null) {
			synchronized(in) {
				in.notifyAll();
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		if(in != null) {
			in.receivedLast();
		}
	}
	
	
	

}
