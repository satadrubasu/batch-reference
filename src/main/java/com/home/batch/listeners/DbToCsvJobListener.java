package com.home.batch.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class DbToCsvJobListener implements JobExecutionListener{

	public void afterJob(JobExecution arg0) {
		System.out.println("After Job [ JobExecutionListener ] -" + arg0.getJobConfigurationName());
		
	}

	public void beforeJob(JobExecution arg0) {
		System.out.println("Before Job [ JobExecutionListener ] -" + arg0.getJobConfigurationName());
		
	}

}
