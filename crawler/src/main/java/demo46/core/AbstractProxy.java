package demo46.core;

import java.lang.reflect.Method;

public abstract class AbstractProxy implements Proxy {
	
	public abstract void advice(AspectCut aspectCut);
	
	
	@Override
	public void doProxy(ProxyChain proxyChain) {
		Class<?> clazz = proxyChain.getTargetClass();
		Method method = proxyChain.getTargetMethod();
		Object[] args = proxyChain.getTargetArgs();
		if(filter(clazz, method, args)) {
			try {
				before(clazz, method, args);
				proxyChain.doProxyChain();
				afterReturning(clazz, method, args);
			} catch (Throwable e) {
				afterThrowing(clazz, method, args, e);
			}finally {
				after(clazz, method, args);
			}
		}
		else {
			proxyChain.doProxyChain();
		}
	}
	
	
	public boolean filter(Class<?> clazz, Method method, Object[] args) {
        return true;
    }
	
	public void before(Class<?> clazz, Method method, Object[] args) {

    }
	
	public void afterReturning(Class<?> clazz, Method method, Object[] args) {

    }
	
	 public void after(Class<?> clazz, Method method, Object[] args) {

	 }
	 
	 public void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable throwable) {

	 }

}
