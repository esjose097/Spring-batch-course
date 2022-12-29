/**
 * Esta clase es un controlador API para poder inicializar los jobs con peticiones http
 */
package com.infybuzz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infybuzz.request.JobParamsRequest;
import com.infybuzz.service.JobService;

@RestController //<-- Marcamos como un Rest controller
@RequestMapping("/api/job") //<-- Declaramos la ruta prefija
public class JobController {
	
	@Autowired
	JobService jobService;
	
	//Creamos una nueva ruta HTTP de tipo GET para inicializar los jobs
	@GetMapping("/start/{jobName}")
	public String startJob(@PathVariable String jobName,
			//Habilitamos la ruta para recibir un Body
			@RequestBody List<JobParamsRequest> jobParamRequestList) throws Exception {
		//Llamamos al servicio para que ejecute inicialice el job
		jobService.startJob(jobName, jobParamRequestList);
		return "Job started...";
	}
	
	//Creamos una nueva ruta HTTP de tipo GET para detener un job en base su jobExecutionId.
	@GetMapping("/stop/{executionId}")
	public String stopJob(@PathVariable long jobExecutionId) {
		//Llamamos al servicio para que detenga el job
		jobService.stopJob(jobExecutionId);
		return "Job stopped...";
	}
	
}
