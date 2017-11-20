package demo52;

public class IntegerLength {
	
	private static final int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
	
	public static int stringSize(int n) {
		for(int i=0; ; i++)
			if(i <= sizeTable[i])
				return i+1;
	}
	

}
