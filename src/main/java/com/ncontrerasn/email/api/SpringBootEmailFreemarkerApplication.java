package com.ncontrerasn.email.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncontrerasn.email.api.dto.MailRequest;
import com.ncontrerasn.email.api.dto.MailResponse;
import com.ncontrerasn.email.api.service.EmailService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

@SpringBootApplication
@RestController
public class SpringBootEmailFreemarkerApplication {

	@Autowired
	private EmailService service;

	//hacer el mapa con las listas de variables y valores
	Map<String, Object> modelo(MailRequest request){
		Map<String, Object> model = new HashMap<>();
		String variables = request.getVariables();
		String valores = request.getValores();
		ArrayList<String> listaVariables = new ArrayList<>(Arrays.asList(variables.split(",")));
		ArrayList<String> listaValores = new ArrayList<>(Arrays.asList(valores.split(",")));
		char c = 'a';
		for(int i = 0; i <  listaValores.size(); i++)
			model.put(String.valueOf(c++), listaValores.get(i));
		return model;
	}

	//una petición POST por cada servidor SMTP

	@PostMapping(value = "/sendingEmail")
	public MailResponse sendEmail( @RequestPart MultipartFile html, @RequestParam String request) throws MessagingException, IOException, TemplateException {
            System.out.println("hola como estas");    
            System.out.println(request);
		ObjectMapper mapper = new ObjectMapper();
		MailRequest req = mapper.readValue(request, MailRequest.class);
		Map<String, Object> model = modelo(req);
		return service.sendEmail(req, html, model);
	}

	@PostMapping("/sendingEmail2")
	public MailResponse sendEmail2(@RequestParam String request, @RequestPart MultipartFile html) throws MessagingException, IOException, TemplateException {
		ObjectMapper mapper = new ObjectMapper();
		MailRequest req = mapper.readValue(request, MailRequest.class);
		Map<String, Object> model = modelo(req);
		return service.sendEmail2(req, html, model);
	}
        
        @PostMapping("/sendingEmail3")
	public String sendEmail3(@RequestParam String request,@RequestParam String request2) throws MessagingException, IOException, TemplateException {
      


        System.out.println(request+request2);
            return request+request2;
	}
        
         @PostMapping("/sendingEmail4")
	public String sendEmail4(@RequestPart MultipartFile html) throws MessagingException, IOException, TemplateException {
        
            

        System.out.println(html);
            return null;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEmailFreemarkerApplication.class, args);
	}
}