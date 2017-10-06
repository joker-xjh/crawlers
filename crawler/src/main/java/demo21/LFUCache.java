package demo21;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache<K, V> {
	
	private class Node {
		V value;
		long timestamp;
		long start;
		
		public Node(K key, V value, long timestamp) {
			this.value = value;
			start = System.currentTimeMillis();
			this.timestamp = timestamp;
		}
		
		public void setValue(V value) {
			this.value = value;
		}
		
		public V getValue() {
			return value;
		}
		
		public boolean isTimeout() {
			if(timestamp == -1)
				return false;
			long now = System.currentTimeMillis();
			if(start + timestamp > now)
				return false;
			return true;
		}
	}
	
	private Map<K, Node> LFU;
	private Map<K, Integer> counter;
	private Map<Integer, LinkedHashSet<K>> fre;
	private int capacity;
	private boolean timeout;
	private int min = -1;
	
	public LFUCache(int capacity, boolean timeout) {
		this.capacity = capacity;
		if(this.capacity < 16)
			this.capacity = 16;
		this.timeout = timeout;
		LFU = new HashMap<>();
		counter = new HashMap<>();
		fre = new HashMap<>();
		fre.put(1, new LinkedHashSet<K>());
	}
	
	public V get(K key) {
		if(LFU.containsKey(key)) {
			Node node = LFU.get(key);
			if(this.timeout && node.isTimeout()) {
				int count = counter.get(key);
				fre.get(count).remove(key);
				counter.remove(key);
				LFU.remove(key);
				return null;
			}
			V val = node.getValue();
			int count = counter.get(key);
			counter.put(key, count+1);
			fre.get(count).remove(key);
			if(count == min && fre.get(count).size() == 0)
				min++;
			if(fre.get(count+1) == null)
				fre.put(count+1, new LinkedHashSet<K>());
			fre.get(count+1).add(key);
			return val;
		}
		return null;
	}
	
	
	public void put(K key, V value) {
		
	}
	
	public void put(K key, V value, long timestamp) {
		if(!this.timeout && timestamp != -1) {
			throw new IllegalStateException();
		}
		if(LFU.containsKey(key)) {
			LFU.get(key).setValue(value);
			get(key);
			return;
		}
		
		if(LFU.size() >= capacity) {
			K remove = fre.get(min).iterator().next();
			LFU.remove(remove);
			counter.remove(remove);
			fre.get(min).remove(remove);
		}
		min = 1;
		Node node = new Node(key, value, timestamp);
		LFU.put(key, node);
		counter.put(key, 1);
		fre.get(1).add(key);
	}
	
	
	
	
	
	

}
