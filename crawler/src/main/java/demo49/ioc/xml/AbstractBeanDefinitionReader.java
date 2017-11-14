package demo49.ioc.xml;

import java.util.HashMap;
import java.util.Map;

import demo49.bean.BeanDefinition;
import demo49.io.ResourceLoader;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader{
	
	private Map<String, BeanDefinition> registry;
	
	private ResourceLoader resourceLoader;
	
	public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
		registry = new HashMap<>();
	}

	public Map<String, BeanDefinition> getRegistry() {
		return registry;
	}
	
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}
	
}
