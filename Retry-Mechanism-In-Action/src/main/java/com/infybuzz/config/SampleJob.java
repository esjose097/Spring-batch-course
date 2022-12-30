package com.infybuzz.config;

import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.infybuzz.listener.SkipListener;
import com.infybuzz.listener.SkipListenerImpl;
import com.infybuzz.model.StudentCsv;
import com.infybuzz.model.StudentJdbc;
import com.infybuzz.model.StudentJson;
import com.infybuzz.model.StudentXml;
import com.infybuzz.processor.FirstItemProcessor;
import com.infybuzz.reader.FirstItemReader;
import com.infybuzz.writer.FirstItemWriter;

@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private FirstItemReader firstItemReader;
	
	@Autowired
	private FirstItemProcessor firstItemProcessor;
	
	@Autowired
	private FirstItemWriter firstItemWriter;
	
	/*
	 * @Autowired private StudentService studentService;
	 */
	 
	
	@Autowired
	private SkipListener skipListener;
	
	@Autowired
	private SkipListenerImpl skipListenerImpl;
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource datasource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spring.universitydatasource")
	public DataSource universitydatasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public Job chunkJob() {
		return jobBuilderFactory.get("Chunk Job")
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.build();
	}
	
	private Step firstChunkStep() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentCsv, StudentJson>chunk(3)
				.reader(flatFileItemReader(null))
				.processor(firstItemProcessor)
				.writer(jsonFileItemWriter(null))
				.faultTolerant()
				.skip(Throwable.class)
				//.skip(NullPointerException.class)
				.skipLimit(100)
				//.skipPolicy(new AlwaysSkipItemSkipPolicy())
				.retryLimit(3)
				.retry(Throwable.class)
				//.listener(skipListener)
				.listener(skipListenerImpl)
				.build();
	}
	
	@StepScope
	@Bean
	public FlatFileItemReader<StudentCsv> flatFileItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		FlatFileItemReader<StudentCsv> flatFileItemReader = 
				new FlatFileItemReader<StudentCsv>();
		
		flatFileItemReader.setResource(fileSystemResource);
		
		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames("ID", "First Name", "Last Name", "Email");
					}
				});
				
				setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
					{
						setTargetType(StudentCsv.class);
					}
				});
				
			}
		});
		
		/*
		DefaultLineMapper<StudentCsv> defaultLineMapper = 
				new DefaultLineMapper<StudentCsv>();
		
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		
		BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper = 
				new BeanWrapperFieldSetMapper<StudentCsv>();
		fieldSetMapper.setTargetType(StudentCsv.class);
		
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		
		flatFileItemReader.setLineMapper(defaultLineMapper);
		*/
		
		flatFileItemReader.setLinesToSkip(1);
		
		return flatFileItemReader;
	}

	@StepScope
	@Bean
	public JsonItemReader<StudentJson> jsonItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		JsonItemReader<StudentJson> jsonItemReader = 
				new JsonItemReader<StudentJson>();
		
		jsonItemReader.setResource(fileSystemResource);
		jsonItemReader.setJsonObjectReader(
				new JacksonJsonObjectReader<>(StudentJson.class));
		
		jsonItemReader.setMaxItemCount(8);
		jsonItemReader.setCurrentItemCount(2);
		
		return jsonItemReader;
	}
	
	@StepScope
	@Bean
	public StaxEventItemReader<StudentXml> staxEventItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		StaxEventItemReader<StudentXml> staxEventItemReader = 
				new StaxEventItemReader<StudentXml>();
		
		staxEventItemReader.setResource(fileSystemResource);
		staxEventItemReader.setFragmentRootElementName("student");
		staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentXml.class);
			}
		});
		
		return staxEventItemReader;
	}
	
	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = 
				new JdbcCursorItemReader<StudentJdbc>();
		
		jdbcCursorItemReader.setDataSource(universitydatasource());
		jdbcCursorItemReader.setSql(
				"select id, first_name as firstName, last_name as lastName,"
				+ "email from student");
		
		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
			{
				setMappedClass(StudentJdbc.class);
			}
		});
		
		//jdbcCursorItemReader.setCurrentItemCount(2);
		//jdbcCursorItemReader.setMaxItemCount(8);
		
		return jdbcCursorItemReader;
	}
	
	/*
	public ItemReaderAdapter<StudentResponse> itemReaderAdapter() {
		ItemReaderAdapter<StudentResponse> itemReaderAdapter = 
				new ItemReaderAdapter<StudentResponse>();
		
		itemReaderAdapter.setTargetObject(studentService);
		itemReaderAdapter.setTargetMethod("getStudent");
		itemReaderAdapter.setArguments(new Object[] {1L, "Test"});
		
		return itemReaderAdapter;
	}
	*/
	
	@StepScope
	@Bean
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter = 
				new FlatFileItemWriter<StudentJdbc>();
		
		flatFileItemWriter.setResource(fileSystemResource);
		
		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("Id,First Name,Last Name,Email");
			}
		});
		
		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
			{
				//setDelimiter("|");
				setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
					{
						setNames(new String[] {"id", "firstName", "lastName", "email"});
					}
				});
			}
		});
		
		flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
			@Override
			public void writeFooter(Writer writer) throws IOException {
				writer.write("Created @ " + new Date());
			}
		});
		
		return flatFileItemWriter;
	}
	
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJson> jsonFileItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		JsonFileItemWriter<StudentJson> jsonFileItemWriter = 
				new JsonFileItemWriter<StudentJson>(fileSystemResource, 
						new JacksonJsonObjectMarshaller<StudentJson>()) {
			@Override
			public String doWrite(List<? extends StudentJson> items) {
				items.stream().forEach(item -> {
					if(item.getId() == 3) {
						System.out.println("Inside jsonFileItemWriter");
						throw new NullPointerException();
					}
				});
				return super.doWrite(items);
			}
		};
		
		return jsonFileItemWriter;
	}
	
	@StepScope
	@Bean
	public StaxEventItemWriter<StudentJdbc> staxEventItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		StaxEventItemWriter<StudentJdbc> staxEventItemWriter = 
				new StaxEventItemWriter<StudentJdbc>();
		
		staxEventItemWriter.setResource(fileSystemResource);
		staxEventItemWriter.setRootTagName("students");
		
		staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentJdbc.class);
			}
		});
		
		return staxEventItemWriter;
	}
	
	@Bean
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter() {
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = 
				new JdbcBatchItemWriter<StudentCsv>();
		
		jdbcBatchItemWriter.setDataSource(universitydatasource());
		jdbcBatchItemWriter.setSql(
				"insert into student(id, first_name, last_name, email) "
				+ "values (:id, :firstName, :lastName, :email)");
		
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());
		
		return jdbcBatchItemWriter;
	}
	
	@Bean
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter1() {
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = 
				new JdbcBatchItemWriter<StudentCsv>();
		
		jdbcBatchItemWriter.setDataSource(universitydatasource());
		jdbcBatchItemWriter.setSql(
				"insert into student(id, first_name, last_name, email) "
				+ "values (?,?,?,?)");
		
		jdbcBatchItemWriter.setItemPreparedStatementSetter(
				new ItemPreparedStatementSetter<StudentCsv>() {
			
			@Override
			public void setValues(StudentCsv item, PreparedStatement ps) throws SQLException {
				ps.setLong(1, item.getId());
				ps.setString(2, item.getFirstName());
				ps.setString(3, item.getLastName());
				ps.setString(4, item.getEmail());
			}
		});
		
		return jdbcBatchItemWriter;
	}
	
	/*
	public ItemWriterAdapter<StudentCsv> itemWriterAdapter() {
		ItemWriterAdapter<StudentCsv> itemWriterAdapter = 
				new ItemWriterAdapter<StudentCsv>();
		
		itemWriterAdapter.setTargetObject(studentService);
		itemWriterAdapter.setTargetMethod("restCallToCreateStudent");
		
		return itemWriterAdapter;
	}
	*/
}
