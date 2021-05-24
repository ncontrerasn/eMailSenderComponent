package com.javatechie.email.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.javatechie.email.api.dto.MailRequest;
import com.javatechie.email.api.dto.MailResponse;
import com.javatechie.email.api.service.EmailService;
import javax.mail.MessagingException;

@SpringBootApplication
@RestController
public class SpringBootEmailFreemarkerApplication {

	@Autowired
	private EmailService service;

	@PostMapping("/sendingEmail")
	public MailResponse sendEmail(@RequestBody MailRequest request) throws MessagingException, IOException, TemplateException {
		Map<String, Object> model = new HashMap<>();
		model.put("recipient", request.getName());
		model.put("ticket", request.getNumber());
		return service.sendEmail(request, model);
	}

	@PostMapping("/sendingEmail2")
	public MailResponse sendEmail2(@RequestBody MailRequest request) throws MessagingException, IOException, TemplateException{
		Map<String, Object> model = new HashMap<>();
		model.put("recipient", request.getName());
		model.put("ticket", request.getNumber());
		return service.sendEmail2(request, model);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEmailFreemarkerApplication.class, args);
	}
}