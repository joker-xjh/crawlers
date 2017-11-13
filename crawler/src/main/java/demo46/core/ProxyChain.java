package demo46.core;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.cglib.proxy.MethodProxy;

public class ProxyChain {
	
	private List<Proxy> proxyList;
	
	private int currentProxyIndex;
	
	private Class<?> targetClass;
	
	private Object target;
	
	private Method targetMethod;
	
	private Object[] targetArgs;
	
	private MethodProxy methodProxy;
	
	private Object methodResult;
	
	 public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod,
             Object[] targetArgs, MethodProxy methodProxy, List<Proxy> proxyList) {
		this.targetClass = targetClass;
		this.target = targetObject;
		this.targetMethod = targetMethod;
		this.targetArgs = targetArgs;
		this.methodProxy = methodProxy;
		this.proxyList = proxyList;
	 }
	
	
	public void doProxyChain() {
		if(currentProxyIndex < proxyList.size()) {
			proxyList.get(currentProxyIndex++).doProxy(this);
		}
		else {
			try {
				methodResult = methodProxy.invoke(target, targetArgs);
			} catch (Throwable e) {
				 throw new RuntimeException(e);
			}
		}
	}
	 

	public List<Proxy> getProxyList() {
		return proxyList;
	}

	public int getCurrentProxyIndex() {
		return currentProxyIndex;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Object getTarget() {
		return target;
	}

	public Method getTargetMethod() {
		return targetMethod;
	}

	public Object[] getTargetArgs() {
		return targetArgs;
	}

	public MethodProxy getMethodProxy() {
		return methodProxy;
	}

	public Object getMethodResult() {
		return methodResult;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
