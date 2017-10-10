package demo24;

public class Singleton2 {
	
	private Singleton2() {
		
	}
	
	private static Singleton2 singleton = null;
	
	 synchronized static public  Singleton2 getInstance() {
		 if(singleton == null)
			 singleton = new Singleton2();
		 return singleton;
	 }

}
