package demo63;

public class CompareFloat {
	
	public static int compare(float a, float b) {
		if(a < b)
			return -1;
		if(a > b)
			return 1;
		int thisBits = Float.floatToIntBits(a);
		int anotherBits = Float.floatToIntBits(b);
		if(thisBits < anotherBits)
			return -1;
		if(thisBits > anotherBits)
			return 1;
		return 0;
	}

}
