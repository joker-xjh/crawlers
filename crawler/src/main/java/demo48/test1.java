package demo48;

import java.lang.ref.SoftReference;


public class test1 {

	public static void main(String[] args) {
		SoftReference<int[]> softReference = new SoftReference<int[]>(new int[100000000]);
		System.gc();
		System.out.println(softReference.get());
		@SuppressWarnings("unused")
		int[] a = new int[100000000];
		System.out.println(softReference.get());
	}

}
