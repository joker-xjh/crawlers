package demo8;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class ThreadPool2 {
	
	private volatile boolean shutdown;
	
	private int core_size;
	
	private int max_size;
	
	private int queue_size;
	
	private int keep_alive;
	
	private BlockingQueue<Runnable> tasks;
	
	private Set<Worker> workers;
	
	
	public ThreadPool2(int core, int max, int queue, int alive) {
		this.core_size = core;
		this.max_size = max;
		this.queue_size = queue;
		this.keep_alive = alive;
		tasks = new LinkedBlockingQueue<>();
		workers = new HashSet<>();
	}
	
	public void execute(Runnable runnable) {
		if(workers.size() < core_size) {
			addWorker(runnable);
		}
		else if(tasks.size() < queue_size) {
			tasks.add(runnable);
		}
		else if(tasks.size() >= queue_size && workers.size() < max_size) {
			addWorker(runnable);
		}
		else {
			throw new RuntimeException("线程超过了" + max_size);
		}
	}
	
	private void addWorker(Runnable runnable) {
		Worker worker = new Worker("pool-thread-"+workers.size(), runnable);
		workers.add(worker);
		worker.start();
	}
	
	
	
	private Runnable getTask() {
		if(tasks.isEmpty() && shutdown)
			return null;
		boolean time = workers.size() > core_size;
		try {
			return time ? tasks.poll(keep_alive, TimeUnit.SECONDS) : tasks.take();
		} catch (InterruptedException e) {
				
		}
		return null;
	}
	
	public boolean shutdown() {
		shutdown = true;
		return workers.isEmpty() && tasks.isEmpty();
	}
	
	private class Worker extends Thread{
		
		@SuppressWarnings("unused")
		private String name;
		
		private Runnable runnable;
		
		public Worker(String name, Runnable runnable) {
			super(name);
			this.name = name;
			this.runnable = runnable;
		}
		
		@Override
		public void run() {
			while(runnable != null || (runnable = getTask()) != null) {
				runnable.run();
				runnable = null;
			}
			workers.remove(this);
		}
		
		
	}
	
	
	
	

}
