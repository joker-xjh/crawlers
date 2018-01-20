package demo63;

public final class MathFunctions {
	
	private MathFunctions() {
		
	}
	
	public static int add(int a, int b) {
		long c = (long)a + (long)b;
		if(c != (int)c)
			throw new ArithmeticException("integer overflow");
		return (int)c;
	}
	
	public static long mul(long a, long b) {
		long c = a * b;
		if((b != 0 && a != c/b) || (a == -1 && b == Long.MIN_VALUE))
			throw new ArithmeticException();
		return c;
	}
	
	public static long add(long a, long b) {
		long c = a + b;
		if(((a ^ c) & (b ^ c) ) < 0)
			throw new ArithmeticException(); 
		return c;
	}
	
	public static int longToInt(long value) {
		int cast = (int) value;
		if(cast != value)
			throw new ArithmeticException();
		return cast;
	}
	
	

}
