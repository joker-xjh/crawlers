package demo21;

import java.util.concurrent.ConcurrentHashMap;

public class LRUCache<K, V> extends AbstractCache<K, V>{
	
	private ConcurrentHashMap<K, Node> cache;
	
	private int capacity;
	
	private boolean timeout;
	
	private OrderNode head;
	
	private OrderNode tail;
	
	public LRUCache() {
		this(16, true);
	}
	
	public LRUCache(int capacity, boolean timeout) {
		this.capacity = capacity;
		if(this.capacity < 16)
			this.capacity = 16;
		this.timeout = timeout;
		cache = new ConcurrentHashMap<>(capacity);
		head = new OrderNode(null, null, -1);
		tail = new OrderNode(null, null, -1);
		head.setNext(tail);
		tail.setPre(head);
	}
	
	private void free(OrderNode node) {
		cache.remove(node.getKey());
		pop(node);
	}
	
	private void pop(OrderNode node) {
		node.getNext().setPre(node.getPre());
		node.getPre().setNext(node.getNext());
		node.setPre(null);
		node.setNext(null);
	}
	
	private void moveToTail(OrderNode node) {
		node.setNext(tail);
		node.setPre(tail.getPre());
		tail.getPre().setNext(node);
		tail.setPre(node);
	}
	
	

	@Override
	public void put(K key, V value) {
		this.put(key, value, -1);
	}
	
	public void put(K key, V value, long timeout) {
		 if (this.timeout == false && timeout != -1){
	            throw new UnsupportedOperationException();
	        }
		 if(cache.size() >= capacity) {
			 OrderNode old = head.getNext();
			 if(this.timeout) {
				 while(old.getNext() != null) {
					 if(old.isTimeout()) {
						 free(old);
					 }
					 old = old.getNext();
				 }
			 }
			 if(cache.size() >= capacity) {
				 OrderNode remove = head.getNext();
				 free(remove);
			 }
		 }
		 OrderNode node = null;
		 if(cache.contains(key)) {
			 node = (OrderNode) cache.get(key);
		 }
		 else {
			 node = new OrderNode(key, value, timeout);
		 }
		 moveToTail(node);
		 cache.put(key, node);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		OrderNode node = (OrderNode) cache.get(key);
		if(node == null)
			return null;
		if(this.timeout) {
			if(node.isTimeout()) {
				free(node);
				return null;
			}
		}
		V val = (V) node.getValue();
		moveToTail(node);
		return val;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(K key) {
		OrderNode node = (OrderNode) cache.get(key);
		cache.remove(key);
		free(node);
		return (V) node.getValue();
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	protected ConcurrentHashMap<K, Node> cacheMap() {
		return cache;
	}

}
