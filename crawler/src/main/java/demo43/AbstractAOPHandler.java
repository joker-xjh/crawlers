package demo43;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class AbstractAOPHandler implements InvocationHandler{
	
	protected Object target;
	
	public AbstractAOPHandler(Object target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = method.invoke(target, args);
		return result;
	}

}
