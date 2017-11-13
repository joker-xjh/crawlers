package demo46.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AspectCut {
	
	private Method method;
	
	private Object target;
	
	private Object[] args;
	
	public AspectCut(Method method, Object target, Object[] args) {
		this.args = args;
		this.method = method;
		this.target = target;
	}
	
	public Object proceed() {
		Object result = null;
		try {
			result = method.invoke(target, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}		
		return result;
	}
	
	public Object[] getArgs() {
		return args;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public Object getTarget() {
		return target;
	}
	

}
