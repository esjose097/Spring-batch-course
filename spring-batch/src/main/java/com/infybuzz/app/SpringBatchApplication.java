package com.infybuzz.app;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })//No tengo idea de porque pero esto resuelve el error del jdbc
//Esto permite que se realice proceso de batch.
@EnableBatchProcessing

//Esto permite realizar la configuración dentro de un package especifico.
@ComponentScan({"com.infybuzz.config", "com.infybuzz.service", "com.infybuzz.listener", 
	"com.infybuzz.reader", "com.infybuzz.writer", "com.infybuzz.processor",
	"com.infybuzz.controller"})
@EnableAsync //Habilitamos lo asincrono
@EnableScheduling //Habilitamos poder calendarizar
public class SpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchApplication.class, args);
	}

}
