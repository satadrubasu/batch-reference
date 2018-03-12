package com.home.batch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.home.batch.model.Claim;
import com.home.batch.validator.ClaimValidator;

@Component
@Qualifier("claimNoOpProcessor")
public class ClaimNoOpProcessor implements ItemProcessor<Claim,Claim>{
	
	@Autowired
	private ClaimValidator validator;

	public Claim process(Claim claimItem) throws ValidationException,Exception {
		
		validator.validate(claimItem);
		
		if(claimItem.getClaimId()%200==0) // to reduce sys out flood
		{
		  System.out.println("= ClaimNo Operation Processor processed till claim : "+ claimItem.getClaimId());
		}
		return claimItem;
	}

}
