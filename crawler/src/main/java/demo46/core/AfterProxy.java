package demo46.core;

import java.lang.reflect.Method;
import demo46.annotation.After;

public class AfterProxy extends AbstractProxy{
	
	private AspectCut aspectcut;

	@Override
	public void advice(AspectCut aspectCut) {
		this.aspectcut = aspectCut;
	}
	
	@Override
	public void after(Class<?> clazz, Method method, Object[] args) {
		aspectcut.proceed();
	}
	
	@Override
	public boolean filter(Class<?> clazz, Method method, Object[] args) {
		String methodName = method.getName();
		Method method2 = aspectcut.getMethod();
		String methodName2 = method2.getAnnotation(After.class).value();
		return methodName.equals(methodName2);
	}

}
