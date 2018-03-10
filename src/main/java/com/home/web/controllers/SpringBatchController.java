package com.home.web.controllers;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class SpringBatchController  {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	@Qualifier("dbToCsvJob")
	private Job dbToCsvJob;
	
	@Autowired
	@Qualifier("csvToDbJob")
	private Job csvToDbJob;
	
	@RequestMapping("/index")
	public String testEndpoint() throws Exception {
		return "index";// will return the index.html from resources/templates Thymeleaf
	}

	@RequestMapping("/job/dbToCSVJob")
	public String dbToCSVJob() throws Exception {
	
		String response = "Failed";
		long startTime = System.currentTimeMillis();
		JobParameters params = new JobParametersBuilder().addLong("time",
				System.currentTimeMillis()).toJobParameters();
		
		try{
			jobLauncher.run(dbToCsvJob, params);
			response = "Completed in time (seconds) :" + (System.currentTimeMillis() - startTime)/1000 ;
		}catch(Exception e)
		{
			response = e.getMessage();
		}
		
		return response;
	}
	
	@RequestMapping("/job/csvToDbJob")
	public String csvToDbJob() throws Exception {
	
		String response = "Failed";
		long startTime = System.currentTimeMillis();
		JobParameters params = new JobParametersBuilder().addLong("time",
				System.currentTimeMillis()).toJobParameters();
		
		try{
			jobLauncher.run(csvToDbJob, params);
			response = "Completed in time (seconds) :" + (System.currentTimeMillis() - startTime)/1000 ;
		}catch(Exception e)
		{
			response = e.getMessage();
		}
		
		return response;
	}
	
	
	
	

}
