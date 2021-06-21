package com.ncontrerasn.email.api.service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import com.ncontrerasn.email.api.dto.MailRequest;
import com.ncontrerasn.email.api.dto.MailResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.web.multipart.MultipartFile;

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
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");
	}

	//iniciar sesión servidor SMTP
	public Session sesion(String usuario, String clave){
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(usuario, clave);
					}
				});
		return session;
	}

	public void llenarMandarMensaje(MailRequest request, MultipartFile htmlP, Map<String, Object> model, Session session, String dir, String personal) throws IOException, MessagingException, TemplateException {
		MimeMessage message = new MimeMessage(session);

		//obtener la lista de variables y valores de la petición
		String variables = request.getVariables();
		String valores = request.getValores();
		ArrayList<String> listaVariables = new ArrayList<>(Arrays.asList(variables.split(",")));
		ArrayList<String> listaValores = new ArrayList<>(Arrays.asList(valores.split(",")));

		message.setFrom(new InternetAddress(dir, personal));
		String asunto = request.getSubject();

		//reemplzar variables para pasar a la pnatilla
		for (int i = 0; i < listaVariables.size(); i++)
			asunto = asunto.replace(listaVariables.get(i), listaValores.get(i));

		//limpiar de símbolos no deseados
		asunto = asunto.replace("{","");
		asunto = asunto.replace("}","");
		asunto = asunto.replace("%","");
		message.setSubject(asunto);

		//TO
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(request.getTo()));

		//CC
		message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(request.getCc()));

		//BCC
		message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(request.getBcc()));

		//escribir la plantilla
		String nombre = "target/classes/templates/plantilla.ftl";
		String plantilla = "";

		//convertir el archivo recibido del POST a un archivo tipo file
		MultipartFile multiFile = htmlP;
		File file = new File(System.getProperty("user.dir") + "/files/plantilla.txt");
		multiFile.transferTo(file);

		try {
			Scanner myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
				plantilla += myReader.nextLine();
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		//reemplazar % por $ para que se puedan reemplzar los valores de la plantilla
		plantilla = plantilla.replace("%","$");
		char c = 'a';

		//reemplzar las variables para evitar el problema de nombres de varialbes con .
		for (int i = 0; i < listaVariables.size(); i++)
			plantilla = plantilla.replace(listaVariables.get(i), String.valueOf(c++));

		//escribir el encabezado del HTML
		try {
			FileWriter myWriter = new FileWriter(nombre);
			myWriter.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
					"<head>\n" +
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
					"<title>Plantilla Correo UIFCE</title>\n" +
					"</head>\n" +
					"<body>");
			myWriter.write(plantilla);
			myWriter.write("</body>\n" +
					"</html>");
			myWriter.close();
		}catch (IOException e) {}

		Template t = config.getTemplate("plantilla.ftl");

		//reemplazar las variables de la plantilla
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
		message.setContent(html, "text/html; charset=utf-8");

		//mandar mensaje
		Transport.send(message);

		//respuesta del POST
		response.setMessage("mail send to: " + request.getTo() + ", cc: " + request.getCc() + ", bcc: " + request.getBcc());
		response.setStatus(Boolean.TRUE);
	}

	public MailResponse sendEmail(MailRequest request, MultipartFile html, Map<String, Object> model) throws IOException, MessagingException, TemplateException {
		propiedades();
		Session session = sesion("uifce.apps.test2@gmail.com", "oxfsouitcmnjjtqs");
		llenarMandarMensaje(request, html, model, session, "uifce.apps.test2@gmail.com", "UIFCE");
		return response;
	}

	public MailResponse sendEmail2(MailRequest request, MultipartFile html, Map<String, Object> model) throws IOException, MessagingException, TemplateException {
		propiedades();
		Session session = sesion("uifce.apps.test@gmail.com", "dbzafmlgawmmqwrm");
		llenarMandarMensaje(request, html, model, session, "uifce.apps.test@gmail.com", "UACE");
		return response;
	}

}