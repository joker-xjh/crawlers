package demo21;

import java.util.concurrent.ConcurrentHashMap;

public class FIFOCache<K,V> extends AbstractCache<K, V>{
	
	private ConcurrentHashMap<K, Node> cache;
	
	private int capacity;
	
	private boolean timeout;
	
	private OrderNode head;
	private OrderNode tail;
	
	public FIFOCache(int capacity, boolean timeout) {
		this.capacity = capacity;
		if(this.capacity < 16)
			this.capacity = 16;
		cache = new ConcurrentHashMap<>(capacity);
		head = new OrderNode(null, null, -1);
		tail = new OrderNode(null, null, -1);
		this.timeout = timeout;
		head.setNext(tail);
		tail.setPre(head);
	}

	@Override
	public void put(K key, V value) {
		put(key, value, -1);
	}
	
	public void put(K key, V value, long timeout) {
		if(!this.timeout && timeout == -1)
			throw new IllegalArgumentException();
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
		OrderNode node = new OrderNode(key, value, timeout);
		push(node);
		cache.put(key, node);
	}
	
	private void free(OrderNode node) {
		cache.remove(node.getKey());
		pop(node);
	}
	
	private void push(OrderNode node) {
		node.setNext(tail);
		node.setPre(tail.getPre());
		tail.getPre().setNext(node);
		tail.setPre(node);
	}
	
	private Node pop(OrderNode node) {
		node.getPre().setNext(node.getNext());
		node.getNext().setPre(node.getPre());
		node.setPre(null);
		node.setNext(null);
		return node;
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
		return (V) node.getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(K key) {
		OrderNode node = (OrderNode) cache.get(key);
		V val = (V) node.getValue();
		cache.remove(key);
		pop(node);
		return val;
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
