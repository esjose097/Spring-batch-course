/**
 * Esta clase representa un job simple para una aplicación de batch.
 */
package com.infybuzz.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infybuzz.listener.FirstJobListener;
import com.infybuzz.listener.FirstStepListener;
import com.infybuzz.service.SecondTasklet;

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
	
	@Autowired
	private FirstStepListener firstStepListener;
	
	
	//Agregamos un bean para la creación de jobs de spring. 
	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First job")//Creamos el Job
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
	
}
