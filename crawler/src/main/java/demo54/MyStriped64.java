package demo54;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongBinaryOperator;

@SuppressWarnings("all")
public abstract class MyStriped64 extends Number{
	
	static final class Cell {
		public volatile long value;
		public Cell(long value) {
			this.value = value;
		}
		private static final sun.misc.Unsafe UNSAFE;
		private static final long valueOffset;
		static {
			UNSAFE = sun.misc.Unsafe.getUnsafe();
			Class<?> clazz = Cell.class;
			try {
				valueOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("value"));
			} catch (Exception e) {
				throw new Error(e);
			}
		}
		
		final boolean cas(long cmp, long val) {
			return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
		}
	}
	
	
	static final int NCPU = Runtime.getRuntime().availableProcessors();
	
	transient volatile Cell[] cells;
	
	transient volatile long base;
	
	transient volatile int cellsBusy;
	
	
 	private static final sun.misc.Unsafe UNSAFE;
    private static final long BASE;
    private static final long CELLSBUSY;
    private static final long PROBE;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> sk = MyStriped64.class;
            BASE = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("base"));
            CELLSBUSY = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("cellsBusy"));
            Class<?> tk = Thread.class;
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    final boolean casBase(long cmp, long val) {
    	return UNSAFE.compareAndSwapLong(this, BASE, cmp, val);
    }
    
    final boolean casCellsBusy() {
    	return UNSAFE.compareAndSwapLong(this, CELLSBUSY, 0, 1);
    }
    
    
    static final int getProbe() {
        return UNSAFE.getInt(Thread.currentThread(), PROBE);
    }
    
    static final int advanceProbe(int probe) {
        probe ^= probe << 13;   
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
        return probe;
    }
    
    
   protected final void longAccumulate(long x, LongBinaryOperator fn,
            boolean wasUncontended) {
    	int h;
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); 
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false;      
        for(;;) {
        	Cell[] as; Cell a; int n; long v;
        	if((as = cells) != null && (n=as.length) > 0) {
        		if((a=as[(n-1) & h]) == null) {
        			if(cellsBusy == 0) {
        				Cell r = new Cell(x);
        				if(cellsBusy == 0 && casCellsBusy()) {
        					boolean created = false;
        					try {
								Cell[] rs; int m, j;
								if((rs=cells) != null && (m=rs.length) > 0 && rs[j=(n-1) & h] == null) {
									rs[j] = r;
									created = true;
								}
							} finally {
								cellsBusy = 0;
							}
        					if(created)
        						break;
        					continue;
        				}
        			}
        			collide = false;
        		}
        		else if(!wasUncontended)
        			wasUncontended = true;
        		else if(a.cas(v=a.value, v+x))
        			break;
        		else if(!collide)
        			collide = true;
        		else if(cellsBusy == 0 && casCellsBusy()) {
        			try {
						if(as == cells) {
							Cell[] rs = new Cell[n<<1];
							for(int i=0; i<rs.length; i++)
								rs[i] = as[i];
							cells = rs;
						}
					} finally {
						cellsBusy = 0;
					}
        			collide = false;
        			continue;
        		}
        		h = advanceProbe(h);
        	}
        	else if(cellsBusy == 0 && as == cells && casCellsBusy()) {
        		boolean init = false;
        		try {
					if(cells == as) {
						Cell[] rs = new Cell[2];
						rs[h & 1] = new Cell(x);
						cells = rs;
						init = true;
					}
				} finally {
					cellsBusy = 0;
				}
        		if(init)
        			break;
        	}
        	else if(casBase(v=base, v+x))
        		break;
        }
    	
    }
    
    
    
    
    
    
    
	

}
