package demo62;

import java.io.IOException;
import java.io.Writer;

public class MyBufferedWriter extends Writer{
	
	private Writer out;
	
	private char[] cb;
	private int nextChar, nChar;
	
	private static final int defaultCharBufferSize = 8192;
	
	private String lineSeparator;
	
	@SuppressWarnings("restriction")
	public MyBufferedWriter(Writer out, int size) {
		super(out);
		if(size <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		this.cb = new char[size];
		this.nChar = size;
		lineSeparator = java.security.AccessController.doPrivileged(
	            new sun.security.action.GetPropertyAction("line.separator"));
	}
	
	public MyBufferedWriter(Writer out) {
		this(out, defaultCharBufferSize);
	}
	
	private void ensureOpen() throws IOException {
		if(out == null)
			throw new IOException("Stream closed");
	}
	
	void flushBuffer() throws IOException{
		synchronized(lock) {
			ensureOpen();
			if(nextChar == 0)
				return;
			out.write(cb, 0, nextChar);
			nextChar = 0;
		}
	}
	
	
	@Override
	public void write(int c) throws IOException {
		synchronized(lock) {
			ensureOpen();
			if(nextChar >= nChar)
				flushBuffer();
			cb[nextChar++] = (char) c; 
		}
	}
	

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		synchronized(lock) {
			ensureOpen();
			if(off < 0 || off > cbuf.length || len < 0 || len + off > cbuf.length || len+off < 0)
				throw new ArrayIndexOutOfBoundsException();
			else if(len == 0)
				return;
			if(len >= nChar) {
				flushBuffer();
				out.write(cbuf, off, len);
				return;
			}
			
			int b = off, t = off + len;
			while(b < t) {
				int d = Math.min(nChar - nextChar, t - b);
				System.arraycopy(cbuf, b, cb, nextChar, d);
				b += d;
				nextChar += d;
				if(nextChar >= nChar)
					flushBuffer();
			}
		}
		
	}
	
	public void write(String s, int off, int len) throws IOException {
		synchronized(lock) {
			ensureOpen();
			int b = off, t = off + len;
			while(b < t) {
				int d = Math.min(nChar - nextChar, t-b);
				s.getChars(b, t, cb, nextChar);
				b += d;
				nextChar += d;
				if(nextChar >= nChar)
					flushBuffer();
			}
		}
	}
	
	public void newLine() throws IOException {
		write(lineSeparator);
	}

	@Override
	public void flush() throws IOException {
		synchronized (lock) {
			flushBuffer();
			out.flush();
		}
		
	}

	@Override
	public void close() throws IOException {
		synchronized(lock) {
			if(out == null)
				return;
			try (Writer writer = out){
				flush();
			} finally {
				out = null;
				cb = null;
			}
		}
		
	}

}
