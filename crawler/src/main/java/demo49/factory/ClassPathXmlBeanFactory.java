package demo49.factory;

import java.lang.reflect.Field;
import java.util.Map;


import demo49.bean.BeanDefinition;
import demo49.bean.PropertyValue;
import demo49.bean.PropertyValues;
import demo49.injection.BeanReference;
import demo49.io.ResourceLoader;
import demo49.ioc.xml.XmlBeanDefinitionReader;

public class ClassPathXmlBeanFactory extends AbstractBeanFactory{
	
	public ClassPathXmlBeanFactory(String location) {
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(new ResourceLoader());
		try {
			reader.loadBeanDefinitions(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Map.Entry<String, BeanDefinition> entry : reader.getRegistry().entrySet()) {
			registerBeanDefinition(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Object getBean(String name) throws Exception {
		if(iocContainer.containsKey(name))
			return iocContainer.get(name);
		Object bean = create(name);
		iocContainer.put(name, bean);
		return bean;
	}
	
	
	private Object create(String name) {
		BeanDefinition beanDefinition = this.beanDefinitionMap.get(name);
		Class<?> clazz = beanDefinition.getBeanClass();
		Object object = null;
		try {
			object = clazz.newInstance();
			PropertyValues propertyValues = beanDefinition.getPropertyValues();
			for(PropertyValue propertyValue : propertyValues.getPropertyValues()) {
				Field field = clazz.getField(propertyValue.getName());
				if(!field.isAccessible())
					field.setAccessible(true);
				if(propertyValue.getValue() instanceof BeanReference) {
					BeanReference reference = (BeanReference)propertyValue.getValue();
					String ref = reference.getRef();
					Object bean = this.getBean(ref);
					field.set(object, bean);
				}
				else {
					field.set(object, propertyValue.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	
	
	
	
	

}
