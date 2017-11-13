package demo46.util;

import java.util.LinkedList;
import java.util.List;

public class CollectionUtils {
	
	@SafeVarargs
	public static <T> List<T> newLinkedList(T ...args){
		List<T> list = new LinkedList<>();
		for(T arg : args)
			list.add(arg);
		return list;
	}

}
