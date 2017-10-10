package demo26;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CopyOnWriteMap<K, V> implements Map<K, V> {
	
	private volatile Map<K, V> map = new HashMap<>();

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.values().contains(value);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public V put(K key, V value) {
		synchronized (this) {
			Map<K, V> newMap = new HashMap<>(map);
			V val = newMap.put(key, value);
			map = newMap;
			return val;
		}
	}

	@Override
	public V remove(Object key) {
		synchronized (this) {
			Map<K, V> newMap = new HashMap<>(map);
			V val = newMap.remove(key);
			map = newMap;
			return val;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		synchronized (this) {
			Map<K, V> newMap = new HashMap<>(map);
			newMap.putAll(m);
			map = newMap;
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

}
