package demo49.ioc.xml;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import demo49.bean.BeanDefinition;
import demo49.bean.PropertyValue;
import demo49.injection.BeanReference;
import demo49.io.ResourceLoader;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader{

	public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}

	@Override
	public void loadBeanDefinitions(String location) throws Exception {
		InputStream inputStream = this.getResourceLoader().getResource(location).getInputStream();
		doLoadBeanDefinitions(inputStream);
	}
	
	private void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputStream);
		registerBeanDefinitions(document);
		inputStream.close();
	}
	
	
	private void registerBeanDefinitions(Document document) {
		Element element = document.getDocumentElement();
		parseBeanDefinitions(element);
	}
	
	private void parseBeanDefinitions(Element root) {
		NodeList nodeList = root.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node instanceof Element) {
				Element element = (Element)node;
				processBeanDefinition(element);
			}
		}
	}

	
	private void processBeanDefinition(Element ele){
		String id = ele.getAttribute("id");
		String className = ele.getAttribute("class");
		BeanDefinition beanDefinition = new BeanDefinition();
		processProperty(ele, beanDefinition);
		beanDefinition.setBeanClassName(className);
		this.getRegistry().put(id, beanDefinition);
	}
	
	
	private void processProperty(Element element, BeanDefinition beanDefinition) {
		NodeList nodeList = element.getElementsByTagName("property");
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node instanceof Element) {
				Element propertyElement = (Element)node;
				String name = propertyElement.getAttribute("name");
				String value = propertyElement.getAttribute("value");
				if(value != null) {
					beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
				}
				else {
					String ref = propertyElement.getAttribute("ref");
					if(ref == null)
						throw new RuntimeException("u must specify a ref or value");
					BeanReference beanReference = new BeanReference(ref);
					beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
				}
			}
		}
	}
	
	
	

}
