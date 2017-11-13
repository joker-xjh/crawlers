package demo46.core;

import java.lang.reflect.Method;

import org.aspectj.lang.annotation.Before;

public class BeforeProxy extends AbstractProxy{
	
	private AspectCut aspectcut;

	@Override
	public void advice(AspectCut aspectCut) {
		this.aspectcut = aspectCut;
	}
	
	@Override
	public void before(Class<?> clazz, Method method, Object[] args) {
		aspectcut.proceed();
	}
	
	@Override
	public boolean filter(Class<?> clazz, Method method, Object[] args) {
		String methodName = method.getName();
		Method method2 = aspectcut.getMethod();
		String methodName2 = method2.getAnnotation(Before.class).value();
		return methodName.equals(methodName2);
	}
	

}
