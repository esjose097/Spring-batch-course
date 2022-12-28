/**
 * Esta clase representa un ItemProcessor, este tipo de clases estan en medio de
 * el ItemReader y el ItemWriter por lo cual al momento de implementar la interfaz
 * "ItemProcessor" necesitamos como generico un I(Input) y un O(Output) este tipo de clases
 * se usan para procesar despues de una lectura.
 */
package com.infybuzz.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component //<-- Hay que marcarlo como componente
public class FirstItemProcessor implements ItemProcessor<Integer, Long>{
	
	/*Este es el metodo de proceso es decir aquí se realiza algun tipo de
	 * proceso o logica con los datos que leimos previamente en el ItemReader.
	*/ 
	@Override
	public Long process(Integer item) throws Exception {
		System.out.println("Inside Item processor");
		return Long .valueOf(item + 20);
	}

}
