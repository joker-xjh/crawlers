package demo63;

import java.util.Arrays;

public abstract class MyAbstractStringBuilder implements Appendable, CharSequence {
	
	char[] value;
	
	int count;
	
	public MyAbstractStringBuilder(int size) {
		value = new char[size];
	}
	
	@Override
	public int length() {
		return count;
	}
	
	public int capacity() {
		return value.length;
	}
	
	public void ensureCapacity(int capacity) {
		if(capacity > 0)
			ensureCapacityInternal(capacity);
	}
	
	private void ensureCapacityInternal(int minimumCapacity) {
		if(minimumCapacity - value.length > 0)
			expandCapacity(minimumCapacity);
	}
	
	void expandCapacity(int minimumCapacity) {
		int newCapacity = value.length * 2 + 2;
		if(newCapacity - minimumCapacity < 0)
			newCapacity = minimumCapacity;
		if(newCapacity < 0) {
			if(minimumCapacity < 0)
				throw new OutOfMemoryError();
			newCapacity = Integer.MAX_VALUE;
		}
		value = Arrays.copyOf(value, newCapacity);
	}
	
	public void trimToSize() {
		if(count < value.length)
			value = Arrays.copyOf(value, count);
	}
	
	public void setLength(int newLength) {
		if(newLength < 0)
			throw new StringIndexOutOfBoundsException(newLength);
		ensureCapacityInternal(newLength);
		if(count < newLength) {
			Arrays.fill(value, count, newLength, '\0');
		}
		count = newLength;
	}
	
	@Override
	public char charAt(int index) {
		if(index < 0 || index >= count)
			throw new StringIndexOutOfBoundsException(index);
		return value[index];
	}
	
	
	public int codePointAt(int index) {
		if(index < 0 || index >= count)
			throw new StringIndexOutOfBoundsException(index);
		return Character.codePointAt(value, index, count);
	}
	
	
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if(srcBegin < 0 || srcEnd < 0 || srcEnd > count || srcBegin > srcEnd)
			throw new StringIndexOutOfBoundsException();
		System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
	}
	
	
	 public void setCharAt(int index, char ch) {
		 if(index < 0 || index >= count)
			 throw new StringIndexOutOfBoundsException();
		 value[index] = ch;
	 }
	 
	 
	 public MyAbstractStringBuilder append(String str) {
		 if(str == null)
			 return appendNull();
		 int len = str.length();
		 ensureCapacityInternal(len + count);
		 str.getChars(0, len, value, count);
		 count += len;
		 return this;
	 }
	 
	public MyAbstractStringBuilder append(MyAbstractStringBuilder sb) {
		if(sb == null)
			return appendNull();
		int len = sb.length();
		ensureCapacity(count + len);
		sb.getChars(0, len, value, count);
		count += len;
		return this;
	}
	
	private MyAbstractStringBuilder appendNull() {
		ensureCapacity(count + 4);
		value[count++] = 'n';
		value[count++] = 'u';
		value[count++] = 'l';
		value[count++] = 'l';
		return this;
	}
	
	public MyAbstractStringBuilder append(CharSequence cs, int start, int end) {
		if(cs == null)
			cs = "null";
		if(start < 0 || end >= cs.length() || start > end)
			throw new IndexOutOfBoundsException();
		int len = end - start;
		ensureCapacityInternal(count + len);
		for(int i=count, j = start; j<end ;i++,j++)
			value[i] = cs.charAt(j);
		count += len;
		return this;
	}
	
	public MyAbstractStringBuilder append(char[] array) {
		int len = array.length;
		ensureCapacityInternal(len + count);
		System.arraycopy(array, 0, value, count, len);
		count += len;
		return this;
	}
	
	
	public MyAbstractStringBuilder append(char[] array, int off, int len) {
		ensureCapacityInternal(count + len);
		System.arraycopy(array, off, value, count, len);
		count += len;
		return this;
	}
	
	
	public MyAbstractStringBuilder append(boolean b) {
		if(b) {
			ensureCapacityInternal(count+4);
			value[count++] = 't';
			value[count++] = 'r';
			value[count++] = 'u';
			value[count++] = 'e';
		}
		else {
			ensureCapacityInternal(count + 5);
			value[count++] = 'f';
			value[count++] = 'a';
			value[count++] = 'l';
			value[count++] = 's';
			value[count++] = 'e';
		}
		return this;
	}
	
	public MyAbstractStringBuilder append(char c) {
		ensureCapacityInternal(count + 1);
		value[count++] = c;
		return this;
	}
	
	public MyAbstractStringBuilder append(int i) {
		if(i == Integer.MIN_VALUE) {
			return append("-2147483648");
		}
//		int appendLength = i < 0 ? stringSize(-i) + 1 : stringSize(i);
//		int spaceNeed = count + appendLength;
//		spaceNeed++;
		return this;
	}
	
	
	final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
	
	static int stringSize(int num) {
		for(int i=0; ;i++) {
			if(num <= sizeTable[i])
				return i+1;
		}
	}
	
	
	public MyAbstractStringBuilder delete(int start, int end) {
		if(start < 0)
			throw new ArrayIndexOutOfBoundsException();
		if(end > count)
			end = count;
		if(start > count)
			throw new ArrayIndexOutOfBoundsException();
		int len = end - start;
		if(len > 0) {
			System.arraycopy(value, start+len, value, start, count - end);
			count -= len;
		}
		return this;
	}
	
	
	public MyAbstractStringBuilder deleteCharAt(int index) {
		if(index < 0 || index >= count)
			throw new ArrayIndexOutOfBoundsException();
		System.arraycopy(value, index+1, value, index, count-index-1);
		count -= 1;
		return this;
	}
	
	public String subString(int start, int end) {
		if(start < 0)
			throw new StringIndexOutOfBoundsException();
		if(end > count)
			throw new StringIndexOutOfBoundsException();
		if(start > end)
			throw new StringIndexOutOfBoundsException();
		return new String(value, start, end);
	}
	
	
	
	
	
	

}
