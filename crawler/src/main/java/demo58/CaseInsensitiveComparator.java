package demo58;

import java.util.Comparator;

public class CaseInsensitiveComparator implements Comparator<String>{

	@Override
	public int compare(String o1, String o2) {
		int n1 = o1.length();
		int n2 = o2.length();
		int min = Math.min(n1, n2);
		for(int i=0; i<min; i++) {
			char c1 = o1.charAt(i);
			char c2 = o2.charAt(i);
			if(c1 != c2) {
				c1 = Character.toUpperCase(c1);
				c2 = Character.toUpperCase(c2);
				if(c1 != c2) {
					c1 = Character.toLowerCase(c1);
					c2 = Character.toLowerCase(c2);
					if(c1 != c2)
						return c1 - c2;
				}
			}
		}
		return n1 - n2;
	}
	
	
	public static int hashCode(int[] array) {
		if(array == null)
			return 0;
		int result = 1;
		for(int a : array)
			result = 31 * result + a;
		return result;
	}
	
	
	public static String toString(int[] array) {
		if(array == null)
			return "null";
		int n = array.length - 1;
		StringBuilder sb = new StringBuilder().append('[');
		for(int i=0;; i++) {
			sb.append(array[i]);
			if(i == n)
				return sb.append(']').toString();
			sb.append(", ");
		}
	}
	
	
	

}
