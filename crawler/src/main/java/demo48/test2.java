package demo48;

import java.lang.ref.WeakReference;

public class test2 {

	public static void main(String[] args) {
		WeakReference<String[]> weakReference = new WeakReference<String[]>(new String[1000]);
		System.out.println(weakReference.get());
		System.gc();
		System.out.println(weakReference.get());
	}

}
