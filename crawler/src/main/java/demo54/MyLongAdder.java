package demo54;


public class MyLongAdder extends MyStriped64 {

	private static final long serialVersionUID = 1L;
	
	public MyLongAdder() {
		
	}
	
	public void add(long x) {
		Cell[] as; long b, v; int m; Cell a;
		if((as = cells) != null || !casBase(b=base, b+x)) {
			boolean unceontented = true;
			if(as == null || (m=as.length-1)<0
				|| (a=as[m & getProbe()]) == null || !(unceontented=a.cas(v=a.value, v+x))) {
				longAccumulate(x, null, unceontented);
			}
				
		}
	}
	
	public long sum() {
		Cell[] as; Cell a;
		long sum = base;
		if((as = cells) != null) {
			for(int i=0; i<as.length; i++) {
				if((a=as[i]) != null)
					sum += a.value;
			}
		}
		return sum;
	}
	

	@Override
	public int intValue() {
		return (int) sum();
	}

	@Override
	public long longValue() {
		return sum();
	}

	@Override
	public float floatValue() {
		return (float)sum();
	}

	@Override
	public double doubleValue() {
		return (double)sum();
	}

	

}
