package demo7;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManagerTemp {
	
	private static final int POOL_CORE_SIZE = 5;
	
	private static final int POOL_MAX_SIZE = 10;
	
	private static final int WORK_QUEUE_SIZE = 50;
	
	private static final int KEEP_ALIVE = 1000;
	
	private static final int SCHEDULE_TIME = 1000;
	
	
	private static final Queue<Runnable> buffer_queue = new LinkedList<>();
	
	private static Runnable transfer = new Runnable() {
		@Override
		public void run() {
			if(!buffer_queue.isEmpty()) {
				threadPool.execute(buffer_queue.poll());
			}
			
		}
	};
	
	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1); 
	
	protected static final ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(transfer, 0, SCHEDULE_TIME, TimeUnit.MILLISECONDS);
	
	
	private final static RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			buffer_queue.offer(r);
		}
	};
	
	
	private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(POOL_CORE_SIZE, POOL_MAX_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(WORK_QUEUE_SIZE),rejectedExecutionHandler);

	
	
	private static class ThreadPoolManagerTempHolder {
		public static ThreadPoolManagerTemp threadPoolManager = new ThreadPoolManagerTemp();
	}
	
	private ThreadPoolManagerTemp() {
		
	}
	
	public static ThreadPoolManagerTemp getInstance() {
		return ThreadPoolManagerTempHolder.threadPoolManager;
	}

}
