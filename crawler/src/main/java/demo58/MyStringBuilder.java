package demo58;

import java.util.Arrays;


public class MyStringBuilder {
	
	private char[] value;
	
	private int count;
	
	public MyStringBuilder(int capacity) {
		value = new char[capacity];
	}
	
	public MyStringBuilder() {
		this(16);
	}
	
	public MyStringBuilder(String string) {
		this(string.length() + 16);
		append(string);
	}
	
	public MyStringBuilder append(String str) {
		if(str == null)
			str = "null";
		int len = str.length();
		ensureCapacityInternal(count + len);
		str.getChars(0, len, value, count);
		count += len;
		return this;
	}
	
	private void ensureCapacityInternal(int minimumCapacity) {
		if(minimumCapacity - value.length > 0)
			expandCapacity(minimumCapacity);
	}
	
	private void expandCapacity(int minimumCapacity) {
		int newLength = value.length * 2 + 2;
		if(newLength - minimumCapacity < 0)
			newLength = minimumCapacity;
		if(newLength < 0) {
			if(minimumCapacity < 0)
				throw new OutOfMemoryError();
			newLength = Integer.MAX_VALUE;
		}
		value = Arrays.copyOf(value, newLength);
	}
	
	
	@Override
	public String toString() {
		return new String(value, 0, count);
	}
	
	
	
	public MyStringBuilder insert(int offset, String str) {
		if(offset < 0 || offset > count)
			throw new StringIndexOutOfBoundsException(offset);
		if(str == null)
			str = "null";
		int len = str.length();
		ensureCapacityInternal(len + count);
		System.arraycopy(value, offset, value, offset + len, count - offset);
		str.getChars(0, len, value, offset);
		count += len;
		return this;
	}
	
	
	public MyStringBuilder delete(int start, int end) {
		if(start < 0)
			throw new StringIndexOutOfBoundsException(start);
		if(end > count)
			end = count;
		if(start > end)
			throw new StringIndexOutOfBoundsException();
		int len = end - start;
		if(len > 0) {
			System.arraycopy(value, end, value, start, count - end);
			count -= len;
		}		
		return this;
	}
	
	
	public int capacity() {
		return value.length;
	}
	
	public int length() {
		return count;
	}
	
	public void setLength(int length) {
		if(length < 0)
			throw new StringIndexOutOfBoundsException(length);
		if(count < length) {
			for(; count < length; count++)
				value[count] = '\0';
		}
		else {
			count = length;
		}
	}
	
	public void trimToSize() {
		if(count < value.length) {
			value = Arrays.copyOf(value, count);
		}
	}
	
	
	
	

}
