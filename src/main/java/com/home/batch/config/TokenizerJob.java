/*package com.home.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.home.batch.model.Claim;
import com.home.batch.model.Schedule;

public class TokenizerJob {
	
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	
	
	//**
	 The Master Bean which defines the Job Configuration
	 @return
	//*
	@Bean
	public Job dbToFileJob(){
		return jobs.get("TokenizerJob")
				.start(step1())
				.build();
	}
	
	@Bean
	public Step step1()
	{
		return stepBuilderFactory.get("readWriteInChunks")
		                .<Claim,Claim>chunk(1) //important to be one in this case to commit after every line read
		                .reader(scheduleReader())
						.processor(processor())
						.writer(writer())
						.listener(dbCsvStepListener())
						.listener(dbCsvChunkListener())
					//	.skipLimit(10) //default is set to 0
					//	.skip(MySQLIntegrityConstraintViolationException.class)
						.build();
	}
	
	@Bean
	public ItemReader<Schedule> scheduleReader(){

		JdbcCursorItemReader<Schedule> reader = new JdbcCursorItemReader<Claim>();
		String query = "select * from claims";
		reader.setSql(query);
		reader.setDataSource(dataSource);
		reader.setRowMapper(claimRowMapper());		

		return reader;
	}
	
	
	
	
	

}
*/