package demo60;

public class FileFat {
	
	private int ID;
	private FileFat next;
	private String data;
	private int useSize;
	private boolean used;
	
	public FileFat() {
		ID = hashCode();
		data = "";
		used = false;
		useSize = 0;
	}
	
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public FileFat getNext() {
		return next;
	}
	public void setNext(FileFat fileFat) {
		this.next = fileFat;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getUseSize() {
		return useSize;
	}
	public void setUseSize(int useSize) {
		this.useSize = useSize;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	

}
