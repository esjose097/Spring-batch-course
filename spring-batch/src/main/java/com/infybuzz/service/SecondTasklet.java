/**
 * Esta es una clase de servicio la cual representa una "tasklet" la logica dentro de un job
 * de esta manera podemos separar la logica del job de la de los tasklet.
 */
package com.infybuzz.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

@Service //<-- Marcamos la clase como un servicio.
public class SecondTasklet implements Tasklet{

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		//Aquí dentro va la lógica del step que queremos realizar.
		System.out.println("This is second tasklet step");
		return RepeatStatus.FINISHED; //<-- Es importante siempre devolver un status.
	}

	
}
