package demo62;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;


public class MyCharArrayWriter extends Writer{
	
	protected char[] buf;
	protected int count;
	
	public MyCharArrayWriter(int size) {
		if(size <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		buf = new char[size];
	}
	
	public MyCharArrayWriter() {
		this(32);
	}
	
	@Override
	public void write(int c) throws IOException {
		synchronized (lock) {
			int newCount = count + 1;
			if(newCount >= buf.length)
				buf = Arrays.copyOf(buf, Math.max(newCount, buf.length << 1));
			buf[count++] = (char) c;
			count = newCount;
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if(off < 0 || off > cbuf.length || len < 0 || len + off > cbuf.length ||  off + len < 0)
			throw new ArrayIndexOutOfBoundsException();
		else if(len == 0)
			return;
		synchronized(lock) {
			int newCount = count + len;
			if(newCount >= buf.length)
				buf = Arrays.copyOf(buf, Math.max(newCount, buf.length << 1));
			System.arraycopy(cbuf, off, buf, count, len);
			count += len;
		}
	}
	
	@Override
	public void write(String str, int off, int len) throws IOException {
		synchronized (lock) {
			int newCount = count + len;
			if(newCount >= buf.length)
				buf = Arrays.copyOf(buf, Math.max(newCount, buf.length << 1));
			str.getChars(off, off+len, buf, count);
			count += len;
		}
	}
	
	@Override
	public void write(String str) throws IOException {
		write(str, 0, str.length());
	}
	
	public void writeTo(Writer writer) throws IOException{
		synchronized (lock) {
			writer.write(buf, 0, count);
		}
	}
	
	public MyCharArrayWriter append(CharSequence charSequence) throws IOException{
		String str = (charSequence == null ? "null" : charSequence.toString());
		write(str);
		return this;
	}
	
    public MyCharArrayWriter append(CharSequence csq, int start, int end) throws IOException {
    	String str = (csq == null ? "null" : csq.toString()).substring(start, end);
    	write(str);
    	return this;
    }
    
    public MyCharArrayWriter append(char c) throws IOException{
    	write(c);
    	return this;
    }

	public void reset() {
		synchronized(lock) {
			count = 0;
		}
	}
	
	public char[] toCharArray() {
		synchronized (lock) {
			return Arrays.copyOf(buf, count);
		}
	}
	
	public int size() {
		return count;
	}
	
	@Override
	public String toString() {
		synchronized (lock) {
			return new String(buf, 0, count);
		}
	}
	

	@Override
	public void flush() throws IOException {
		
	}

	@Override
	public void close() throws IOException {
		synchronized(lock) {
			buf = null;
		}
	}

}
