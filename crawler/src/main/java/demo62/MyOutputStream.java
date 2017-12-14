package demo62;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public abstract class MyOutputStream implements Closeable , Flushable{
	
	public abstract void write(int b) throws IOException;
	
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		if(b == null)
			throw new NullPointerException();
		if(off < 0 || off > b.length || len < 0 || off + len > b.length)
			throw new ArrayIndexOutOfBoundsException();
		if(len == 0)
			return;
		for(int i=0; i<len; i++)
			write(b[off + i]);
	}
	
	
	
	@Override
	public void flush() throws IOException {
		
		
	}
	
	@Override
	public void close() throws IOException {
		
		
	}
	

}
