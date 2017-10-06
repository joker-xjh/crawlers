package demo20;

public interface ConnectionFactory<T> {
	
	Connection<T> getConnection();

}
