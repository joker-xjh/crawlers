package objectpool.interfaces;

import java.util.concurrent.TimeUnit;

public interface BlockingPool<T> extends Pool<T>{
	
	T get();
	
	T get(long time, TimeUnit timeUnit);

}
