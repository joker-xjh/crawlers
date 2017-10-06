package demo21;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCache<K,V> implements Cache<K, V>{

	protected abstract ConcurrentHashMap<K, Node> cacheMap();
	
	@Override
	public int size() {
		return cacheMap().size();
	}
	
	@Override
	public boolean contains(K key) {
		return cacheMap().contains(key);
	}
	
	@Override
	public boolean isEmpty() {
		return cacheMap().isEmpty();
	}
	
	
	
}
