package demo43;

public abstract class AbstractProxy implements Proxy{
	
	@SuppressWarnings("unchecked")
	<T> T getProxyObjectBytype(Object object, Aop aop) {
		return (T) getProxyObject(object, aop);
	}
	
	@SuppressWarnings("unchecked")
	<T> T getProxyObjectByClassName(String className, Aop aop) throws Exception {
		return (T) getProxyObject(Class.forName(className).newInstance(), aop);
	}
	
	@SuppressWarnings("unchecked")
	<T> T  getProxyObjectByType(Class<T> type, Aop aop) throws Exception {
		return (T) getProxyObject(type.newInstance(), aop);
	}

}
