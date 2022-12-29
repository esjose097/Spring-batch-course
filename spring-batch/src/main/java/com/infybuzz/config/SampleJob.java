/**
 * Esta clase representa un job simple para una aplicación de batch.
 */
package com.infybuzz.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.infybuzz.listener.FirstJobListener;
import com.infybuzz.listener.FirstStepListener;
import com.infybuzz.model.StudentCsv;
import com.infybuzz.model.StudentJson;
import com.infybuzz.model.StudentXml;
import com.infybuzz.processor.FirstItemProcessor;
import com.infybuzz.reader.FirstItemReader;
import com.infybuzz.service.SecondTasklet;
import com.infybuzz.writer.FirstItemWriter;

//Esta anotación se necesita para marcar la clase como una de configuración
@Configuration
public class SampleJob {

	//Inyectamos la libreria JobBuilderFactory para facilitar la creación de jobs.
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	//Inyectamos la libreria StepBuilderFactory para facilitar la creación de steps.
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	//Se inyecta la segunda tarea
	@Autowired
	private SecondTasklet secondTasklet;
	
	//Se inyecta el listener para el primer job.
	@Autowired
	private FirstJobListener firstJobListener;
	
	//Se inyecta el listener para el primer step
	@Autowired
	private FirstStepListener firstStepListener;
	
	@Autowired
	private FirstItemReader firstItemReader;
	
	//Se inyecta el ItemProcessor
	@Autowired
	private FirstItemProcessor firstItemProcessor;
	
	//Se inyecta el ItemWriter
	@Autowired
	private FirstItemWriter firstItemWriter;
	
	
	//Este job representa un Tasklet-step. 
	@Bean //El bean es necesario si queremos que el job se ejecute, mientras este comentariado este no se ejecutara.
	public Job firstJob() {
		return jobBuilderFactory.get("First Job")//Creamos el Job
				.incrementer(new RunIdIncrementer())//Se vuelve unique el job en la db por lo cual ahora puede ejecutarse multiples veces la misma instancia.
		.start(firstStep())//Le agregamos el step al job y lo iniciamos.
		.next(secondStep())//Le agreamos el segundo paso.
		.listener(firstJobListener)
		.build();
	}
	
	//Creamos el primer step Como nota este tambien se puede separar en una clase servicio sin problema.
	private Step firstStep(){
		return stepBuilderFactory.get("First step")
		.tasklet(firstTask()) //Le cargamos la logica por decirlo de alguna manera.
		.listener(firstStepListener)//Agregamos el listener para interceptar el job
		.build();
	}
	
	//Creamos el segundo step
	private Step secondStep() {
		return stepBuilderFactory.get("Second step")
				.tasklet(secondTasklet)
				.build();
	}
	
	//Creamos el "tasklet" o logica que ira dentro del step.
	private Tasklet firstTask() {
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				//Aquí dentro va la lógica del step que queremos realizar.
				System.out.println("This is first tasklet step");
				return RepeatStatus.FINISHED; //<-- Es importante siempre devolver un status.
			}
		};
	}
	
	/* Este "tasklet" se separo dentro del packete servicio, dejo esto comentariado para que sirva de referencia
	 * de que se puede realizar aqui mismo sin ningun problema. 
	 * Creamos el segundo "tasklet" o logica que ira dentro del second step.
	private Tasklet secondTask() {
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				//Aquí dentro va la lógica del step que queremos realizar.
				System.out.println("This is second tasklet step");
				return RepeatStatus.FINISHED; //<-- Es importante siempre devolver un status.
			}
		};
	}
	*/
	
	//La diferencia es que este job trabaja un chunk-oriented Step mientras que el otro un Tasklet step
	@Bean
	public Job secondJob() {
		return jobBuilderFactory.get("Second Job")//Creamos el Job
				.incrementer(new RunIdIncrementer())//Se vuelve unique el job en la db por lo cual ahora puede ejecutarse multiples veces la misma instancia.
		.listener(firstJobListener)//Agregamos el listener
		.start(firstChunkStep())//Iniciamos el job con el firstChunkStep
		.next(secondStep()) //Continuamos con un tasklet step.
		.build();
	}
	
	//Representa un chunkStep, recordemos que los chunkSteps se componen de un reader, processor y writer.
	private Step firstChunkStep() {
		return stepBuilderFactory.get("First chunk Step")
				.<StudentXml, StudentXml>chunk(3)//Primero asignamos <Input, Output> y despues la longitud.
				//.reader(flatFileItemReader(null)) //Agregamos el reader para csv
				//.reader(jsonItemReader(null))//Agregamos el reader para JSON
				.reader(staxEventItemReader(null)) //Agregamos el reader para xml
				//.processor(firstItemProcessor) //Agregamos el processor
				.writer(firstItemWriter) //Agregamos el writer.
				.build();
	}
	
	@StepScope
	@Bean
	//Metodo reader que se encarga de leer un archivo Csv
	public FlatFileItemReader<StudentCsv> flatFileItemReader(
			//Damos el valor de la ruta mediante un parametro.
			@Value("#{jobParameters['inputFile']}")FileSystemResource fileSystemResource){
		//Se declara una lista de flatFileItemReader
		FlatFileItemReader<StudentCsv> flatFileItemReader =
				new FlatFileItemReader<StudentCsv>();
		
		//Se adjunta la ruta de la fuente 
		flatFileItemReader.setResource(fileSystemResource);
		
		//Se agrega el lineMapper
		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>(){
			{	//Agregamos el delimitador
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						//Declaramos las filas.
						setNames("ID","First Name","Last Name","Email");
					}
				});
				//Agregamos el FieldSetMapper
				setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>(){
					{
						//Declaramos el tipo de clase que vamos a mappear
						setTargetType(StudentCsv.class);
					}
				});
			}
		});
		//Le indicamos que brincara una linea antes de empezar a leer.
		flatFileItemReader.setLinesToSkip(1);
		
		return flatFileItemReader;
	}
	
	//Metodo reader para leer archivos de tipo JSON.
	@StepScope
	@Bean
	public JsonItemReader<StudentJson> jsonItemReader(
			//Le damos la ruta del archivo mediante argunementos
			@Value("#{jobParameters['inputFile']}")FileSystemResource fileSystemResource){

		//Creamos un objeto JsonItemReader 
		JsonItemReader<StudentJson> jsonItemReader =
				new JsonItemReader<StudentJson>();
		
		//Le damos la ruta de la fuente es decir la ruta del archivo.
		jsonItemReader.setResource(fileSystemResource);

		//Aquí es como si le establecieramos el modelo que va mapear.
		jsonItemReader.setJsonObjectReader(
				new JacksonJsonObjectReader<>(StudentJson.class));
		
		//jsonItemReader.setMaxItemCount(2); <-- Este metodo da un limite de lecturas a tu reader.
		//jsonItemReader.setCurrentItemCount(2); <-- Este metodo sirve para iniciar la lectura desde n item.
		
		return jsonItemReader;
	}
	
	@StepScope
	@Bean
	public StaxEventItemReader<StudentXml> staxEventItemReader(
			@Value("#{jobParameters['inputFile']}")FileSystemResource fileSystemResource){
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
	
}
