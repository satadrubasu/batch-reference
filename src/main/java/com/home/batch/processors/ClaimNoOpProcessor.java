package com.home.batch.processors;

import org.springframework.batch.item.ItemProcessor;

import com.home.batch.model.Claim;


public class ClaimNoOpProcessor implements ItemProcessor<Claim,Claim>{

	public Claim process(Claim claimItem) throws Exception {

		
		System.out.println("= ClaimNo Op Processor ==");
		return claimItem;
	}

}
