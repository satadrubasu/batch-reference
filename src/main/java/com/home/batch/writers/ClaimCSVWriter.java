package com.home.batch.writers;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.home.batch.model.Claim;

public class ClaimCSVWriter implements ItemWriter<Claim>{

	public void write(List<? extends Claim> commitReadyChunkList) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
