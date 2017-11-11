package demo43;

import java.lang.reflect.Method;

public interface Aop {
	
	void before(Object proxy, Method method, Object[] args);
	
	void after(Object proxy, Method method, Object[] args);

}
