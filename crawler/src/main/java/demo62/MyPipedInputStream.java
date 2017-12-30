package demo62;

import java.io.IOException;
import java.io.InputStream;

public class MyPipedInputStream extends InputStream{
	
	boolean closedByWriter = false;
	volatile boolean closedByReader = false;
	boolean connected = false;
	
	Thread writeSide;
	Thread readSide;
	
	private static final int DEFAULT_PIPE_SIZE = 1024;
	
	protected static final int PIPE_SIZE = DEFAULT_PIPE_SIZE;
	
	protected byte[] buffer;
	
	protected int in = -1;
	
	protected int out = 0;
	
	public MyPipedInputStream(MyPipedOutputStream out, int size) throws IOException {
		initPipe(size);
		connect(out);
	}
	
	private void initPipe(int size) {
		if(size <= 0)
			throw new IllegalArgumentException();
		buffer = new byte[size];
	}
	
	public void connect(MyPipedOutputStream out) throws IOException {
		out.connect(this);
	}
	
	private void checkStateForReceive() throws IOException{
		if(!connected)
			throw new IOException("Pipe not connected");
		else if(closedByReader || closedByWriter)
			throw new IOException("Pipe closed");
		else if(readSide != null && !readSide.isAlive())
			throw new IOException("Read end dead");
	}
	
	private void waitSpace() throws IOException {
		while(in == out) {
			checkStateForReceive();
			notifyAll();
			try {
				wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected synchronized void receive(int b) throws IOException {
		checkStateForReceive();
		writeSide = Thread.currentThread();
		if(in == out)
			waitSpace();
		if(in < 0) {
			in = 0;
			out = 0;
		}
		buffer[in++] = (byte) (b & 0xFF);
		if(in >= buffer.length)
			in = 0;
	}
	
	synchronized void receive(byte[] b, int off, int len) throws IOException {
		checkStateForReceive();
		writeSide = Thread.currentThread();
		int bytesToTransfer = len;
		while(bytesToTransfer > 0) {
			if(in == out)
				waitSpace();
			int nextTransferAmount = 0;
			if(out < in) {
				nextTransferAmount = buffer.length - in;
			}
			else {
				if(in == -1) {
					in = out = 0;
					nextTransferAmount = buffer.length - in;
				}
				else {
					nextTransferAmount = out - in;
				}
			}
			if(nextTransferAmount > bytesToTransfer)
				nextTransferAmount = bytesToTransfer;
			System.arraycopy(b, off, buffer, in, nextTransferAmount);
			off += nextTransferAmount;
			in += nextTransferAmount;
			bytesToTransfer -= nextTransferAmount;
			if(in >= buffer.length)
				in = 0;
		}
	}
	
	synchronized void receivedLast() {
		closedByWriter = true;
		notifyAll();
	}
	
	
	@Override
	public synchronized int read() throws IOException {
		if(!connected)
			throw new IOException("Pipe not connected");
		else if(closedByReader)
			throw new IOException("Pipe closed");
		else if (writeSide != null && !writeSide.isAlive()
                && !closedByWriter && (in < 0)) 
         throw new IOException("Write end dead");
		
		readSide = Thread.currentThread();
		int trials = 2;
		while(in < 0) {
			if(closedByWriter)
				return -1;
			if ((writeSide != null) && (!writeSide.isAlive()) && (--trials < 0)) {
                throw new IOException("Pipe broken");
            }
			notifyAll();
			try {
				wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		int ret = buffer[out++] & 0xFF;
		if(out >= buffer.length)
			out = 0;
		if(in == out)
			in = -1;
		return ret;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if(b == null)
			throw new NullPointerException();
		else if(off < 0 || off > b.length || len < 0 || off + len > b.length)
			throw new ArrayIndexOutOfBoundsException();
		else if(len == 0)
			return 0;
		int c = read();
		if(c < 0)
			return -1;
		buffer[off] = (byte) c;
		int rlen = 1;
		while(in >= 0 && len > 1) {
			int available;
			if(in > out)
				available = in - out;
			else
				available = buffer.length - out;
			if(available > len -1)
				available = len - 1;
			System.arraycopy(buffer, out, b, off + rlen, available);
			rlen += available;
			len -= available;
			out += available();
			if(out >= buffer.length)
				out = 0;
			if(in == out)
				in = -1;
		}
		return rlen;
	}
	
	
	@Override
	public int available() throws IOException {
		if(in < 0)
			return 0;
		else if(in == out)
			return buffer.length;
		else if(in > out)
			return in - out;
		else 
			return in + buffer.length - out;
	}
	
	@Override
	public void close() throws IOException {
		closedByReader = true;
		synchronized(this) {
			in = -1;
		}
	}

}
