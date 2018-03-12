package com.home.batch.policies;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * TODO: this bean mapped to both reader and processor , check the behaviour of the count
 * @author satadrubasu
 *
 */
@Component
@Qualifier("genericSkipPolicy")
public class GenericSkipPolicy implements SkipPolicy{

	@Override
	public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        
		System.out.println("== SkipPolicy some Exception happened [count] : " + skipCount); 
		
		return true;
		
	}

}
