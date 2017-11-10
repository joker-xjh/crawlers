package demo38;

import java.util.LinkedHashMap;


public class LRUCache<K, V> extends LinkedHashMap<K, V>{
	private static final long serialVersionUID = 1L;
	
	private int capacity;
	
	public LRUCache(int capacity) {
		super(16, 0.75f, true);
		this.capacity = capacity;
	}
	
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > capacity;
	}
	
	public static void main(String[] args) {
		LRUCache<String, String> cache = new LRUCache<>(3);
		cache.put("a", "a");
		cache.put("b", "b");
		cache.put("c", "c");
		cache.put("d", "d");
		System.out.println(cache);
	}

}
