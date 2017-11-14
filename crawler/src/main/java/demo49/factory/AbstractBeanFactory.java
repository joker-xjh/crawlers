package demo49.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo49.bean.BeanDefinition;

public abstract class AbstractBeanFactory implements BeanFactory{
	
	protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
	
	protected List<String> beanDefinitionMapNames = new ArrayList<>();
	
	protected Map<String, Object> iocContainer = new ConcurrentHashMap<>();
	
	@Override
	public void registerBean(String name, Object bean) {
		iocContainer.put(name, bean);
	}
	
	@Override
	public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
		beanDefinitionMap.put(name, beanDefinition);
		beanDefinitionMapNames.add(name);
	}

}
