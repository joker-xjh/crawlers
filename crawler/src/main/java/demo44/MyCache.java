package demo44;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MyCache<K, V> {
	
	private final int size;
	
	private final Map<K, V> eden;
	
	private final Map<K, V> longterm;
	
	
	public MyCache(int size) {
		this.size = size;
		eden = new ConcurrentHashMap<>(size);
		longterm = new WeakHashMap<>(size);
	}
	
	public V get(K key) {
		V v = eden.get(key);
		if(v == null) {
			synchronized (longterm) {
				v = longterm.get(key);
			}
			if(v != null) {
				eden.put(key, v);
			}
		}
		return v;
	}
	
	
	public void put(K key, V value) {
		if(eden.size() >= size) {
			synchronized (longterm) {
				longterm.putAll(eden);
			}
			eden.clear();
		}
		eden.put(key, value);
	}

}
