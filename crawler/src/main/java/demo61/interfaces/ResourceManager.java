package demo61.interfaces;

public interface ResourceManager {
	
	void requestResource(String rid, int nums);
	
	void releaseResource(String rid, int nums);

}
