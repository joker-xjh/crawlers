package demo62;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;


public abstract class MyWriter implements Flushable, Closeable, Appendable {
	
	private char[] buffer;
	
	private static final int WRITER_BUFFER_SIZE = 1024;
	
	protected Object lock;
	
	protected MyWriter() {
		this.lock = this;
	}
	
	protected MyWriter(Object lock) {
		if(lock == null)
			throw new NullPointerException();
		this.lock = lock;
	}
	
	public abstract void write(char[] buf, int off, int len) throws IOException;
	
	public void write(int c) throws IOException {
		synchronized(lock) {
			if(buffer == null)
				buffer = new char[WRITER_BUFFER_SIZE];
			buffer[0] = (char) c;
			write(buffer, 0, 1);
		}
	}
	
	public void write(char[] buff) throws IOException {
		write(buff, 0, buff.length);
	}
	
	
	public void write(String str, int off, int len) throws IOException {
		synchronized(lock) {
			if(buffer == null)
				buffer = new char[WRITER_BUFFER_SIZE];
			char[] array;
			if(len > buffer.length) {
				array = new char[len];
			}
			else {
				array = buffer;
			}
			str.getChars(off, off+len, buffer, 0);
			write(array, 0, len);
		}
	}
	
	public void write(String str) throws IOException {
		write(str, 0, str.length());
	}
	
	
	@Override
	public Appendable append(CharSequence csq) throws IOException {
		if(csq == null)
			write("null");
		else
			write(csq.toString());
		return this;
	}
	
	@Override
	public Appendable append(char c) throws IOException {
		write(c);
		return this;
	}
	
	
	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		csq = csq == null ? "null" : csq;
		write(csq.subSequence(start, end).toString());
		return this;
	}
	
	public abstract void close() throws IOException;
	
	public abstract void flush() throws IOException;

}
