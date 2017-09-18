package Core.dao;

public interface BaseDao<T> {

	void insert(T t);
	void deleteByUserid(String userid);
	boolean contains(String userid);
	
}
