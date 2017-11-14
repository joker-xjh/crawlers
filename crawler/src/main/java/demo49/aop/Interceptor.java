package demo49.aop;

import java.lang.reflect.Method;

public interface Interceptor {
	
	Object interceptor(Object target, Method method, Object[] args);

}
