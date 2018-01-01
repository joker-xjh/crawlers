package demo62;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class MyPushbackReader extends FilterReader{
	
	protected char[] buffer;
	
	protected int pos;

	protected MyPushbackReader(Reader in, int size) {
		super(in);
		if(size <= 0)
			throw new IllegalArgumentException("size <= 0");
		buffer = new char[size];
	}
	
	private void ensureOpen() throws IOException {
		if(in == null)
			throw new IOException("Stream closed");
	}
	
	@Override
	public int read() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(pos < buffer.length)
				return buffer[pos++];
			return super.read();
		}
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(off < 0 || len < 0 || off > cbuf.length - len)
				throw new ArrayIndexOutOfBoundsException();
			else if(len == 0)
				return 0;
			int temp = buffer.length - pos;
			if(temp > 0) {
				if(temp > len)
					temp = len;
				len -= temp;
				off += temp;
				pos += temp;
			}
			if(len > 0) {
				int n = super.read(cbuf, off, len);
				if(n == -1)
					return temp == 0 ? -1 : temp;
				temp += n;
			}
			return temp;
		}
	}
	
	
	public void unread(int b) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(pos == 0)
				throw new IOException("Push Back buf is full");
			buffer[--pos] = (char) b;
		}
	}
	
	
	public void unread(byte[] b, int off, int len) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(off < 0 || len < 0 || off > b.length - len)
				throw new ArrayIndexOutOfBoundsException();
			else if(len == 0)
				return;
			if(pos < len)
				throw new IOException("Pushback buffer overflow");
			pos -= len;
			System.arraycopy(b, off, buffer, pos, len);
		}
	}
	
	
	@Override
	public boolean ready() throws IOException {
		synchronized (lock) {
			ensureOpen();
			return pos < buffer.length || super.ready();
		}
	}
	
	@Override
	public boolean markSupported() {
		return false;
	}
	
	@Override
	public void mark(int readAheadLimit) throws IOException {
		throw new IOException("mark/reset not supported");
	}
	
	
	@Override
	public void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}
	
	@Override
	public void close() throws IOException {
		buffer = null;
		super.close();
	}
	
	
	@Override
	public long skip(long n) throws IOException {
		if(n <= 0)
			return 0;
		synchronized (lock) {
			ensureOpen();
			long p = buffer.length - pos;
			if(p > 0) {
				if(p > n)
					p = n;
				n -= p;
				pos += p;
			}
			if(n > 0) {
				p += super.skip(n);
			}
			return p;
		}
	}

}
