package demo51.imp;

import demo51.interfaces.TimePeriod;

public class FixedIntervalTimedPeriod implements TimePeriod{
	
	private volatile long timePeriod;
	
	public FixedIntervalTimedPeriod(long timePeriod) {
		if(timePeriod <= 0)
			throw new IllegalArgumentException("timePeriod 不能小于等于0");
		this.timePeriod = timePeriod;
	}
	
	public void setTimePeriod(long timePeriod) {
		if(timePeriod <= 0)
			throw new IllegalArgumentException("timePeriod 不能小于等于0");
		this.timePeriod = timePeriod;
	}

	@Override
	public long getTimePeriod() {
		return timePeriod;
	}

	@Override
	public void afterExecution() {
		
	}

}
