package demo56;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer <K, V>{

	private final ConcurrentMap<K, Future<V>> cache = new ConcurrentHashMap<>();
	
	public V compute(K key) throws InterruptedException {
		while(true) {
			Future<V> future = cache.get(key);
			if(future == null) {
				Callable<V> callable = new Callable<V>() {
					@Override
					public V call() throws Exception {
						return null;
					}
				};
				FutureTask<V> futureTask = new FutureTask<>(callable);
				future = cache.putIfAbsent(key, futureTask);
				if(future == null) {
					future = futureTask;
					futureTask.run();
				}
					
			}
			try {
				return future.get();
			} catch (ExecutionException e) {
				cache.remove(key);
				e.printStackTrace();
			}
		}
	}
	
}
