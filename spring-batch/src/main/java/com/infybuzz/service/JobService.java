/**
 * Esta clase representa un servicio para poder activar de manera asincrona los jobs
 */
package com.infybuzz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.infybuzz.request.JobParamsRequest;

@Service //Marcamos la clase como servicio
public class JobService {

	//Inyectamos la dependencia jobLauncher la cual nos ayuda a iniciar los jobs
	@Autowired
	JobLauncher jobLauncher;
	
	//Inyectamos la dependencia JobOperator la cual nos ayudara a realizar multiples operaciones con los Jobs
	@Autowired
	JobOperator jobOperator;
	
	//Declaramos los jobs mediante un autowired y ponemos la etiqueta qualifier para identificar los beans
	@Qualifier("firstJob")
	@Autowired
	Job firstJob;
	
	@Qualifier("secondJob")
	@Autowired
	Job secondJob;	
	
	//Metodo que inicializa los jobs
	@Async
	public void startJob(String jobName, List<JobParamsRequest> jobParamRequestList){
		
		//Declaramos un HashMap de JobParameter
		Map<String, JobParameter> params = new HashMap<>();
		
		//Le asignamos la hora actual del sistema
		params.put("currentTime", new JobParameter(System.currentTimeMillis()));
		
		//Se itera la lista de parametros y se agregan los valores dentro del hashmap params.
		jobParamRequestList.stream().forEach(jobPramReq -> {
			params.put(jobPramReq.getParamKey(), new JobParameter(jobPramReq.getParamValue()));
		});
		
		//Creamos una instancia de JobParameters en base al hashmap creado con anterioridad
		JobParameters jobParameters = new JobParameters(params);
		
		//Abrimos un bloque try/catch
		try 
		{
			//Declaramos un job execution nulo.
			JobExecution jobExecution = null;
			//Comprobamos que job nos estan pidiendo
			if(jobName.equals("First Job")) 
			{
				//Inicializamos el job
				jobExecution = jobLauncher.run(firstJob, jobParameters);			
			}
			else if(jobName.equals("Second Job")) 
			{
				//Inicializamos el job
				jobExecution = jobLauncher.run(secondJob, jobParameters);
			}
			
			//Logeamos el id del jobExecution.
			System.out.println("jobExecution ID = " + jobExecution.getId());
		}catch(Exception e){
			//Lanzamos un mensaje en caso de un error
			System.err.println("Exception while starting job");
		}		
	}
	
	//Metodo que detiene un job en base su jobExecutionId
	public void stopJob(long jobExecutionId) {
		//Abrimos un bloque try/catch
		try 
		{
			//Utilizamos la dependencia jobOperator para detener el job
			jobOperator.stop(jobExecutionId);
		}catch(Exception e) {
			//En caso de error logeamos
			e.printStackTrace();
		}
	}
}
