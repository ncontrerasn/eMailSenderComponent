package com.javatechie.email.api.dto;

import lombok.Data;

@Data
public class MailRequest {

	private String to;
	private String subject;
	private String plantilla;
	private String valores;
	private String variables;

}