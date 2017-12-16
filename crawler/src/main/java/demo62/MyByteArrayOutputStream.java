package demo62;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MyByteArrayOutputStream extends OutputStream{
	
	protected byte[] buf;
	
	protected int count;
	
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	public MyByteArrayOutputStream(int size) {
		if(size < 0)
			throw new IllegalArgumentException();
		buf = new byte[size];
	}
	
	public MyByteArrayOutputStream() {
		this(32);
	}
	
	private void ensureCapacity(int minCapacity) {
		if(minCapacity - buf.length > 0)
			grow(minCapacity);
	}
	
	private void grow(int minCapacity) {
		int oldCapacity = buf.length;
		int newCapacity = oldCapacity << 1;
		if(minCapacity > newCapacity)
			newCapacity = minCapacity;
		if(newCapacity > MAX_ARRAY_SIZE)
			newCapacity = hugeCapacity(newCapacity);
		buf = Arrays.copyOf(buf, newCapacity);
	}
	
	 private static int hugeCapacity(int minCapacity) {
		 if(minCapacity < 0)
			 throw new OutOfMemoryError();
		 return minCapacity > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	 }
	
	

	@Override
	public synchronized void write(int b) throws IOException {
		ensureCapacity(count + 1);
		buf[count] = (byte) b;
		count += 1;
	}
	
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		if(off < 0 || off > b.length || len < 0 || len > b.length - off)
			throw new ArrayIndexOutOfBoundsException();
		ensureCapacity(count + len);
		System.arraycopy(b, off, buf, count, len);
		count += len;
	}
	
	public synchronized void wirteTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}
	
	public synchronized void reset() {
		count = 0;
	}
	
	public synchronized byte[] toByteArray() {
		return Arrays.copyOf(buf, count);
	}
	
	public synchronized int size() {
		return count;
	}
	
	public synchronized String toString() {
		return new String(buf, 0, count);
	}
	
	public synchronized String toString(String charsetName) throws UnsupportedEncodingException {
		return new String(buf, 0, count, charsetName);
	}

}
