package demo58;

import java.util.Arrays;

public class MoreObjects {
	
	public static final class ToStringHelper {
		
		private static final class ValueHolder {
			String name;
			Object value;
			ValueHolder next;
		}
		
		private final String className;
		
		private final ValueHolder head = new ValueHolder();
		
		private ValueHolder tail = head;
		
		public ToStringHelper(String className) {
			this.className = className;
		}
		
		
		private ValueHolder addHolder() {
			ValueHolder valueHolder = new ValueHolder();
			tail = tail.next = valueHolder;
			return valueHolder;
		}
		
		@SuppressWarnings("unused")
		private ToStringHelper addHolder(Object value) {
			ValueHolder valueHolder = addHolder();
			valueHolder.value = value;
			return this;
		}
		
		@SuppressWarnings("unused")
		private ToStringHelper addHolder(String name, Object value) {
			ValueHolder valueHolder = addHolder();
			valueHolder.name = name;
			valueHolder.value = value;
			return this;
		}
		
		@Override
		public String toString() {
			String nextSeparator = "";
			StringBuilder sb = new StringBuilder(32).append(className).append('{');
			for(ValueHolder valueHolder = head.next;
					valueHolder != null;
					valueHolder = valueHolder.next) {
				Object value = valueHolder.value;
				if(value != null) {
					sb.append(nextSeparator);
					nextSeparator = ", ";
					if(valueHolder.name != null) {
						sb.append(valueHolder.name).append('=');
					}
					if(value.getClass().isArray()) {
						Object[] array = {value};
						String stringArray = Arrays.deepToString(array);
						sb.append(stringArray, 1, stringArray.length()-1);
					}
					else {
						sb.append(value);
					}
				}
			}
			
			return sb.append('}').toString();
		}
		
		
		
	}

}
