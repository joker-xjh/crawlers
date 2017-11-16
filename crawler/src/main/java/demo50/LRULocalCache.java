package demo50;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRULocalCache {
	
	private static final int DEFAULT_TIMEOUT = 3600 * 1000;
	
	private static final int SECOND_TIME = 1000;
	
	private static final Map<String, Object> cache;
	
	private static final Timer timer;
	
	static {
		cache = new LRUMap<>(16);
		timer = new Timer();
	}
	
	
	
	static class LRUMap<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = 1L;
		
		private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		
		private final Lock readLock = readWriteLock.readLock();
		
		private final Lock writeLock = readWriteLock.writeLock();
		
		private int capacity = 16;
		
		public LRUMap(int capacity) {
			super(capacity, 0.75f, true);
			this.capacity = capacity;
		}
		
		
		@Override
		public V get(Object key) {
			readLock.lock();
			try {
				return super.get(key);
			} finally {
				readLock.unlock();
			}
		}
		
		@Override
		public V put(K key, V value) {
			writeLock.lock();
			try {
				return super.put(key, value);
			} finally {
				writeLock.unlock();
			}
		}
		
		
		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			writeLock.lock();
			try {
				super.putAll(m);
			} finally {
				writeLock.unlock();
			}
		}
		
		@Override
		public V remove(Object key) {
			writeLock.lock();
			try {
				return super.remove(key);
			} finally {
				writeLock.unlock();
			}
		}
		
		@Override
		public boolean containsKey(Object key) {
			readLock.lock();
			try {
				return super.containsKey(key);
			} finally {
				readLock.unlock();
			}	
		}
		
		@Override
		public int size() {
			readLock.lock();
			try {
				return super.size();
			} finally {
				readLock.unlock();
			}
		}
		
		
		@Override
		public void clear() {
			writeLock.lock();
			try {
				super.clear();
			} finally {
				writeLock.unlock();
			}
		}
		
		
		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
			return this.size() > capacity;
		}
		
	}
	
	
	static class CleanWorkerTask extends TimerTask {
		
		private String key;
		
		public CleanWorkerTask(String key) {
			this.key = key;
		}

		@Override
		public void run() {
			LRULocalCache.remove(key);
		}
		
	}
	
	public static void put(String key, Object value) {
		cache.put(key, value);
		timer.schedule(new CleanWorkerTask(key), DEFAULT_TIMEOUT);
	}
	
	public static void put(String key, Object value, int timeout) {
		cache.put(key, value);
		timer.schedule(new CleanWorkerTask(key), timeout * SECOND_TIME);
	}
	
	public static void putAll(Map<String, Object> map) {
		cache.putAll(map);
		for(String key : map.keySet()) {
			timer.schedule(new CleanWorkerTask(key), DEFAULT_TIMEOUT);
		}
	}
	
	public static void putAll(Map<String, Object> map, int timeout) {
		cache.putAll(map);
		for(String key : map.keySet()) {
			timer.schedule(new CleanWorkerTask(key),  timeout * SECOND_TIME);
		}
	}
	
	public static Object get(String key) {
		return cache.get(key);
	}
	
	public static int size() {
		return cache.size();
	}
	
	public static boolean containsKey(String key) {
		return cache.containsKey(key);
	}
	
	public static void clear() {
		if(size() > 0) {
			cache.clear();
		}
		timer.cancel();
	}
	
	
	public static void remove(String key) {
		cache.remove(key);
	}
	
}
