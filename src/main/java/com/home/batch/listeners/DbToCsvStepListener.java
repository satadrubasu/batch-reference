package com.home.batch.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class DbToCsvStepListener implements StepExecutionListener{

	public ExitStatus afterStep(StepExecution arg0) {
		System.out.println("After Step [ StepExecutionListener ] -" + arg0.getStepName());
		return ExitStatus.COMPLETED;
	}

	public void beforeStep(StepExecution arg0) {
		System.out.println("Before Step [ StepExecutionListener ] -" + arg0.getStepName());
		
	}

}
