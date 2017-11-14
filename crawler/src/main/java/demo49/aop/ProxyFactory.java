package demo49.aop;

import java.lang.reflect.Proxy;

public class ProxyFactory {
	
	public static Object newInstance(ProxyHandler handler) {
		Object target = handler.getTarget();
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), 
				target.getClass().getInterfaces(), handler);
	}

}
