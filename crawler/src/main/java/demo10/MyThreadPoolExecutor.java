package demo10;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("all")
public class MyThreadPoolExecutor extends AbstractExecutorService{
	
	volatile int runState;
	
	static final int RUNNING = 0;
	static final int SHUTDOWN = 1;
	static final int STOP = 2;
	static final int TERMINATED = 3;
	
	private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
	private final ReentrantLock mainLock = new ReentrantLock();
	private final HashSet<Worker> workers = new HashSet<>();
	
	private volatile long keepAliveTime;
	private volatile boolean allowCoreThreadTimeOut;
	
	private volatile int corePoolSize;
	private volatile int maxPoolSize;
	
	private volatile int poolSize;
	
	private volatile RejectedExecutionHandler handler;
	
	private volatile ThreadFactory threadFactory;
	
	private int largestPoolSize;
	
	private long completedTaskCount;
	
	
	
	
	

	

	@Override
	public void shutdown() {
		runState = SHUTDOWN;
	}

	@Override
	public List<Runnable> shutdownNow() {
		
		return null;
	}

	@Override
	public boolean isShutdown() {
		
		return runState >= SHUTDOWN;
	}

	@Override
	public boolean isTerminated() {
		
		return runState == TERMINATED;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		
		return false;
	}

	@Override
	public void execute(Runnable command) {
		if(command == null)
			throw new NullPointerException();
		if(poolSize >= corePoolSize || !addIfUnderCorePoolSize(command)) {
			if(runState == RUNNING && workQueue.offer(command)) {
				if(runState != RUNNING || poolSize == 0 ) {
					//ensureQueuedTaskHandled(command);
				}
			}
			else if(!addIfUnderMaximumPoolSize(command)) {
				 reject(command);
			}
		}
		
	}
	
	final void reject(Runnable runnable) {
		//handler.rejectedExecution(runnable, this);
	}
	
	
	private boolean addIfUnderCorePoolSize(Runnable firstTask) {
		Thread t = null;
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try {
			if(poolSize < corePoolSize && runState == RUNNING)
				t = addThread(firstTask);
		} finally {
			mainLock.unlock();
		}
		if(t==null)
			return false;
		t.start();
		return true;
	}
	
	private Thread addThread(Runnable task) {
		Worker worker = new Worker(task);
		Thread t = threadFactory.newThread(task);
		if(t!= null) {
			worker.thread = t;
			workers.add(worker);
			int n = ++poolSize;
			if(n>largestPoolSize)
				largestPoolSize = n;
		}
		return t;
	}
	
	Runnable getTask() {
		while(true) {
			try {
				int state = runState;
				if(state > SHUTDOWN)
					return null;
				Runnable r;
				if(state == SHUTDOWN)
					r=workQueue.poll();
				else if(poolSize > corePoolSize || allowCoreThreadTimeOut)
					r = workQueue.poll(keepAliveTime, TimeUnit.MILLISECONDS);
				else 
					r = workQueue.take();
				if(r != null)
					return r;
				
				if(workerCanExit()) {
					if(state >= SHUTDOWN) {
						interruptIdleWorkers(); 
					}
					return null;
				}
				
			} catch (InterruptedException  e) {
				
			}
		}
	}
	
	
	private boolean workerCanExit() {
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		boolean canExit = false;;
		try {
			canExit = runState >= STOP || workQueue.isEmpty() || (allowCoreThreadTimeOut && poolSize > Math.max(1, corePoolSize));
		} finally {
			mainLock.unlock();
		}
		return canExit;
	}
	
	void interruptIdleWorkers() {
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try {
			for(Worker worker : workers)
				worker.interruptIfIdle();
		} finally {
			mainLock.unlock();
		}
	}
	
	
	private boolean addIfUnderMaximumPoolSize(Runnable firstTask) {
		ReentrantLock mainLock = this.mainLock;
		Thread t = null;
		mainLock.lock();
		try {
			if(poolSize < maxPoolSize && runState == RUNNING)
				t = addThread(firstTask);
		} finally {
			mainLock.unlock();
		}
		if(t== null)
			return false;
		t.start();
		return true;
	}
	
	
	public boolean preStartCoreThread() {
		return addIfUnderCorePoolSize(null);
	}
	
	public int preStartAllCoreThread() {
		int n = 0;
		while(addIfUnderCorePoolSize(null))
			n++;
		return n;
	}
	
	
	
	
	
	private final class Worker implements Runnable {
		
		private ReentrantLock runLock = new ReentrantLock();
		private Runnable firstTask;
		volatile long completedTasks;
		Thread thread;
		public Worker(Runnable runnable) {
			this.firstTask = runnable;
		}
		
		boolean isActive() {
			return runLock.isLocked();
		}
		
		void interruptIfIdle() {
			 final ReentrantLock runLock = this.runLock;
			 if(runLock.tryLock()) {
				 try {
					if(thread != Thread.currentThread())
						thread.interrupt();
				} finally {
					runLock.unlock();
				}
			 }
		}
		
		void interruptNow() {
			interruptIfIdle();
		}
		
		
		
		@Override
		public void run() {
			try {
				Runnable task = firstTask;
				firstTask = null;
				while(task != null || (task = getTask())!=null) {
					task.run();
					task = null;
				}
			} finally {
				workers.remove(this);
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
