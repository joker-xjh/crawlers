package demo62;

import java.util.Arrays;
import java.util.Comparator;

public final class MyObjects {

	private MyObjects() {
		
	}
	
	public static boolean equals(Object a, Object b) {
		return ( a == b ) || (a != null && a.equals(b));
	}
	
	public static int hashCode(Object o) {
		return o == null ? 0 : o.hashCode();
	}
	
	public static int hash(Object...objects) {
		return Arrays.hashCode(objects);
	}
	
	public static String toString(Object o) {
		return String.valueOf(o);
	}
	
	public static String toString(Object o, String defaultMessage) {
		return o != null ? o.toString() : defaultMessage;
	}
	
	public static <T> int compare(T a, T b, Comparator<? super T> c) {
		return a == b ? 0 : c.compare(a, b);
	}
	
	public static <T> T requireNonNull(T o) {
		if(o == null)
			throw new NullPointerException();
		return o;
	}
	
	public static <T> T requireNonNull(T o, String message) {
		if(o == null)
			throw new NullPointerException(message);
		return o;
	}
	
	public static boolean isNull(Object o) {
		return o == null;
	}
	
	public static boolean isNonNull(Object o) {
		return o != null;
	}
	
}
