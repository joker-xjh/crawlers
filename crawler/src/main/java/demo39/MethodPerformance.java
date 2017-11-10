package demo39;

public class MethodPerformance {
	
	private long begin;
	
	private long end;
	
	private String serviceMethod;
	
	public MethodPerformance(String name) {
		this.serviceMethod = name;
		this.begin = System.currentTimeMillis();
	}
	
	public void printPerformance() {
		end = System.currentTimeMillis();
		long diff = end - begin;
		System.out.println(serviceMethod + "花费" + diff + "毫秒");
	}

}
