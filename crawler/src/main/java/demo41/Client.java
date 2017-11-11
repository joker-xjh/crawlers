package demo41;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Client {

	public static void main(String[] args) {
		Subject subject = new RealSubject();
		InvocationHandler handler = new DynamicProxy(subject);
		Subject proxy = (Subject)Proxy.newProxyInstance(subject.getClass().getClassLoader(), subject.getClass().getInterfaces(), handler);
		proxy.hello();
	}

}
