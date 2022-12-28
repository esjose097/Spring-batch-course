/**
 * Esta clase representa un ItemReader el cual se utiliza para leer datos.
 */
package com.infybuzz.reader;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component //<-- Hay que marcarlo como componente.
public class FirstItemReader implements ItemReader<Integer>{

	/*Se supone que los ItemReaders reciben o tienen una fuente de donde poder empezar a leer datos
	 * por lo cual en este caso la estamos simulando con una lista de enteros*/
	List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
	int i = 0;
	/*
	 * Tengo entendido que este metodo se usa para estar leyendo una entrada, este método se estará repitiendo en loop
	 * de manera indefinida segun tengo entendido.
	 */
	@Override
	public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		System.out.println("Inside Item Reader");
		Integer item;
		if(i < list.size()) 
		{
			item = list.get(i);
			i++;
			return item;
		}
		i = 0;
		return null;
	}
}
