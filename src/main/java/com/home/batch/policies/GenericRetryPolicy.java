package com.home.batch.policies;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.stereotype.Component;

@Component
@Qualifier("genericRetryPolicy")
public class GenericRetryPolicy implements RetryPolicy{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean canRetry(RetryContext arg0) {
		System.out.println("## RetryPolicy--> can retry ?");
		return false;
	}

	@Override
	public void close(RetryContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RetryContext open(RetryContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerThrowable(RetryContext arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}



}
