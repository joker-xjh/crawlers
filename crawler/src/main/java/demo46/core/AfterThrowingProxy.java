package demo46.core;

import java.lang.reflect.Method;

import demo46.annotation.AfterThrowing;

public class AfterThrowingProxy extends AbstractProxy{
	
	private AspectCut aspectcut;
	
	@Override
	public void advice(AspectCut aspectCut) {
		this.aspectcut = aspectCut;
	}
	
	@Override
	public void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable throwable) {
		aspectcut.proceed();
	}
	
	@Override
	public boolean filter(Class<?> clazz, Method method, Object[] args) {
		String methodName = method.getName();
		Method method2 = aspectcut.getMethod();
		String methodName2 = method2.getAnnotation(AfterThrowing.class).value();
		return methodName.equals(methodName2);
	}

}
