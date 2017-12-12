package demo62;

import sun.misc.Unsafe;

@SuppressWarnings("all")
public class MyAtomicIntegerArray {
	
	private final int[] array;
	private final static Unsafe unsafe = Unsafe.getUnsafe();
	private static final int base = unsafe.arrayBaseOffset(int[].class);
	private static final int shift = 2;
	
	public MyAtomicIntegerArray(int length) {
		array = new int[length];
	}
	
	public MyAtomicIntegerArray(int[] array) {
		this.array = array.clone();
	}
	
	private static long byteOffset(int i) {
		return ((long)i << shift) + base;
	}
	
	private long checkedByteOffset(int i) {
		if(i < 0 || i >= array.length)
			throw new ArrayIndexOutOfBoundsException("index "+i);
		return byteOffset(i);
	}
	
	public final int length() {
		return array.length;
	}
	
	private int getRaw(long offset) {
		return unsafe.getInt(array, offset);
	}
	
	public int get(int i) {
		return getRaw(checkedByteOffset(i));
	}
	
	public final void set(int i, int newValue) {
		unsafe.putIntVolatile(array, checkedByteOffset(i), newValue);
	}
	
	public final int getAndSet(int i, int newValue) {
		return unsafe.getAndSetInt(array, checkedByteOffset(i), newValue);
	}
	
	public final boolean compareAndSet(int i, int old, int newValue) {
		return unsafe.compareAndSwapInt(array, checkedByteOffset(i), old, newValue);
	}
	
	public final int getAndAdd(int i, int add) {
		return unsafe.getAndAddInt(array, checkedByteOffset(i), add);
	}
	
	@Override
	public String toString() {
		int length = array.length - 1;
		if(length == -1)
			return "[]";
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(int i=0; ;i++) {
			sb.append(get(i));
			if(i == length)
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}
	

}
