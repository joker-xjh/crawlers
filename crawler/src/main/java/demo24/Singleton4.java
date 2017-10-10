package demo24;

public class Singleton4 {
	
	private Singleton4() {
		
	}
	
	private static class SingletonHolder {
		private static final Singleton4 singleton = new Singleton4();
	}
	
	public static Singleton4 getInstance() {
		return SingletonHolder.singleton;
	}

}
