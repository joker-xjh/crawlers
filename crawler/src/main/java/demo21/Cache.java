package demo21;

public interface Cache<K,V> {
	
	void put(K key, V value);
	
	V get(K key);
	
	int size();
	
	boolean contains(K key);
	
	V remove(K key);
	
	void clear();
	
	boolean isEmpty();

}
