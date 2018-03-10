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
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"com.home.batch.config"})
@EnableAutoConfiguration
@EnableBatchProcessing
public class Application {
	
	public static void main(String[] args) throws BeansException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException, IOException {
    	
     ApplicationContext context =SpringApplication.run(Application.class, args);
         	        		
        	// Fetch the Job from Context
       	JobParameters jobParameters = new JobParametersBuilder()
			.addString("user", "satadru").toJobParameters();  
       	
       	JobLauncher launcher = (JobLauncher)context.getBean(JobLauncher.class);
       	Job dbToCsvJob = (Job)context.getBean(Job.class);
       	
        	// Setting the execution with jobParams
       	JobExecution jobExecution = launcher.run(dbToCsvJob, jobParameters);
        	
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
