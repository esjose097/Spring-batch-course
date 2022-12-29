/**
 * Este es el modelo de la petición http que necesitaremos.
 * Simula un:
 * {
 * 	"id":1,
 * 	"firstName":"jose",
 * 	"lastName":"casal",
 * 	"email":"jose@gmail.com"
 * }
 */
package com.infybuzz.app;

public class StudentResponse {

	private Long id;

	private String firstName;

	private String lastName;

	private String email;

	public StudentResponse(Long id, String firstName, String lastName, String email) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
