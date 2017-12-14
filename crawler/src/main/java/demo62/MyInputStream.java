package demo62;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class MyInputStream implements Closeable{
	
	private static final int MAX_SKIP_BUFFER_SIZE = 2048;
	
	private static final int DEFAULT_BUFFER_SIZE = 8196;
	
	private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
	
	public abstract int read() throws IOException;
	
	public int read(byte[] b) throws IOException{
		return read(b, 0, b.length);
	}
	
	public int read(byte[] b, int offset, int len) throws IOException {
		if(b == null)
			throw new NullPointerException();
		if(offset < 0 || len < 0 || len < b.length - offset)
			throw new ArrayIndexOutOfBoundsException();
		if(len == 0)
			return 0;
		int c = read();
		if(c == -1)
			return -1;
		b[offset] = (byte)c;
		int i = 1;
		for(; i<len; i++) {
			int temp = read();
			if(temp == -1)
				break;
			b[offset + i] = (byte)temp;
		}
		return i;
	}
	
	public byte[] readAllBytes() throws IOException {
		byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
		int capacity = buf.length;
		int nread = 0;
		int n;
		for(;;) {
			while((n = read(buf, nread, capacity - nread)) > 0)
				nread += n;
			if(n < 0)
				break;
			if(capacity <= MAX_BUFFER_SIZE - capacity)
				capacity = capacity << 1;
			else {
				if(capacity == MAX_BUFFER_SIZE)
					throw new OutOfMemoryError();
				capacity = MAX_BUFFER_SIZE;
			}
		}
		return ( capacity == nread ) ? buf : Arrays.copyOf(buf, nread);
	}
	
	
	public int avaiable() throws IOException {
		return 0;
	}
	
	public long skip(long n) throws IOException {
		if(n <= 0)
			return 0;
		long remaining = n;
		int size = (int) Math.min(MAX_SKIP_BUFFER_SIZE, n);
		byte[] skip = new byte[size];
		int nread;
		while(remaining > 0) {
			nread = read(skip, 0, (int)Math.min(size, remaining));
			if(nread < 0)
				break;
			remaining -= nread;
		}
		return n - remaining;
	}
	
	public long transferTo(OutputStream out) throws IOException {
		long transfered = 0;
		byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
		int nread;
		while((nread = read(buf, 0, buf.length)) >= 0) {
			out.write(buf, 0, nread);
			transfered += nread;
		}
		return transfered;
	}
	
	
	
	
	
	
	
	
	
	
	

}
