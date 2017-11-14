package demo49.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import demo49.annotation.Aop;


public class ProxyHandler implements InvocationHandler{
	
	private Object target;
	
	public ProxyHandler(Object target) {
		this.target = target;
	}
	
	public Object getTarget() {
		return target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Aop aop = target.getClass().getAnnotation(Aop.class);
		if(aop == null) {
			return method.invoke(target, args);
		}
		String declaredMethodName = aop.declaredMethodName();
		if(!method.getName().equals(declaredMethodName))
			return method.invoke(target, args);
		Class<?> interceptor = aop.interceptor();
		Object object = interceptor.newInstance();
		if(! (object instanceof Interceptor))
			throw new RuntimeException("拦截器必须继承Interceptor接口");
		return ((Interceptor)object).interceptor(target, method, args);
	}
	
	

}
