package demo51.interfaces;

public interface TimedTask extends YTask{
	
	void setStartTime(long startTime);
	
	long getStartTime();
	
	void setTimePeriod(TimePeriod timePeriod);
	
	TimePeriod getTimePeriod();

}
