package demo62;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyPushbackInputStream extends FilterInputStream{
	
	protected byte[] buffer;
	
	protected int pos;
	
	private void ensureOpen() throws IOException {
		if(in == null)
			throw new IOException("Stream closed");
	}
	

	protected MyPushbackInputStream(InputStream in, int size) {
		super(in);
		if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
		buffer = new byte[size];
		this.pos = size;
	}
	
	@Override
	public int read() throws IOException {
		ensureOpen();
		if(pos < buffer.length)
			return buffer[pos++] & 0xFF;
		return super.read();
	}
	
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		ensureOpen();
		if(b == null)
			throw new NullPointerException();
		else if(off < 0 || len < 0 || off > b.length - len)
			throw new ArrayIndexOutOfBoundsException();
		else if(len == 0)
			return 0;
		int avail = buffer.length - pos;
		if(avail > 0) {
			if(avail > len)
				avail = len;
			System.arraycopy(buffer, pos, b, off, avail);
			pos += avail;
			len -= avail;
			off += avail;
		}
		if(len > 0) {
			len = super.read(b, off, len);
			if(len == -1)
				return avail == 0 ? -1 : avail;
			avail += len;
		}
		return avail;
	}
	
	
	public void unread(int b) throws IOException {
		ensureOpen();
		if(pos == 0)
			throw new IOException("Push back buffer is full");
		buffer[--pos] = (byte) b;
	}
	
	public void unread(byte[] b, int off, int len) throws IOException {
		ensureOpen();
		if(len > pos)
			throw new IOException("Push back buffer is full");
		pos -= len;
		System.arraycopy(b, off, buffer, pos, len);
	}
	
	public void unread(byte[] b) throws IOException {
		unread(b, 0, b.length);
	}
	
	@Override
	public int available() throws IOException {
		ensureOpen();
		int avail = buffer.length - pos;
		int n = super.available();
		return avail > Integer.MAX_VALUE - n ? Integer.MAX_VALUE : avail + n;
	}
	
	@Override
	public long skip(long n) throws IOException {
		ensureOpen();
		if(n <= 0)
			return 0;
		long s = buffer.length - pos;
		if(s > 0) {
			if(s > n) {
				s = n;
			}
			n -= s;
			pos += s;
		}
		if(n > 0) {
			n = super.skip(n);
			s += n;
		}
		return s;
	}
	
	@Override
	public boolean markSupported() {
		return false;
	}
	
	
	@Override
	public void close() throws IOException {
		if(in == null)
			return;
		in.close();
		in = null;
		buffer = null;
	}
	
	
	

}
