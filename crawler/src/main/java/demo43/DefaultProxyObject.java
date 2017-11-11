package demo43;

import java.lang.reflect.Proxy;

public class DefaultProxyObject extends AbstractProxy{

	@Override
	public Object getProxyObject(Object target, Aop aop) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new DefaultAOPHandler(target, aop));
	}

}
