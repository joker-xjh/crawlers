package demo49.bean;

import java.util.ArrayList;
import java.util.List;

public class PropertyValues {
	
	private final List<PropertyValue> propertyValues = new ArrayList<>();
	
	public void addPropertyValue(PropertyValue propertyValue) {
		propertyValues.add(propertyValue);
	}
	
	public List<PropertyValue> getPropertyValues() {
		return propertyValues;
	}
	
}
