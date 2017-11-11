package demo42;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect 
public class Audience {
	
	@Pointcut("execution(*Performer.perform(..))")
	public void performance() {
		
	}
	
	@Before("performance()")
	public void takeSeats() {
		System.out.println("taking seats");
	}
	
	@AfterReturning("performance()")
	public void applaud() {
		System.out.println("clap clap clap");
	}
	
	@AfterThrowing("performance()")
	public void demandRefund() {
		System.out.println("We want our money back");
	}
	

}
