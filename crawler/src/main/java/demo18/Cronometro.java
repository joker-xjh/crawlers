package demo18;

public class Cronometro {
	
	private long start;
	
	private long end;
	
	private boolean started;
	
	public void start() {
		started = true;
		start = System.currentTimeMillis();
	}
	
	public long step() {
		return started ? System.currentTimeMillis() - start : -1;
	}
	
	public void end() {
		if(started) {
			started = false;
			end = System.currentTimeMillis();
		}
	}
	
	
	public long getTimeMillis() {
		return started ? -1 : end - start;
	}
	
	public long getTimeSecond() {
		return getTimeMillis()/1000;
	}
	
	public void reset() {
		started = false;
		start = end = 0;
	}
	
	public static String beautify(long time) {
		long millis = time % 1000;
		time /= 1000;
		long sec = time % 60;
		long min = time /60;
        return (min < 10 ? "0" : "") + min + "m:" + (sec < 10 ? "0" : "") + sec + "s:" + (millis < 100 ? (millis < 10 ? "00" : "0") : "") + millis + "ms";
	}
	
	@Override
	public String toString() {
		long time = getTimeMillis();
		return beautify(time);
	}

}
