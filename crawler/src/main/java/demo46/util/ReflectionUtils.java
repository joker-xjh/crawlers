package demo46.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;

import demo46.annotation.After;

public class ReflectionUtils {
	
	private static final List<Class<? extends Annotation>> annotations = CollectionUtils.newLinkedList(
			Before.class, After.class, AfterThrowing.class, AfterReturning.class
			);
	
	public static List<Method> findAnnotatedMethods(Class<?> clazz, List<Class<? extends Annotation>> annotatios){
		List<Method> list = new LinkedList<>();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods) {
			for(Class<? extends Annotation> annotation : annotatios) {
				if(method.isAnnotationPresent(annotation)) {
					list.add(method);
					break;
				}
			}
		}
		return list;
	}
	
	public static Class<? extends Annotation> findMethodAnnotation(Method method){
		Class<? extends Annotation> res = null;
		for(Class<? extends Annotation> annotation : annotations) {
			if(method.isAnnotationPresent(annotation)) {
				res = annotation;
				break;
			}
		}
		return res;
	}
	
	
	
	
	
	
	

}
