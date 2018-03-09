package com.home.batch;

import java.io.IOException;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class Application {

    private static final String DBtoCSV_JOB = "dbToCsvJob";
	private static final String Other_JOB = "otherJob";

	public static void main(String[] args) throws BeansException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException, IOException {
    	
        SpringApplication app = new SpringApplication(Application.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);
        JobLauncher jobLauncher = new SimpleJobLauncher();
    	        		
        
        	// Fetch the Job from Context
        	Job dbToCsvJob = ctx.getBean(DBtoCSV_JOB, Job.class);
        	JobParameters jobParameters = new JobParametersBuilder()
			.addString("user", "satadru")
    		.toJobParameters();  
        	
        	// Setting the execution with jobParams
        	JobExecution jobExecution = jobLauncher.run(dbToCsvJob, jobParameters);
        	
        	BatchStatus batchStatus = jobExecution.getStatus();
        	while(batchStatus.isRunning()){
        		System.out.println(".....    Batch Is running .....................");
        		Thread.sleep(5000);
        	}
			ExitStatus exitStatus = jobExecution.getExitStatus();
			String exitCode = exitStatus.getExitCode();
			System.out.println(String.format("###### Exit status: %s", exitCode));

			if(exitStatus.equals(ExitStatus.COMPLETED)){
				System.out.println("####### COMPLETED STATUS #######");
			}

        	JobInstance jobInstance = jobExecution.getJobInstance();
        	System.exit(0);
        	
        } 

}
