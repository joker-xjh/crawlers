package demo61;

public class Resource {

	String rid;
	RCB rcb;
	
	public Resource(String rid, int free) {
		this.rid = rid;
		this.rcb = new RCB(rid, free);
	}
	
	static class RCB {
		String rid;
		int free;
		public RCB(String rid, int free) {
			this.rid = rid;
			this.free = free;
		}
	}
	
}
