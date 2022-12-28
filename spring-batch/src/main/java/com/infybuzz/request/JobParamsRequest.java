/**
 * Esta clase es un modelo para los parametros se intenta recrear un json como este:
 * [
 * {
 * 	"paramKey" : "123",
 * "paramValue" : "abc"
 * },
 * {
 * 	"paramKey" : "321",
 * 	"paramValue : "cba"
 * }
 * ]
 */
package com.infybuzz.request;

public class JobParamsRequest {

	private String paramKey;
	
	private String paramValue;

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
	
}
