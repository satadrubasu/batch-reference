package com.home.batch.tasklets;


import javax.sql.DataSource;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("preImportCleanupTasklet")
public class PreImportCleanupTasklet implements Tasklet {
	
	@Autowired
	private DataSource dataSource;
	
	public static final String CCLEANUP_QUERY = "DELETE FROM claims";
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		try{
			JdbcTemplate template = new JdbcTemplate(this.dataSource);
			System.out.println("--> PreImportCleanupTasklet:: firing cleanup of table.claims before import.");
			template.update(CCLEANUP_QUERY);
		    }
		    catch (RuntimeException runtimeException) 
		    {
		        System.err.println("***NagiosHostDao::deleteObject, RuntimeException occurred, message follows.");
		        System.err.println(runtimeException);
		        throw runtimeException;
		    }
	        catch(Exception e){
    		   e.printStackTrace();
    		   throw e;
    	   }
		System.out.println("-> PreImportCleanupTasklet:: Cleanup Tasklet finished");
		return RepeatStatus.FINISHED;
	}

}

