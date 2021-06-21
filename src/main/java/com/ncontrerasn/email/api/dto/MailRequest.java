package com.ncontrerasn.email.api.dto;

import lombok.Data;

@Data
public class MailRequest {

	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String plantilla;
	private String valores;
	private String variables;

}
