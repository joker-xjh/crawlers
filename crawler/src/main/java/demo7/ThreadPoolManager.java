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

public class ThreadPoolManager {
	
	private static ThreadPoolManager sThreadPoolManager = new ThreadPoolManager();
	
	// 线程池维护线程的最少数量
	private static final int SIEZ_CORE_POOL = 3;
	
	// 线程池维护线程的最大数量
	private static final int SIZE_MAX_POOL = 6;
	
	// 线程池维护线程所允许的空闲时间
	private static final int TIME_KEEP_ALIVE = 5000;
	
	// 线程池所使用的缓冲队列大小
	private static final int SIZE_WORK_QUEUE = 500;
	
	// 任务调度周期
	private static final int PERIOD_TASK_QOS = 1000;
	
	// 任务缓冲队列
	private final Queue<Runnable> mTaskQueue = new LinkedList<>();
	
	//将构造方法访问修饰符设为私有，禁止任意实例化
	private ThreadPoolManager() {
		
	}
	
	//线程池单例创建方法
	public static ThreadPoolManager newInstance() {
		return sThreadPoolManager;
	}
	
	// 线程池超出界线时将任务加入缓冲队列
	private RejectedExecutionHandler mHandler = new RejectedExecutionHandler() {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			mTaskQueue.offer(r);
		}
	};
	
	
	private final Runnable mAccessBufferThread = new Runnable() {
		@Override
		public void run() {
			if(hasMoreAcquire()) {
				mThreadPool.execute(mTaskQueue.poll());
			}
		}
	};
	
	//创建一个调度线程池
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	protected ScheduledFuture<?> mTaskHandler = scheduler.scheduleAtFixedRate(mAccessBufferThread, 0, PERIOD_TASK_QOS, TimeUnit.MILLISECONDS);
	
	
	
	
	//线程池
	private final ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(SIEZ_CORE_POOL, SIZE_MAX_POOL,
			TIME_KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(SIZE_WORK_QUEUE), mHandler);
	
	//消息队列检查方法
	private boolean hasMoreAcquire() {
		return !mTaskQueue.isEmpty();
	}
	
	protected boolean isTaskEnd() {
		if(mThreadPool.getActiveCount() == 0)
			return true;
		else
			return false;
	}
	
	public void addTask(Runnable runnable) {
		if(runnable != null) {
			mThreadPool.execute(runnable);
		}
	}
	
	
	
	
	public void shutdown() {
		mTaskQueue.clear();
		mThreadPool.shutdown();
	}
	
	
	

}
