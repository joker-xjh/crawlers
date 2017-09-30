package orm;

import java.lang.reflect.Field;

public class Main {
	
	
	

	public static void main(String[] args) {
		Main main = new Main();
		Class<?> clazz = main.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			System.out.println(field);
			System.out.println(field.getDeclaringClass());
		}

	}

}
