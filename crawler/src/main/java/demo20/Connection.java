package demo20;

public abstract class Connection<T> {
	final T obj;
	public Connection(T obj) {
		this.obj = obj;
	}
	public T getObj() {
		return obj;
	}
	protected abstract boolean close();

}
