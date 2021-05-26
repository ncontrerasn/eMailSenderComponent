package com.javatechie.email.api.service;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import com.javatechie.email.api.dto.MailRequest;
import com.javatechie.email.api.dto.MailResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.util.Properties;

@Service
public class EmailService {

	@Autowired
	Configuration config;

	Properties props = new Properties();

	MailResponse response = new MailResponse();

	public void propiedades() {
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");
	}

	public Session sesion(String usuario, String clave){
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(usuario, clave);
					}
				});
		return session;
	}

	public void llenarMandarMensaje(MailRequest request, Map<String, Object> model, Session session, String dir, String personal) throws IOException, MessagingException, TemplateException {
		MimeMessage message = new MimeMessage(session);

		String variables = request.getVariables();
		String valores = request.getValores();
		ArrayList<String> listaVariables = new ArrayList<>(Arrays.asList(variables.split(",")));
		ArrayList<String> listaValores = new ArrayList<>(Arrays.asList(valores.split(",")));

		message.setFrom(new InternetAddress(dir, personal));
		String asunto = request.getSubject();
		for (int i = 0; i < listaVariables.size(); i++)
			asunto = asunto.replace(listaVariables.get(i), listaValores.get(i));

		asunto = asunto.replace("{","");
		asunto = asunto.replace("}","");
		asunto = asunto.replace("%","");
		message.setSubject(asunto);

		//TO se puede cambiar a CC
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(request.getTo()));

		String nombre = "target/classes/templates/plantilla.ftl";
		String plantilla = request.getPlantilla();

		plantilla = plantilla.replace("%","$");
		char c = 'a';

		for (int i = 0; i < listaVariables.size(); i++)
			plantilla = plantilla.replace(listaVariables.get(i), String.valueOf(c++));

		try {
			FileWriter myWriter = new FileWriter(nombre);
			myWriter.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
					"<head>\n" +
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
					"<title>Plantilla Correo UIFCE</title>\n" +
					"</head>\n" +
					"\n" +
					"<body>");
			myWriter.write(plantilla);
			myWriter.write("</body>\n" +
					"</html>");
			myWriter.close();
		}catch (IOException e) {}

		Template t = config.getTemplate("plantilla.ftl");
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
		message.setContent(html, "text/html; charset=utf-8");
		Transport.send(message);
		response.setMessage("mail send to : " + request.getTo());
		response.setStatus(Boolean.TRUE);
	}

	public MailResponse sendEmail(MailRequest request, Map<String, Object> model) throws IOException, MessagingException, TemplateException {
		propiedades();
		Session session = sesion("uifce.apps.test2@gmail.com", "oxfsouitcmnjjtqs");
		llenarMandarMensaje(request, model, session, "uifce.apps.test2@gmail.com", "UIFCE");
		return response;
	}

	public MailResponse sendEmail2(MailRequest request, Map<String, Object> model) throws IOException, MessagingException, TemplateException {
		propiedades();
		Session session = sesion("uifce.apps.test@gmail.com", "dbzafmlgawmmqwrm");
		llenarMandarMensaje(request, model, session, "uifce.apps.test@gmail.com", "UACE");
		return response;
	}

}