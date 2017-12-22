package demo21;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MyExpiringCache {

	private long millisUntilExpiration;
	
	private Map<String, Entry> cache;
	
	private int queryCount;
	
	private int queryOverflow = 300;
	
	private int maxEntryCount = 200;
	
	public MyExpiringCache(long expiration) {
		this.millisUntilExpiration = expiration;
		cache = new LinkedHashMap<String, Entry>() {
			private static final long serialVersionUID = 1L;
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, demo21.MyExpiringCache.Entry> eldest) {
				return size() > maxEntryCount;
			}
		};
	}
	
	public MyExpiringCache() {
		this(30000);
	}
	
	private Entry entryFor(String key) {
		Entry entry = cache.get(key);
		if(entry != null) {
			long time = System.currentTimeMillis() - entry.getTimestamp();
			if(time > millisUntilExpiration) {
				cache.remove(key);
				entry = null;
			}
		}
		return entry;
	}
	
	private void cleanup() {
		Set<String> keys = cache.keySet();
		String[] array = new String[keys.size()];
		int i=0;
		for(String key : keys) {
			array[i++] = key;
		}
		for(String key : array) {
			entryFor(key);
		}
		queryCount = 0;
	}
	
	
	public synchronized void clear() {
		cache.clear();
	}
	
	public synchronized void put(String key, Object val) {
		if(++queryCount > queryOverflow) {
			cleanup();
		}
		Entry entry = cache.get(key);
		if(entry != null) {
			entry.setVal(val);
			entry.setTimestamp(System.currentTimeMillis());
		}
		else {
			cache.put(key, new Entry(System.currentTimeMillis(), val));
		}
	}
	
	public synchronized Object get(String key) {
		if(++queryCount > queryOverflow)
			cleanup();
		Entry entry = cache.get(key);
		if(entry != null) {
			entry.setTimestamp(System.currentTimeMillis());
			return entry.getVal();
		}
		return null;
	}
	
	
	
	static class Entry {
		private long timestamp;
		private Object val;
		
		public Entry(long timestamp, Object val) {
			this.timestamp = timestamp;
			this.val = val;
		}
		
		public long getTimestamp() {
			return timestamp;
		}
		
		public Object getVal() {
			return val;
		}
		
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		
		public void setVal(Object val) {
			this.val = val;
		}
		
	}
	
}
