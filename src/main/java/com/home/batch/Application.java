package com.home.batch;

import java.io.IOException;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"com.home.batch.config"})
@EnableAutoConfiguration
@EnableBatchProcessing
public class Application {
	
	public static void main(String[] args) throws BeansException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException, IOException {
    	
     ApplicationContext context =SpringApplication.run(Application.class, args);
         	        		
        // Enable this only for standalone - when using mvc mute this
     //  	runStandalone(context);

        	System.exit(0);
        }

	/**
	 * Comment this out if we want to run via the controller
	 * @param context
	 * @throws InterruptedException
	 */
	private static void runStandalone(ApplicationContext context) throws InterruptedException {
		JobParameters jobParameters = new JobParametersBuilder()
			.addString("user", "satadru02").toJobParameters();  
       	
       	JobLauncher launcher = (JobLauncher)context.getBean(JobLauncher.class);
       	Job dbToCsvJob = (Job)context.getBean(Job.class);
       	
        	// Setting the execution with jobParams
       	JobExecution jobExecution = null;
       	try{
       	    jobExecution = launcher.run(dbToCsvJob, jobParameters);
       	}catch(JobExecutionException e)
       	{
       		System.out.println("#### OOPS ####  Job Launch Exception !!" + e.getMessage());
       		System.exit(0);
	      }
       	
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
	} 

}
