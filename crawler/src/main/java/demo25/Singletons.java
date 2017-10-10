package demo25;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public class Singletons {
	
	private static final Map<Class<?>, Object> singletonMap = new ConcurrentHashMap<>();
	
	public static <T> T get(Class<?> clazz) {
		if(clazz == null) {
			throw new IllegalArgumentException(" class can not be null");
		}
		if(!singletonMap.containsKey(clazz))
			register(clazz);
		Object obj = singletonMap.get(clazz);
		if(clazz.isInstance(obj))
			return (T) obj;
		
		throw new SingletonException("The singleton object " + clazz.getName() + " is not registered");
	}
	
	
	
	private static void register(Class<?> clazz) {
		if(clazz.isAnnotationPresent(Singleton.class)) {
			try {
				Constructor<?> constructor = clazz.getDeclaredConstructor();
				singletonMap.put(clazz, constructor.newInstance());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		throw new SingletonException("");
	}

}
