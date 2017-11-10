package demo40;

import java.lang.reflect.Method;

public interface AOPMethod {
	
	void before(Object proxy, Method method, Object[] args);
	
	void after(Object proxy, Method method, Object[] args);

}
