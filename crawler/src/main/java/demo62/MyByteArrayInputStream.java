package demo62;

import java.io.IOException;
import java.io.InputStream;

public class MyByteArrayInputStream extends InputStream{
	
	protected byte[] buf;
	
	protected int pos;
	
	protected int mark;
	
	protected int count;
	
	public MyByteArrayInputStream(byte[] buf) {
		this.buf = buf;
		this.pos = 0;
		this.count = buf.length;
	}
	
	public MyByteArrayInputStream(byte[] buf, int offset, int length) {
		this.buf = buf;
		this.pos = offset;
		this.mark = offset;
		this.count = Math.min(offset + length, buf.length);
	}

	@Override
	public synchronized int read() throws IOException {
		return pos < count ? buf[pos++] & 0xff : -1;
	}
	
	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if(b == null)
			throw new NullPointerException();
		if(off < 0 || off > b.length || len > b.length - off)
			throw new ArrayIndexOutOfBoundsException();
		int avaiable = count - pos;
		if(avaiable < len)
			len = avaiable;
		if(len <= 0)
			return 0;
		System.arraycopy(buf, pos, b, off, len);
		pos += len;
		return len;
	}
	
	@Override
	public synchronized long skip(long n) throws IOException {
		long k = count - pos;
		if(n < k)
			k = n < 0 ? 0 : n;
		pos += k;
		return k;
	}
	
	
	@Override
	public synchronized int available() throws IOException {
		return count - pos;
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		mark = pos;
	}
	
	@Override
	public synchronized void reset() throws IOException {
		pos = mark;
	}
	
	
	

}
