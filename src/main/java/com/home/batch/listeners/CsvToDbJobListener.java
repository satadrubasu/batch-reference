package com.home.batch.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class CsvToDbJobListener implements JobExecutionListener{

	public void afterJob(JobExecution arg0) {
		System.out.println("After Job [ CsvToDbJobListener ] Endtime -" + arg0.getEndTime());
		
	}

	public void beforeJob(JobExecution arg0) {
		System.out.println("Before Job [ CsvToDbJobListener ] StartTime-" + arg0.getStartTime());
		
	}

}
