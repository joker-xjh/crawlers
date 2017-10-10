package demo24;

public class Singleton3 {
	
	private Singleton3() {
		
	}
	
	private static volatile Singleton3 singleton = null;
	
	public static Singleton3 getInstance() {
		if(singleton == null) {
			synchronized (Singleton3.class) {
				if(singleton == null) {
					singleton = new Singleton3();
				}
			}
		}
		return singleton;
	}

}
