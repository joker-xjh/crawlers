package demo50;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCache {
	
	private static final int DEFAULT_TIMEOUT = 3600 * 1000;
	
	private static final long SECOND_TIME = 1000;
	
	private static final Map<String, Object> cache;
	
	private static final Timer timer;
	
	static {
		cache = new ConcurrentHashMap<>();
		timer = new Timer();
	}
	
	private LocalCache() {
		
	}
	
	static class CleanWorkerTask extends TimerTask {
		
		private String key;
		
		public CleanWorkerTask(String key) {
			this.key = key;
		}

		@Override
		public void run() {
			LocalCache.remove(key);
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
			timer.schedule(new CleanWorkerTask(key), timeout * SECOND_TIME);
		}
	}
	
	
	public static Object get(String key) {
		return cache.get(key);
	}
	
	public static int size() {
		return cache.size();
	}
	
	
	public static void remove(String key) {
		cache.remove(key);
	}
	

}
