package demo62;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MyBufferedOutputStream extends FilterOutputStream{
	
	protected byte[] buf;
	
	protected int count;
	
	public MyBufferedOutputStream(OutputStream out) {
		this(out, 8192);
	}

	public MyBufferedOutputStream(OutputStream out, int size) {
		super(out);
		if(size <= 0)
			throw new IllegalArgumentException("Buffer Size <= 0");
		buf = new byte[size];
	}
	
	private void flushBuf() throws IOException {
		if(count > 0) {
			out.write(buf, 0, count);
			count = 0;
		}
	}
	
	@Override
	public synchronized void write(int b) throws IOException {
		if(count >= buf.length) {
			flushBuf();
		}
		buf[count++] = (byte) b;
	}
	
	
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		if(len >= buf.length) {
			flushBuf();
			out.write(b, off, len);
			return;
		}
		if(len > buf.length - count)
			flushBuf();
		System.arraycopy(b, off, buf, count, len);
		count += len;
	}
	
	
	@Override
	public synchronized void flush() throws IOException {
		flushBuf();
		out.flush();
	}
	
	
	

}
