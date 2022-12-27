/**
 * Este es una clase listener funciona interceptando un job y en este caso logeando y agregando detalles antes y despues de
 * la ejecución de nuestro job en cuestión.
 */
package com.infybuzz.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component //<-- Se marca como componente
public class FirstJobListener implements JobExecutionListener{

	//Codigo que se ejecutara antes de la ejecución del job
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Before job: " + jobExecution.getJobInstance().getJobName());
		System.out.println("Job params: " + jobExecution.getJobParameters());
		System.out.println("Job execution context: " + jobExecution.getExecutionContext());
	}

	//Codigo que se ejecutara despues de la ejecución del job.
	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("After job: " + jobExecution.getJobInstance().getJobName());
		System.out.println("Job params: " + jobExecution.getJobParameters());
		System.out.println("Job execution context: " + jobExecution.getExecutionContext());	}

}
