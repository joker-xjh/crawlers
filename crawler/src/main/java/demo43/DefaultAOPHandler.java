package demo43;

import java.lang.reflect.Method;

public class DefaultAOPHandler extends AbstractAOPHandler{
	
	private Aop aop;
	

	public DefaultAOPHandler(Object target, Aop aop) {
		super(target);
		this.aop = aop;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		aop.before(proxy, method, args);
		result = method.invoke(target, args);
		aop.after(proxy, method, args);
		return result;
	}
	
	

}
