package demo36;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RWDictionary {
	private Map<String, String> map = new HashMap<>();
	
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private Lock read = lock.readLock();
	
	private Lock write = lock.writeLock();
	
	public String get(String key) {
		read.lock();
		try {
			return map.get(key);
		} finally {
			read.unlock();
		}
	}
	
	public void put(String key, String value) {
		write.lock();
		try {
			map.put(key, value);
		} finally {
			write.unlock();
		}
	}
	
	public int size() {
		write.lock();
		try {
			return map.size();
		} finally {
			write.unlock();
		}
	}
	
	
	public void clear() {
		write.lock();
		try {
			map.clear();
		} finally {
			write.unlock();
		}
	}
	
	
	

}
