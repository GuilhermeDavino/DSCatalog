package com.devsuperior.dscatalog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDTO {
	
	@JsonProperty(value = "email")
	@NotBlank(message = "Campo obrigatório")
	@Email(message = "Campo inválido")
	private String email;
	
	public EmailDTO() {
		
	}
	
	public EmailDTO(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	
	

}
