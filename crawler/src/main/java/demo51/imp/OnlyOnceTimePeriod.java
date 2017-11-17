package demo51.imp;

import demo51.interfaces.TimePeriod;

public class OnlyOnceTimePeriod implements TimePeriod{

	@Override
	public long getTimePeriod() {
		return 0;
	}

	@Override
	public void afterExecution() {
		
	}

}
