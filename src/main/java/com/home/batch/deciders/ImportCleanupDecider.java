package com.home.batch.deciders;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class ImportCleanupDecider implements JobExecutionDecider {

	 @Override
	    public FlowExecutionStatus decide(JobExecution jobExecution,StepExecution stepExecution) {
	        if ( jobExecution.getJobParameters().isEmpty()) {
	              System.out.println("ImportCleanupDecider found no jobParams -> FlowExecutionStatus=SKIP_BATCH_FLOW");
	          return  FlowExecutionStatus.FAILED;
	            
	        }
	        
	        System.out.println("ImportCleanupDecider:: Success flow to return COMPLETED STATUS");
	        return FlowExecutionStatus.COMPLETED;
	    }
}
