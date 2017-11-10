package demo40;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AOPHandle implements InvocationHandler{
	
	private AOPMethod aopmethod;
	
	private Object target;
	
	public AOPHandle(Object target, AOPMethod aopMethod) {
		this.target = target;
		this.aopmethod = aopMethod;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = null;
		aopmethod.before(proxy, method, args);
		ret = method.invoke(target, args);
		aopmethod.after(proxy, method, args);
		return ret;
	}

}
