package com.home.batch.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.home.batch.listeners.CsvToDbJobListener;
import com.home.batch.model.Claim;
import com.home.batch.processors.ClaimNoOpProcessor;
import com.home.batch.tasklets.PreImportCleanupTasklet;

/**
 * TODO: check the restartability / file not written
 * 
 * @author cts1
 *
 */
@ComponentScan
@EnableBatchProcessing
@Configuration
@PropertySource("classpath:application.properties")
public class CsvtoDbJob {

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	@Qualifier("preImportCleanupTasklet")
	private PreImportCleanupTasklet preImportCleanupTasklet;
	
	
	@Autowired
	@Qualifier("claimNoOpProcessor")
	private ClaimNoOpProcessor claimNoOpProcessor;
	

	@Value("${server.port}")
	private String serverPort;

	/*
	 * Load the properties
	 */
/*	@Value("${spring.datasource.driver-class-name}")
	private String databaseDriver;
	@Value("${url}")
	private String databaseUrl;
	@Value("${username}")
	private String databaseUsername;
	@Value("${spring.datasource.password}")
	private String databasePassword;
	@Value("${spring.datasource.spring.datasource.driver-class-name}")
	private String driverClassName;*/
	

	/**
	 * The Master Bean which defines the Job Configuration
	 * 
	 * @return
	 */
	@Bean
	@Qualifier("csvToDbJob")
	public Job csvToDbJob() {
		return jobs.get("csvToDbJob").incrementer(new RunIdIncrementer())
				.listener(csvToDbJobListener())
				.flow(cleanupTasklet())
				.next(csvToDbStep())
				.end().build();
	}

	
	@Bean
	@Qualifier("cleanupTasklet")
	public Step cleanupTasklet()
	{	StepBuilder stepBuilder = stepBuilderFactory.get("cleanupTaskletStep");
	    TaskletStepBuilder tasklet = stepBuilder.tasklet(preImportCleanupTasklet);
		return tasklet.build();
	}
	
	
	/**
	 * observe how a the <Processor is marked as a step Scope >beans are kept as
	 * step scope Note Restartable is at a Step level
	 * with chunk =1 took 325 seconds to process the 6000 records
	 * with chunk = 100 took 10 seconds to process the 6000 records
	 * 
	 * @return
	 */
	@Bean
	@Qualifier("csvToDbStep")
	public Step csvToDbStep() {
		return stepBuilderFactory.get("csvToDbStep").<Claim, Claim>chunk(100)
				.reader(csvReader())
				.processor(processor())
				.writer(getWriter())
				.build();
	}

	/**********************************************************************
	 * Tasklet comprising of Chunk items Configure Below ( Read/process/Write )
	 * 
	 **********************************************************************/

	/**
	 * CSV Reader cursor and continually retrieves the next row in the ResultSet
	 * 
	 * @return
	 */
	@Bean
	public ItemReader<Claim> csvReader() {

		FlatFileItemReader<Claim> reader = new FlatFileItemReader<Claim>();
		reader.setResource(new ClassPathResource("ClaimImport.csv"));
		// we use a default line mapper to assign the content of each line 
		reader.setLineMapper(new DefaultLineMapper<Claim>() {
			{
				this.setLineTokenizer(new DelimitedLineTokenizer(",") {
					{
						this.setNames(new String[] { "claimId", "insuredId", "amount", "patientName" });
					}
				});
				this.setFieldSetMapper(new BeanWrapperFieldSetMapper<Claim>() { 
					{
						this.setTargetType(Claim.class);
					}
				});
			}
		});
		return reader;
	}

	/**
	 * Observe the StepScope for the processor
	 * 
	 * @return
	 */
	@Bean
	public ItemProcessor<Claim, Claim> processor() {
		return claimNoOpProcessor;
	}
	
	@Bean
	public ItemWriter<Claim> getWriter() {
		JpaItemWriter<Claim> writer = new JpaItemWriter<Claim>();
		writer.setEntityManagerFactory(entityManagerFactory().getObject());
		return writer;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
		lef.setPackagesToScan("com.home");
		lef.setDataSource(dataSource);
		lef.setJpaVendorAdapter(jpaVendorAdapter());
		lef.setJpaProperties(new Properties());
		return lef;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.MYSQL);
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(false);
		jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
		return jpaVendorAdapter;
	}

	/**********************************************************************
	 * LISTENERS Configure Below
	 **********************************************************************/

	@Bean
	public JobExecutionListener csvToDbJobListener() {
		return new CsvToDbJobListener();
	}
	
	/**
	 * 
	 * just and executor service / thread pool which can be injected to a Step having ( read/process/write )
	 * @return
	 */
	@Bean
	public TaskExecutor taskExecutor()
	{
		SimpleAsyncTaskExecutor asyncExec = new SimpleAsyncTaskExecutor("Thread_prefix");
		asyncExec.setConcurrencyLimit(10);
		return asyncExec;
	}

}
