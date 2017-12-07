package demo58;

import java.util.Arrays;

public class MoreObjects {
	
	public static final class ToStringHelper {
		
		private static final class ValueHolder {
			String name;
			Object value;
			ValueHolder next;
		}
		
		private final String className;
		
		private final ValueHolder head = new ValueHolder();
		
		private ValueHolder tail = head;
		
		public ToStringHelper(String className) {
			this.className = className;
		}
		
		
		private ValueHolder addHolder() {
			ValueHolder valueHolder = new ValueHolder();
			tail = tail.next = valueHolder;
			return valueHolder;
		}
		
		@SuppressWarnings("unused")
		private ToStringHelper addHolder(Object value) {
			ValueHolder valueHolder = addHolder();
			valueHolder.value = value;
			return this;
		}
		
		@SuppressWarnings("unused")
		private ToStringHelper addHolder(String name, Object value) {
			ValueHolder valueHolder = addHolder();
			valueHolder.name = name;
			valueHolder.value = value;
			return this;
		}
		
		@Override
		public String toString() {
			String nextSeparator = "";
			StringBuilder sb = new StringBuilder(32).append(className).append('{');
			for(ValueHolder valueHolder = head.next;
					valueHolder != null;
					valueHolder = valueHolder.next) {
				Object value = valueHolder.value;
				if(value != null) {
					sb.append(nextSeparator);
					nextSeparator = ", ";
					if(valueHolder.name != null) {
						sb.append(valueHolder.name).append('=');
					}
					if(value.getClass().isArray()) {
						Object[] array = {value};
						String stringArray = Arrays.deepToString(array);
						sb.append(stringArray, 1, stringArray.length()-1);
					}
					else {
						sb.append(value);
					}
				}
			}
			
			return sb.append('}').toString();
		}
		
		
		
	}
	
	
	public static String padStart(String string, int minLength, char padChar) {
		if(string == null)
			throw new NullPointerException();
		if(string.length() >= minLength)
			return string;
		StringBuilder sb = new StringBuilder(minLength);
		for(int i=string.length(); i<minLength; i++)
			sb.append(padChar);
		sb.append(string);
		return sb.toString();
	}
	
	public static String padEnd(String string, int minLength, char padChar) {
		if(string == null)
			throw new NullPointerException();
		if(string.length() >= minLength)
			return string;
		StringBuilder sb = new StringBuilder(minLength);
		sb.append(string);
		for(int i=string.length(); i<minLength; i++)
			sb.append(padChar);
		return sb.toString();
	}
	
	
	public static String repeat(String string, int count) {
		if(string == null)
			throw new NullPointerException();
		if(count == 1)
			return string;
		if(count == 0)
			return "";
		if(count < 0)
			throw new IllegalArgumentException();
		final int len = string.length();
		final long longSize = (long)len * (long)count;
		final int size = (int)longSize;
		if(size != longSize)
			throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
		final char[] array = new char[size];
		string.getChars(0, len, array, 0);
		int n;
		for(n=len; n < size-n; n<<=1)
			System.arraycopy(array, 0, array, n, n);
		System.arraycopy(string, 0, array, n, size-n);
		return new String(array);
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
