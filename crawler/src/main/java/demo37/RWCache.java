package demo37;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RWCache {
	
	private Map<String, Object> cache = new HashMap<>();
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public Object getData(String key) {
		Object  value = null;
		lock.readLock().lock();
		try {
			value = cache.get(key);
			if(value == null) {
				lock.writeLock().lock();
				try {
					value = "kobe";
					cache.put(key, value);
				} finally {
					lock.writeLock().unlock();
				}
			}
		} finally {
			lock.readLock().unlock();
		}
		return value;
	}

}
