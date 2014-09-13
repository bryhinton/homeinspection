package email;
// File Name Email.java

import sqlrow.Utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Email
{
	protected String to = null;
	protected String from = null;
	protected String subject = null;
	protected String content = null;
	protected String fileName = null;

	public void send() throws Exception
	{
		// Assuming you are sending email from localhost
		String host = "smtpout.secureserver.net";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "80");
		//properties.setProperty("mail.smtp.user", "bryhinton@hotmail.com");
		//properties.setProperty("mail.smtp.socketFactory.port", "465");
		//properties.setProperty("mail.smtp.socketFactory.class",	"javax.net.ssl.SSLSocketFactory");
		//properties.setProperty("mail.smtp.socketFactory.fallback", "false");
		//properties.setProperty("mail.transport.protocol", "smtps");
		//properties.setProperty("mail.smtp.quitwait", "false");

		// Get the default Session object.
		Session session = Session.getInstance(properties, (new Email()).new SMTPAuthenticator());
		//session.setDebug(true);

		try
		{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			InternetAddress[] addresses = new InternetAddress[1];
			addresses[0] = new InternetAddress(getFrom(), "Home Safety Inspection");
			// Set From: header field of the header.
			message.setReplyTo(addresses);
			message.setFrom(addresses[0]);//new InternetAddress(getFrom()));

			// Set To: header field of the header.
			String[] emailAddresses = getTo().split(",");

			for(String emailAddress : emailAddresses) {
				if(Utils.notEmpty(emailAddress)) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
				}
			}

			// Set Subject: header field
			message.setSubject(getSubject());

			// Now set the actual message
			if(Utils.isEmpty(fileName)) {
				message.setContent(getContent(), "text/html");
			}
			else {
				// create the message part
				MimeBodyPart messageBodyPart =
						new MimeBodyPart();

				//fill message
				//messageBodyPart.setText(getContent());
				messageBodyPart.setContent(getContent(), "text/html");

				Multipart multipart = new MimeMultipart("mixed");
				multipart.addBodyPart(messageBodyPart);

				// Part two is attachment
				messageBodyPart = new MimeBodyPart();
				DataSource source =	new FileDataSource(fileName);
				messageBodyPart.setDataHandler(new DataHandler(source));

				String[] parts = fileName.split("/");
				messageBodyPart.setFileName(parts[parts.length - 1]);
				multipart.addBodyPart(messageBodyPart);

				// Put parts in message
				message.setContent(multipart);
			}

			// Send message
//			Transport transport = session.getTransport("smtp");
//			transport.connect(host, 587, "bryhinton@hotmail.com", "h@rv@rd841");
//			transport.sendMessage(message, message.getAllRecipients());
//			transport.close();
			Transport.send(message);
			System.out.println("Message sent successfully!!");
		}
		catch (Exception mex)
		{
			mex.printStackTrace();
			throw mex;
		}
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String text) {
		this.content = text;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private class SMTPAuthenticator extends Authenticator
	{
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication("info@servicetechapps.com", "Plumbing1");
		}
	}
}