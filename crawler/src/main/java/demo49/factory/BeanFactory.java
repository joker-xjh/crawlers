package demo49.factory;

import demo49.bean.BeanDefinition;

public interface BeanFactory {
	
	Object getBean(String name) throws Exception;
	
	void registerBean(String name, Object bean);
	
	void registerBeanDefinition(String name, BeanDefinition beanDefinition);

}
