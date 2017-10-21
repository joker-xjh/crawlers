package demo32;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class MyHandlerExecutionChain {
	
	private final Object handler;
	
	private HandlerInterceptor[] interceptors;
	
	private List<HandlerInterceptor> interceptorsList;
	
	private int interceptorIndex = -1;
	
	public MyHandlerExecutionChain(Object handler, HandlerInterceptor...handlerInterceptors) {
		if(handler instanceof MyHandlerExecutionChain) {
			MyHandlerExecutionChain original = (MyHandlerExecutionChain) handler;
			this.handler = original.handler;
			this.interceptorsList = new ArrayList<>();
			this.interceptorsList.addAll(Arrays.asList(handlerInterceptors));
		}
		else {
			this.handler = handler;
			this.interceptors = handlerInterceptors;
		}
		this.interceptors = new HandlerInterceptor[this.interceptorsList.size()];
		interceptorsList.toArray(interceptors);
	}
	
	public Object getHandler() {
		return handler;
	}
	
	public void addHandlerInterceptor(HandlerInterceptor interceptor) {
		interceptorsList.add(interceptor);
	}
	
	
	public void addHandlerInterceptors(HandlerInterceptor...handlerInterceptors) {
		if(handlerInterceptors != null && handlerInterceptors.length > 0) {
			interceptorsList.addAll(Arrays.asList(handlerInterceptors));
		}
	}
	
	public HandlerInterceptor[] getHandlerInterceptors() {
		return this.interceptors;
	}
	
	public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		for(int i=0; i<interceptors.length; i++) {
			HandlerInterceptor interceptor = interceptors[i];
			if(!interceptor.preHandle(request, response, handler)) {
				return false;
			}
			this.interceptorIndex = i;
		}
		return true;
	}
	
	public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception { 
		for(int i=interceptors.length-1; i>=0; i--) {
			HandlerInterceptor interceptor = interceptors[i];
			interceptor.postHandle(request, response, interceptor, mv);
		}
	}
	
	public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		for(int i=interceptorIndex; i>=0; i--) {
			HandlerInterceptor interceptor = interceptors[i];
			try {
				interceptor.afterCompletion(request, response, interceptor, ex);
			} catch (Throwable  e) {
				
			}
		}
	}


	

}
