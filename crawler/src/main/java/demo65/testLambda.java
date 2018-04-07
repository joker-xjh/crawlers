package demo65;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class testLambda {

	public static void main(String[] args) {
		new Thread(() ->  System.out.println("hello lambda") ) .start();
		List<String> languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
		filter(languages, n -> n.startsWith("J"));
		filter(languages, n -> true);
		filter(languages, n -> false);
		filter(languages, n ->{System.out.println("cao");return true;});

	}
	
	public static void filter(List<String> list, Predicate<String> tester) {
		for(String str:list) {
			if(tester.test(str))
				System.out.println(str);
		}
	}

}
