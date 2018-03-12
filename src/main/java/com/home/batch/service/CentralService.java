package com.home.batch.service;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

@Service
@EnableAutoConfiguration
public class CentralService {
	
	@Autowired
	private JobOperator jobOperator;
	
	
	
	
	public String getJobs()
	{
		
		String msg = jobOperator.getJobNames().toString();
		System.out.println("###### JOB Operater get JobNames : ");
		System.out.println(jobOperator.getJobNames().toString());
		
		return msg;
		
	}
	
	

}
