package com.home.batch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.home.batch.model.Claim;

@Component
@Qualifier("claimNoOpProcessor")
public class ClaimNoOpProcessor implements ItemProcessor<Claim,Claim>{

	public Claim process(Claim claimItem) throws Exception {
		if(claimItem.getClaimId()%200==0) // to reduce sys out flood
		{
		  System.out.println("= ClaimNo Operation Processor processed till claim : "+ claimItem.getClaimId());
		}
		return claimItem;
	}

}
