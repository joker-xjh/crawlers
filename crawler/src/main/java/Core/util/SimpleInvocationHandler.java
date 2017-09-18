package Core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

public class SimpleInvocationHandler implements InvocationHandler{
	
	private static Logger logger = SimpleLogger.getSimpleLogger(HttpClientUtil.class);
	
	private Object target;
	
	public SimpleInvocationHandler() {
		
	}
	
    public SimpleInvocationHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		long start = System.currentTimeMillis();
		Object result = method.invoke(target, args);
		long end = System.currentTimeMillis();
		logger.debug(target.getClass().getSimpleName() + " " + method.getName() + " cost time:" + (end - start) + "ms");
		return result;
	}

}
