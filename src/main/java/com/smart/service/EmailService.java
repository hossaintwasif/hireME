package com.smart.service;

import org.springframework.stereotype.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
	public  boolean  sendEmail(String message, String subject, String to) {
			  
		boolean f=false;
		String from = "hossaintwasif98@gmail.com";
			  //Variable for gmail 
		      String host="smtp.gmail.com";
			  
			  //Get the system properties 
		      Properties properties=System.getProperties();
			  System.out.println("Properties "+properties);
			  
			  //Setting important information to properties object
			  
			  //Host set 
			  properties.put("mail.smtp.host", host);
			  properties.put("mail.smtp.port", "465");
			  properties.put("mail.smtp.ssl.enable", "true");
			  properties.put("mail.smtp.auth", "true");
			  
			  
			  //To get the session object 
			  Session session =Session.getInstance(properties,new Authenticator() {
			  
			  @Override protected PasswordAuthentication getPasswordAuthentication() {
			  
			  return new PasswordAuthentication("hossaintwasif98@gmail.com","vdpz czes jmlg lhlo"); }
			  
			  });
			  
			  session.setDebug(true);
			  
			  //Step 2: Compose the message [text,multi media] 
			  MimeMessage m=new MimeMessage(session);
			  
			  try {
			  
			  //From email
				  m.setFrom(from);
			  
			  //Adding recipient to message 
				  m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			  
			  //Adding subject 
				  m.setSubject(subject);
			  
			  //Adding text to message 
				//  m.setText(message);
				  m.setContent(message,"text/html");
			  
			  //Send
			  
			  //Step 3: Sending message using transport class 
				  Transport.send(m);
			  
			  System.out.println("Send successfully.....!!"); 
			  f=true;
			  } catch (MessagingException e) {
			  
			  e.printStackTrace(); 
			  }
			return f;
			  
			  }
	
}
