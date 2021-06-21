package com.ncontrerasn.email.api.dto;

import lombok.Data;

import java.io.File;

@Data
public class MailRequest {

	private String to;
	private String subject;
	private String plantilla;
	private String valores;
	private String variables;
	private File html;

}
