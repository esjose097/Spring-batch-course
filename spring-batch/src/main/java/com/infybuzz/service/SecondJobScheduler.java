/**
 * Esta clase es para poder "agendar" un job.
 */
package com.infybuzz.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service //<-- Marcamos la clase como servicio
public class SecondJobScheduler {
	
	/*Se inyecta el jobLauncher que nos ayuda a iniciar los jobs*/
	@Autowired
	JobLauncher jobLauncher;
	
	//Se inyecta el job
	@Qualifier("secondJob")
	@Autowired
	Job secondJob;
	
	@Scheduled(cron = "0 0/1 * 1/1 * ?")//Esta expressión la saque de: "cronmaker.com" es para que se repita cada minuto
	public void secondJobStarted() {
		
		//Declaramos un HashMap de JobParameter
		Map<String, JobParameter> params = new HashMap<>();
		
		//Le asignamos la hora actual del sistema
		params.put("currentTime", new JobParameter(System.currentTimeMillis()));
		
		//Creamos una instancia de JobParameters en base al hashmap creado con anterioridad
		JobParameters jobParameters = new JobParameters(params);
		
		//Abrimos un bloque try/catch
		try 
		{ 	
			//Inicializamos el job
			JobExecution jobExecution = jobLauncher.run(secondJob, jobParameters);
			
			//Logeamos el id del jobExecution.
			System.out.println("jobExecution ID = " + jobExecution.getId());
		}catch(Exception e){
			//Lanzamos un mensaje en caso de un error
			System.err.println("Exception while starting job");
		}				
	}
}
