package demo49.factory;

import java.io.IOException;
import java.util.List;

import demo49.annotation.Component;
import demo49.bean.BeanDefinition;
import demo49.ioc.util.PkgScanner;

public class AnnotationBeanFactory extends AbstractBeanFactory{
	
	public AnnotationBeanFactory(String packageLocation) {
		PkgScanner pkgScanner = new PkgScanner(packageLocation, Component.class);
		List<String> clazzUrl = null;
		try {
			clazzUrl = pkgScanner.scan();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(clazzUrl != null) {
			for (String clazz : clazzUrl) {
                try {
                    Object bean = Class.forName(clazz).newInstance();
                    this.registerBean(bean.getClass().getSimpleName(), bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		}
		
	}

	@Override
	public Object getBean(String name) throws Exception {
		if(iocContainer.containsKey(name))
			return iocContainer.get(name);
		BeanDefinition beanDefinition = beanDefinitionMap.get(name);
		Class<?> clazz = beanDefinition.getBeanClass();
		Object object = clazz.newInstance();
		iocContainer.put(name, object);
		return object;
	}

}
