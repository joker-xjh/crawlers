package demo62;

import java.io.IOException;
import java.io.Reader;

public class MyStringReader extends Reader{
	
	private String str;
	private int next;
	private int length;
	private int mark;
	
	public MyStringReader(String str) {
		this.str = str;
		this.length = str.length();
	}
	
	private void ensureOpen() throws IOException {
		if(str == null)
			throw new IOException("Stream closed");
	}
	
	@Override
	public int read() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(next >= length)
				return -1;
			return str.charAt(next++);
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(off < 0 || off >= cbuf.length || len < 0 || off + len >= cbuf.length || off + len < 0)
				throw new ArrayIndexOutOfBoundsException();
			else if(len == 0)
				return 0;
			if(next >= length)
				return -1;
			int n = Math.min(len, length - next);
			str.getChars(next, next + n, cbuf, off);
			next += n;
			return n;
		}
	}
	
	@Override
	public long skip(long n) throws IOException {
		if(n < 0)
			throw new IllegalArgumentException();
		synchronized (lock) {
			ensureOpen();
			if(next >= length)
				return 0;
			int skip = (int) Math.min(n, length - next);
			next += skip;
			return skip;
		}
	}
	
	@Override
	public boolean ready() throws IOException {
		synchronized (lock) {
			ensureOpen();
			return true;
		}
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public void mark(int readAheadLimit) throws IOException {
		if(readAheadLimit <= 0)
			throw new IllegalArgumentException();
		synchronized (lock) {
			ensureOpen();
			mark = next;
		}
	}
	
	@Override
	public void reset() throws IOException {
		synchronized (lock) {
			ensureOpen();
			next = mark;
		}
	}

	@Override
	public void close() throws IOException {
		str = null;
	}

}
