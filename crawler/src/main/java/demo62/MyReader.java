package demo62;

import java.io.Closeable;
import java.io.IOException;

public abstract class MyReader implements Readable, Closeable {
	
	protected Object lock;
	
	protected MyReader() {
		this.lock = this;
	}
	
	protected MyReader(Object lock) {
		if(lock == null)
			throw new NullPointerException();
		this.lock = lock;
	}
	
	public abstract int read(char[] buf, int off, int len) throws IOException;
	
	public int read() throws IOException {
		char[] buf = new char[1];
		if(read(buf, 0, 1) == -1)
			return -1;
		return buf[0];
	}
	
	private static final int maxskipBuffSize = 8192;
	
	private char[] skipbuf = null;
	
	public long skip(long n) throws IOException {
		if(n < 0L)
			throw new IOException();
		int nn = (int) Math.min(n, maxskipBuffSize);
		synchronized(lock) {
			if(skipbuf == null || skipbuf.length < nn)
				skipbuf = new char[nn];
			long r = n;
			while(r > 0) {
				int nc = read(skipbuf, 0, (int)Math.min(nn, r));
				if(nc == -1)
					break;
				r -= nc;
			}
			return n - r;
		}
	}
	
	public boolean ready() throws IOException {
		return false;
	}
	
	
	public boolean markedSupported() throws IOException {
		return false;
	}
	
	public void mark(int readLimit) throws IOException {
		throw new IOException("mark() not supported");
	}
	
	public void reset() throws IOException {
		throw new IOException("reset() not supported");
	}
	
	public abstract void close() throws IOException;
	

}
