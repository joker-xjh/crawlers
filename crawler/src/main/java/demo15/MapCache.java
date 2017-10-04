package demo15;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache {
	
	private static final int DEFAULT_CACHES = 1024;
	
	private Map<String, CacheObject> cache ;
	
	public MapCache(int capacity) {
		cache = new ConcurrentHashMap<>(capacity);
	}
	
	public MapCache() {
		this(DEFAULT_CACHES);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		CacheObject cacheObject = cache.get(key);
		if(cacheObject != null) {
			long now = System.currentTimeMillis()/1000;
			if(cacheObject.getExpired()<=0 || cacheObject.getExpired() > now) {
				return (T)cacheObject.getValue();
			}
		}
		return null;
	}
	
	
	public <T> T get(String key, String field) {
		key = key+":"+field;
		return this.get(key);
	}
	
	
	public void put(String key, Object value) {
		this.put(key, value, -1);
	}
	
	public void put(String key, Object value, long expired) {
		expired = expired > 0 ? expired/1000 : expired;
		CacheObject cacheObject = new CacheObject(key, value, expired);
		cache.put(key, cacheObject);
	}
	
	
	public void hput(String key, Object value, String field, long expired) {
		key += ":" + field;
		expired = expired > 0 ? expired/1000 : expired;
		CacheObject cacheObject = new CacheObject(key, value, expired);
		cache.put(key, cacheObject);
	}
	
	public void hput(String key, Object value, String field) {
		this.hput(key, value, field, -1);
	}
	
	
	public void remove(String key) {
		cache.remove(key);
	}
	
	public void remove(String key, String field) {
		key += ":" + field;
		this.remove(key);
	}
	
	public void clear() {
		cache.clear();
	}
	
	
	private static class CacheObject{
		private String key;
		private Object value;
		private long expired;
		
		public CacheObject(String key, Object value, long expired) {
			this.expired = expired;
			this.key = key;
			this.value = value;
		}
		
		public long getExpired() {
			return expired;
		}
		
		@SuppressWarnings("unused")
		public String getKey() {
			return key;
		}
		
		public Object getValue() {
			return value;
		}
		
	}
	

}
