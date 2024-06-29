package org.caesar.userservice.Utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class Utils {

    private Properties prop;

    public boolean emailSender(String username, String userEmail) {
        prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable","true");
        prop.setProperty("mail.smtp.auth","smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        Session session = getSession();

        try {
            // Creazione del messaggio email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("caesar.magnus.info@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Ripristino password");
            message.setText("Sesso");

            // Invio dell'email
            Transport.send(message);

            return true;

        } catch (MessagingException e) {
            return false;
        }
    }

    private Session getSession() {
        return Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("caesar.magnus.info@gmail.com", "Ciccio_Luzzi_Cesare_Imperatore_10469");
            }
        });
    }
}
