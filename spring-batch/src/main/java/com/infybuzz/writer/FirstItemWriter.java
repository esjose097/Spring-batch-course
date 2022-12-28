/**
 * Esta clase representa un ItemWriter por lo cual necesitara una salida la cual se marca despues
 * de la implementación de la interfaz.
 */
package com.infybuzz.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class FirstItemWriter implements ItemWriter<Long>{

	/*
	 * Este es el metodo de escritura..
	 */
	@Override
	public void write(List<? extends Long> items) throws Exception {
		System.out.println("Inside Iterm writer");
		items.stream().forEach(System.out::println); //<--No conocia esta forma de iterar una lista.
		
	}

}