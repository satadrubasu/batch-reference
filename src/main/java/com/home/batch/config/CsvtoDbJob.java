package com.home.batch.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.home.batch.listeners.DbToCsvJobListener;
import com.home.batch.model.Claim;
import com.home.batch.processors.ClaimNoOpProcessor;

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
//				/.listener(csvJobListener())
				// .start(readAndWrite())
				.flow(step1()).end().build();
	}

	/**
	 * observe how a the <Processor is marked as a step Scope >beans are kept as
	 * step scope Note Restartable is at a Step level
	 * 
	 * @return
	 */
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Claim, Claim>chunk(1)
				.reader(csvReader())
				//.processor(processor())
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

		// we read a flat file that will be used to fill a Person object
		FlatFileItemReader<Claim> reader = new FlatFileItemReader<Claim>();
		// we pass as parameter the flat file directory
		reader.setResource(new ClassPathResource("ClaimImport.csv"));
		// we use a default line mapper to assign the content of each line to
		// the Person object
		reader.setLineMapper(new DefaultLineMapper<Claim>() {
			{
				this.setLineTokenizer(new DelimitedLineTokenizer(",") {
					{
						this.setNames(new String[] { "claimId", "insuredId", "amount", "patientName" });
					}
				});
				this.setFieldSetMapper(new BeanWrapperFieldSetMapper<Claim>() { // field
																				// mapper
					{
						this.setTargetType(Claim.class);
					}
				});
			}
		});
		return reader;
	}
/*
	@Bean
    public DataSource getdataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(databaseDriver);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }*/
	/**
	 * Observe the StepScope for the processor
	 * 
	 * @return
	 */
	@Bean
	public ItemProcessor<Claim, Claim> processor() {
		return new ClaimNoOpProcessor();
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
	public JobExecutionListener csvJobListener() {
		return new DbToCsvJobListener();
	}

}
