package demo62;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyBufferedInputStream extends FilterInputStream{
	
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	protected volatile byte[] buf;
	
	protected int pos;
	
	protected int count;
	
	protected int markpos = -1;
	
	protected int marklimit;
	
	
	private InputStream getInIfOpen() throws IOException {
		InputStream inputStream = in;
		if(inputStream == null)
			throw new IOException("Stream is closed");
		return inputStream;
	}
	
	private byte[] getBufIfOpen() throws IOException {
		byte[] buffer = buf;
		if(buffer == null)
			throw new IOException("Stream is closed");
		return buffer;
	}
	
	
	public MyBufferedInputStream(InputStream in, int size) {
		super(in);
		if(size <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		buf = new byte[size];
	}
	
	
	public MyBufferedInputStream(InputStream in) {
		this(in, DEFAULT_BUFFER_SIZE);
	}
	
	private void fill() throws IOException {
		byte[] buffer = getBufIfOpen();
		if(markpos < 0)
			pos = 0;
		else if(pos >= buf.length) {
			if(markpos > 0) {
				int sz = pos - markpos;
				System.arraycopy(buffer, markpos, buffer, 0, sz);
				markpos = 0;
				pos = sz;
			}
			else if(buffer.length >= marklimit) {
				markpos = -1;
				pos = 0;
			}
			else if(buffer.length >= MAX_ARRAY_SIZE) {
				throw new OutOfMemoryError("Required array size too large");
			}
			else {
				int nsz = (pos <= MAX_ARRAY_SIZE - pos) ? pos * 2 : MAX_ARRAY_SIZE;
				if(nsz > marklimit)
					nsz = marklimit;
				byte[] nbuf = new byte[nsz];
				System.arraycopy(buf, 0, nbuf, 0, pos);
				buf = nbuf;
				buffer = nbuf;
			}
		}
		
		count = pos;
		int n = getInIfOpen().read(buf, pos, buf.length - pos);
		if(n > 0)
			count += n;
	}
	
	
	@Override
	public synchronized int read() throws IOException {
		if(pos >= count) {
			fill();
			if(pos >= count)
				return -1;
		}
		return getBufIfOpen()[pos++] & 0xff;
	}
	
	private synchronized int read1(byte[] b, int off, int len) throws IOException {
		int avail = count - pos;
		if(avail <= 0) {
			if(len >= getBufIfOpen().length && markpos < 0)
				return getInIfOpen().read(b, off, len);
			fill();
			avail = count - pos;
			if(avail <= 0)
				return -1;
		}
		int cnt = avail < len ? avail : len;
		System.arraycopy(buf, pos, b, off, cnt);
		pos += cnt;
		return cnt;
	}
	
	
	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		getBufIfOpen();
		if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
		int n = 0;
		for(;;) {
			int nread = read1(b, off, len);
			if(nread <= 0)
				 return (n == 0) ? nread : n;
			n += nread;
			if(n >= len)
				return n;
			InputStream inputStream = in;
			if(in != null && inputStream.available() <= 0)
				return n;
		}
	}
	
	@Override
	public synchronized long skip(long n) throws IOException {
		getBufIfOpen();
		int avail = count - pos;
		if(avail <= 0) {
			if(markpos < 0)
				return getInIfOpen().skip(n);
			fill();
			avail = count - pos;
			if(avail <= 0)
				return 0;
		}
		long skipped = avail < n ? avail : n;
		pos += skipped;
		return skipped;
	}
	
	@Override
	public int available() throws IOException {
		int n = count - pos;
		int avail = getInIfOpen().available();
		return n > Integer.MAX_VALUE - avail ? Integer.MAX_VALUE : n + avail;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		markpos = pos;
		marklimit = readlimit;
	}
	
	@Override
	public synchronized void reset() throws IOException {
		getBufIfOpen();
		if(markpos < 0)
			throw new IOException("Resetting to invalid mark");
		pos = markpos;
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public synchronized void close() throws IOException {
		buf = null;
		in.close();
	}
	

}
