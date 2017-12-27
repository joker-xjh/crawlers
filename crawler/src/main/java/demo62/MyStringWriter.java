package demo62;

import java.io.IOException;
import java.io.Writer;

public class MyStringWriter extends Writer{
	
	private StringBuffer buf;
	
	public MyStringWriter() {
		buf = new StringBuffer();
		lock = buf;
	}
	
	public MyStringWriter(int size) {
		if(size < 0)
			throw new IllegalArgumentException();
		buf = new StringBuffer(size);
		lock = buf;
	}
	
	@Override
	public void write(int c) throws IOException {
		buf.append((char)c);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if(off < 0 || off >= cbuf.length || len < 0 || off + len >= cbuf.length)
			throw new ArrayIndexOutOfBoundsException();
		else if(len == 0)
			return;
		buf.append(cbuf, off, len);
	}
	
	public void write(String str) {
		buf.append(str);
	}
	
	public void write(String str, int off, int len) {
		buf.append(str.substring(off, len));
	}
	
	@Override
	public Writer append(CharSequence csq) throws IOException {
		if(csq == null)
			csq = "null";
		write(csq.toString());
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		csq = csq == null ? "null" : csq.subSequence(start, end);
		write(csq.toString());
		return this;
	}
	
	@Override
	public Writer append(char c) throws IOException {
		write(c);
		return this;
	}
	
	public StringBuffer getBuf() {
		return buf;
	}
	
	@Override
	public String toString() {
		return buf.toString();
	}

	@Override
	public void flush() throws IOException {
		
	}

	@Override
	public void close() throws IOException {
		
	}

}
