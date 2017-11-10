package demo40;

import java.lang.reflect.Field;

public class AnnoInjection {
	
	public static Object getBean(Object obj) {
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for(Field field : fields) {
				Seven seven = field.getAnnotation(Seven.class);
				if(seven != null) {
					System.err.println("注入"+field.getName()+"  "+seven.value());
					obj.getClass().getMethod("set"+field.getName().substring(0,1).toUpperCase()+
							field.getName().substring(1), String.class).invoke(obj, seven.value());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return obj;
	}

}
