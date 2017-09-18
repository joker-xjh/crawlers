package Core.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SimpleLogger extends Logger{

	protected SimpleLogger(String name) {
		super(name);
	}
	
	private static Properties setLogProperty() {
		Properties properties = new Properties();
		try {
			properties.load(SimpleInvocationHandler.class.getResourceAsStream("/log4j.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
	
	
	
	public static Logger getSimpleLogger(Class<?> c) {
		Logger logger = Logger.getLogger(c);
		PropertyConfigurator.configure(setLogProperty());
		return logger;
	}
	
	

}
