package demo51.imp;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import demo51.interfaces.TimePeriod;
import demo51.interfaces.TimedTask;
import demo51.interfaces.YTask;

public abstract class AbstractTimedTask implements TimedTask {
	
	protected volatile Integer taskNo;
	
	protected volatile long startTime;
	
	protected volatile TimePeriod timePeriod;
	
	protected volatile long executionTime;
	
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	public AbstractTimedTask(Integer taskNo, long startTime, TimePeriod timePeriod) {
		this.taskNo = taskNo;
		this.startTime = startTime;
		this.timePeriod = timePeriod;
		calculateExecutionTime(startTime);
	}
	
	protected void calculateExecutionTime(long initTime) {
        long initExecutionTime = initTime;
        long curTime = System.currentTimeMillis();

        while(initExecutionTime < curTime) {
            // 获取当前时间的周期
            long timePeriodMillis = timePeriod.getTimePeriod();
            if(timePeriodMillis <= 0L)
                break;
            initExecutionTime += timePeriodMillis;
        }
        executionTime = initExecutionTime;
    }
	
	@Override
	public long getExecutionTime() {
		readWriteLock.readLock().lock();
		try {
			return this.executionTime;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}
	
	
	@Override
	public Integer getTaskNo() {
		readWriteLock.readLock().lock();
		try {
			return taskNo;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}
	
	@Override
	public void setStartTime(long startTime) {
		readWriteLock.writeLock().lock();
		try {
			this.startTime = startTime;
		} finally {
			readWriteLock.writeLock().unlock();
		}
		
	}
	
	
	@Override
	public long getStartTime() {
		readWriteLock.readLock().lock();
		try {
			return this.startTime;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}
	
	
	@Override
	public void setTimePeriod(TimePeriod timePeriod) {
		readWriteLock.writeLock().lock();
		try {
			this.timePeriod = timePeriod;
		} finally {
			readWriteLock.writeLock().unlock();
		}	
	}
	
	@Override
	public TimePeriod getTimePeriod() {
		readWriteLock.readLock().lock();
		try {
			return this.timePeriod;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}
	
	
	@Override
	public int hashCode() {
		return this.getTaskNo().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof YTask))
			return false;
		return this.getTaskNo().equals(((YTask)obj).getTaskNo());
	}
	

}
