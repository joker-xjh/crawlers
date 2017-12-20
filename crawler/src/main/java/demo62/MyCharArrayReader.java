package demo62;

import java.io.IOException;
import java.io.Reader;

public class MyCharArrayReader extends Reader{
	
	protected char[] buf;
	protected int pos;
	protected int markedPos;
	protected int count;
	
	public MyCharArrayReader(char[] buf) {
		this.buf = buf;
		this.pos = 0;
		this.count = buf.length;
	}
	
	public MyCharArrayReader(char[] buf, int off, int len) {
		if(off < 0 || off > buf.length || len < 0)
			throw new ArrayIndexOutOfBoundsException();
		this.buf = buf;
		this.pos = off;
		this.markedPos = off;
		this.count = Math.min(off+len, buf.length);
	}
	
	private void ensureOpen() throws IOException {
		if(buf == null)
			throw new IOException("Stream closed");
	}
	
	@Override
	public int read() throws IOException {
		synchronized(lock) {
			ensureOpen();
			if(pos >= count)
				return -1;
			return buf[pos++];
		}
	}
	
	
	@Override
	public long skip(long n) throws IOException {
		synchronized(lock) {
			ensureOpen();
			if(n > count - pos)
				n = count - pos;
			if(n <= 0)
				return 0;
			pos += n;
			return n;
		}
	}
	
	
	@Override
	public boolean ready() throws IOException {
		synchronized(lock) {
			ensureOpen();
			return (count - pos) > 0;
		}
	}
	
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public void mark(int readAheadLimit) throws IOException {
		synchronized (lock) {
			ensureOpen();
			markedPos = pos;
		}
	}
	
	@Override
	public void reset() throws IOException {
		synchronized (lock) {
			ensureOpen();
			pos = markedPos;
		}
	}
	
	

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		synchronized(lock) {
			ensureOpen();
			if(off < 0 || off > cbuf.length || len < 0 || len + off > cbuf.length || off + len < 0)
				throw new ArrayIndexOutOfBoundsException();
			else if(len == 0)
				return 0;
			if(pos >= count)
				return -1;
			if(len + pos > count )
				len = count - pos;
			if(len <= 0)
				return 0;
			System.arraycopy(buf, pos, cbuf, off, len);
			pos += len;
			return len;
		}
	}

	@Override
	public void close() throws IOException {
		buf = null;
	}

}
