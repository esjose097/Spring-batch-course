/**
 * Esta clase funciona interceptando un step de algun job y logeando datos antes y despues de su debida
 * ejecución.
 */
package com.infybuzz.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener{

	//Codigo que se ejecutara antes de la ejecución del step
	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Before step: " + stepExecution.getStepName());
		System.out.println("Job execution context: " + stepExecution.getJobExecution().getExecutionContext());
		System.out.println("Step execution context: " + stepExecution.getExecutionContext());
	}

	//Codigo que se ejecutara despues de la ejecución del step.
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("After step: " + stepExecution.getStepName());
		System.out.println("Job execution context: " + stepExecution.getJobExecution().getExecutionContext());
		System.out.println("Step execution context: " + stepExecution.getExecutionContext());
		return null;
	}

}
