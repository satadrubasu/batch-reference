package com.home.batch.config;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.home.batch.listeners.DbToCsvJobListener;
import com.home.batch.model.Claim;
import com.home.batch.model.Stock;
import com.home.batch.model.StockVolume;
import com.home.batch.processors.ClaimNoOpProcessor;
import com.home.batch.processors.StockNoOpProcessor;

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
public class CsvtoCsvMultiThread {

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
	@Qualifier("localMap")
	public Map<String,String> localMap()
	{
		Map<String,String> cacheMap = new ConcurrentHashMap<String,String>();
		return cacheMap;
		
	}
	
	@Bean
	@Qualifier("tradesJob")
	public Job csvTocsv() {
		return jobs.get("tradesJob").incrementer(new RunIdIncrementer())
//				/.listener(csvJobListener())
				// .start(readAndWrite())
				.flow(stepTrades()).end().build();
	}

	/**
	 * observe how a the <Processor is marked as a step Scope >beans are kept as
	 * step scope Note Restartable is at a Step level
	 * 
	 * @return
	 */
	@Bean
	@Qualifier("stepTrades")
	public Step stepTrades() {
		return stepBuilderFactory.get("stepTrades").<Stock, StockVolume>chunk(10)
				.reader(csvTradesReader())
				.processor(stockProcessor())  // converts Stock to StockVolume
				.writer(csvTradesWriter())
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
	public ItemReader<Stock> csvTradesReader() {

		// we read a flat file that will be used to fill a Person object
		FlatFileItemReader<Stock> reader = new FlatFileItemReader<Stock>();
		// we pass as parameter the flat file directory
		reader.setResource(new FileSystemResource("src\\main\\resources\\trades.csv"));		// we use a default line mapper to assign the content of each line to
		// the Person object
		reader.setLineMapper(new DefaultLineMapper<Stock>() {
			{
				this.setLineTokenizer(new DelimitedLineTokenizer(",") {
					{
						this.setNames(new String[] { "stock", "time", "price", "shares"});
					}
				});
				this.setFieldSetMapper(new BeanWrapperFieldSetMapper<Stock>() { 
					{
						this.setTargetType(Stock.class);
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
	@Qualifier("stockProcessor")
	public ItemProcessor<Stock, StockVolume> stockProcessor() {
		return new StockNoOpProcessor();
	}

	@Bean
	@Qualifier("csvTradesWriter")
	public ItemWriter<? super StockVolume> csvTradesWriter() {
		
		 FlatFileItemWriter<StockVolume> csvFileWriter = new FlatFileItemWriter<>();
         csvFileWriter.setResource(new FileSystemResource("D:\\exportedStock.csv"));
         LineAggregator<StockVolume> lineVolAggregator = stockVolLineAggregator();
	        csvFileWriter.setLineAggregator(lineVolAggregator);
	        return csvFileWriter;
		
	}
	
	/* private LineAggregator<Stock> stockLineAggregator() {
	        DelimitedLineAggregator<Stock> lineAggregator = new DelimitedLineAggregator<>();
	        lineAggregator.setDelimiter(",");

	        FieldExtractor<Stock> fieldExtractor = createStockFieldExtractor();
	        lineAggregator.setFieldExtractor(fieldExtractor);

	        return lineAggregator;
	    }*/
	 @Bean
	 public LineAggregator<StockVolume> stockVolLineAggregator() {
	        DelimitedLineAggregator<StockVolume> lineAggregator = new DelimitedLineAggregator<>();
	        lineAggregator.setDelimiter(":");

	        FieldExtractor<StockVolume> fieldVolExtractor = createStockVolFieldExtractor();
	        lineAggregator.setFieldExtractor(fieldVolExtractor);

	        return lineAggregator;
	    }
/*	 @Bean
	 public FieldExtractor<Stock> createStockFieldExtractor() {
	        BeanWrapperFieldExtractor<Stock> extractor = new BeanWrapperFieldExtractor<>();
	        extractor.setNames(new String[] { "stock", "time", "price", "shares"});
	        return extractor;
	    }*/
	 
	 @Bean
	 @Qualifier("createStockVolFieldExtractor")
	 public FieldExtractor<StockVolume> createStockVolFieldExtractor() {
	        BeanWrapperFieldExtractor<StockVolume> extractorVol = new BeanWrapperFieldExtractor<>();
	        extractorVol.setNames(new String[] { "stock", "volume"});
	        return extractorVol;
	    }

	

	/**********************************************************************
	 * LISTENERS Configure Below
	 **********************************************************************/

	@Bean
	public JobExecutionListener csvJobListener() {
		return new DbToCsvJobListener();
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
