package com.infybuzz.app;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//Esto permite que se realice proceso de batch.
@EnableBatchProcessing
//Esto permite realizar la configuración dentro de un package especifico.
@ComponentScan({"com.infybuzz.config", "com.infybuzz.service", "com.infybuzz.listener"})
public class SpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchApplication.class, args);
	}

}
