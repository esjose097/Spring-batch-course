/**
 * Clase servicio para llamar al servicio REST "spring-rest-service y obtener una lista de estudiantes 
 */
package com.infybuzz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infybuzz.model.StudentResponse;

@Service //<-- Marcamos la clase como servicio.
public class StudentService {

	//Declaramos una lista de StudentResponse
	List<StudentResponse> list;
	
	
	//Método que realiza la llamada a la RestApi y devuelve una lista de estudiantes.
	public List<StudentResponse> restCallGetStudents(){
		
		//Instanciamos un RestTemplate para facilitar la llamada.
		RestTemplate restTemplate = new RestTemplate();
		//Obtenemos y guaramos una arreglo de studentResponse llamando a la API.
		StudentResponse[] studentResponseArray = 
				restTemplate.getForObject("http://localhost:8081/api/v1/students", 
				StudentResponse[].class);
		
		//Inicializamos la lista
		list = new ArrayList<>();
		
		//Iteramos el arreglo obtenido con anterioridad.
		for(StudentResponse sr : studentResponseArray) 
		{
			//Agregamos los elementos del arreglo a la lista
			list.add(sr);
		}
		
		return list;
	}
	
	//Creamos un metodo para cargar la lista y aparte devolver un estudiante
	public StudentResponse getStudent() {
		//Si la lista es nula la recargamos.
		if(list == null) 
		{
			restCallGetStudents();
		}
		
		//Si la lista no es nula y no esta vacia
		if(list != null && !list.isEmpty()) 
		{
			//Devolvemos el elemento 0
			return list.remove(0);
		}
		
		return null;
	}
}
