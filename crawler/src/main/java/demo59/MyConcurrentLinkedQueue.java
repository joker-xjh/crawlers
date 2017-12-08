package demo59;


@SuppressWarnings("all")
public class MyConcurrentLinkedQueue<E> {
	
	private static class Node<E> {
		volatile E item;
		volatile Node<E> next;
		
		public Node(E item) {
			UNSAFE.putObject(this, itemOffset, item);
		}
		
		boolean casItem(E old, E val) {
			return UNSAFE.compareAndSwapObject(this, itemOffset, old, val);
		}
		
		boolean casNext(Node<E> old, Node<E> val) {
			return UNSAFE.compareAndSwapObject(this, nextOffset, old, val);
		}
		
		void lazySetNext(Node<E> next) {
			UNSAFE.putObject(this, nextOffset, next);
		}
		
		 private static final sun.misc.Unsafe UNSAFE;
	     private static final long itemOffset;
	     private static final long nextOffset;
	     
	     static {
	    	 UNSAFE = sun.misc.Unsafe.getUnsafe();
	    	 Class<?> clazz = Node.class;
	    	 try {
				itemOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("item"));
				nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
			} catch (Exception e) {
				throw new Error(e);
			} 
	    	
	     }
		
	}
	
	
	private transient volatile Node<E> head;
	
	private transient volatile Node<E> tail;
	
	public MyConcurrentLinkedQueue() {
		head = tail = new Node(null);
	}
	
	
	final void updateHead(Node<E> h, Node<E> p) {
		if(h != p && casHead(h, p))
			h.lazySetNext(h);
	}
	
	final Node<E> succ(Node<E> p) {
		Node<E> next = p.next;
		return p == next ? head : next;
	}
	
	
	
	public boolean offer(E e) {
		checkNotNull(e);
		final Node<E> node = new Node(e);
		for(Node<E> t = tail, p = t;;) {
			Node<E> q = p.next;
			if(q == null) {
				if(p.casNext(null, node)) {
					if(p != t)
						casTail(t, node);
					return true;
				}
			}
			else if(p == q) {
				p = (t != (t = tail)) ? t : head;
			}
			else {
				 p = (p != t && t != (t = tail)) ? t : q;
			}
		}
	}
	
	public E poll() {
		restartFromHead:
		for(;;) {
			for(Node<E> h=head, p=h,q;;) {
				E item = p.item;
				if(item != null && p.casItem(item, null)) {
					if(p != h)
						updateHead(h, ((q = p.next) != null) ? q : p);
					return item;
				}
				else if((q = p.next) == null) {
					updateHead(h, p);
					return null;
				}
				else if(p == q)
					continue restartFromHead;
				else {
					p = q;
				}
			}
		}		
	}
	
	
	public E peek() {
		restartFromHead:
		for(;;) {
			for(Node<E> h = head, p = h, q;;) {
				E item = p.item;
				if(item != null || (q = p.next) == null) {
					updateHead(h, p);
					return item;
				}
				else if( p == q)
					continue restartFromHead;
				else
					p = q;
			}
		}
	}
	
	
	
	
	
	 private static void checkNotNull(Object v) {
	        if (v == null)
	            throw new NullPointerException();
	    }

	    private boolean casTail(Node<E> cmp, Node<E> val) {
	        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
	    }

	    private boolean casHead(Node<E> cmp, Node<E> val) {
	        return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
	    }
	
	private static final sun.misc.Unsafe UNSAFE;
	private static final long headOffset;
	private static final long tailOffset;
	static {
		UNSAFE = sun.misc.Unsafe.getUnsafe();
		Class<?> clazz = MyConcurrentLinkedQueue.class;
		try {
			headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
			tailOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tail"));
		} catch (Exception e) {
			throw new Error(e);
		} 
		
	}
	
	
	
	
	
	

}
