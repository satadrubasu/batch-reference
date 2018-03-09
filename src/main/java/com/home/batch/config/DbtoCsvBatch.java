package com.home.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;

import com.home.batch.listeners.DbToCsvChunkListener;
import com.home.batch.listeners.DbToCsvJobListener;
import com.home.batch.listeners.DbToCsvStepListener;
import com.home.batch.mappers.ClaimRowMapper;
import com.home.batch.model.Claim;
import com.home.batch.processors.ClaimNoOpProcessor;
import com.home.batch.writers.ClaimCSVWriter;

@Configuration
@EnableBatchProcessing // adds batch specific beans that support jobs helps inject JobRepository (bean name “jobRepository”)
/* 
 * JobLauncher (bean name “jobLauncher”)
 * a JobRegistry (bean name “jobRegistry”)
 * a PlatformTransactionManager (bean name “transactionManager”)
 * a JobBuilderFactory (bean name “jobBuilders”) as a convenience to prevent from having to inject the job repository into every job
 * a StepBuilderFactory*/
public class DbtoCsvBatch {
	
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	/**
	 * The Master Bean which defines the Job Configuration
	 * @return
	 */
	@Bean
	public Job dbToFileJob(){
		return jobs.get("dbToCsvJob")
				.listener(dbCsvJobListener())
				.start(readAndWrite())
				.build();
	}
	
	/**
	 * observe how a the <Processor is marked as a step Scope >beans are kept as step scope
	 * @return
	 */
	@Bean
	public Step readAndWrite()
	{
		return stepBuilderFactory.get("readWriteInChunks")
		                .<Claim,Claim>chunk(1) //important to be one in this case to commit after every line read
		                .reader(dbReader())
						.processor(processor())
						.writer(writer())
						.listener(dbCsvStepListener())
						.listener(dbCsvChunkListener())
					//	.skipLimit(10) //default is set to 0
					//	.skip(MySQLIntegrityConstraintViolationException.class)
						.build();
	}
	
	/**********************************************************************
	 *  Tasklet comprising of Chunk items Configure Below ( Read/process/Write )
	 *  
	 **********************************************************************/
	
	/**
	 * JDBC cursor and continually retrieves the next row in the ResultSet
	 * @return
	 */
	@Bean
	public ItemReader<Claim> dbReader(){

		JdbcCursorItemReader<Claim> reader = new JdbcCursorItemReader<Claim>();
		String query = "select * from claims";
		reader.setSql(query);
		reader.setDataSource(dataSource);
		reader.setRowMapper(claimRowMapper());		

		return reader;
	}
	
	@Bean
	public RowMapper<Claim> claimRowMapper()
	{
		return new ClaimRowMapper();
	}
	
	/**
	 *  Observe the StepScope for the processor
	 * @return
	 */
	@Bean
	@StepScope
	public ItemProcessor<Claim,Claim> processor()
	{
		return new ClaimNoOpProcessor();
	}
	
	@Bean
    public ItemWriter<Claim> writer() {
    	//return new ClaimCSVWriter();  not using custom writer
		
		// use FlatFileItemWriter implementation
		FlatFileItemWriter<Claim> writer = new FlatFileItemWriter<Claim>();
    	writer.setResource(new ClassPathResource("ClaimExport.csv"));
    	DelimitedLineAggregator<Claim> delLineAgg = new DelimitedLineAggregator<Claim>();
    	delLineAgg.setDelimiter(",");
    	BeanWrapperFieldExtractor<Claim> fieldExtractor = new BeanWrapperFieldExtractor<Claim>();
    	fieldExtractor.setNames(new String[] {"id", "claimId","insuredId","amount","patientName"});
    	delLineAgg.setFieldExtractor(fieldExtractor);
    	writer.setLineAggregator(delLineAgg);
        return writer;
    }
	
	/**********************************************************************
	 *    LISTENERS Configure Below
	 **********************************************************************/
	
	@Bean
	public StepExecutionListener dbCsvStepListener(){
		return new DbToCsvStepListener();
	}
 
	@Bean
	public JobExecutionListener dbCsvJobListener(){
		return new DbToCsvJobListener();
	}  
	
	@Bean
	public ChunkListener dbCsvChunkListener(){
		return new DbToCsvChunkListener();
	}  

}
