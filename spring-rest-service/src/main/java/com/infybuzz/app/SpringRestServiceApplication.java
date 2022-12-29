/**
 * Esta es una aplicación que trabaja como un servicio, simplemente esta corriendo esperando peticiones
 * http para enviar una lista, el punto de esto es simular recibir datos de una API REST
 */
package com.infybuzz.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication //<-- Declaramos como aplicacion
@RestController //<-- Marcamos como un controlador rest
@RequestMapping("/api/v1") //<-- Agregamos la ruta prefija
public class SpringRestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestServiceApplication.class, args);
	}
	
	//Ruta que recibe peticiones HTTP de tipo GET para devolver una lista de estudiantes en un formato JSON.
	@GetMapping("/students")
	public List<StudentResponse> students() {
		return Arrays.asList(
				new StudentResponse(1L, "John", "Smith", "john@gmail.com"),
				new StudentResponse(2L, "Sachin", "Dave", "sachin@gmail.com"),
				new StudentResponse(3L, "Peter", "Mark", "peter@gmail.com"),
				new StudentResponse(4L, "Martin", "Smith", "martin@gmail.com"),
				new StudentResponse(5L, "Raj", "Patel", "raj@gmail.com"),
				new StudentResponse(6L, "Virat", "Yadav", "virat@gmail.com"),
				new StudentResponse(7L, "Prabhas", "Shirke", "prabhas@gmail.com"),
				new StudentResponse(8L, "Tina", "Kapoor", "tina@gmail.com"),
				new StudentResponse(9L, "Mona", "Sharma", "mona@gmail.com"),
				new StudentResponse(10L, "Rahul", "Varma", "rahul@gmail.com"));
	}

}
