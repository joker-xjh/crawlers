package demo49.bean;

public class BeanDefinition {
	
	private String name;
	
	private Object bean;
	
	private Class<?> beanClass;
	
	private PropertyValues propertyValues = new PropertyValues();
	
	public void setBeanClassName(String name) {
		try {
			this.beanClass = Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Class<?> getBeanClass() {
		return beanClass;
	}
	
	public Object getBean() {
		return bean;
	}
	
	public void setBean(Object bean) {
		this.bean = bean;
	}
	
	public PropertyValues getPropertyValues() {
		return propertyValues;
	}
	
	
	public void setPropertyValues(PropertyValues propertyValues) {
		this.propertyValues = propertyValues;
	}
	
	
	
	

}
