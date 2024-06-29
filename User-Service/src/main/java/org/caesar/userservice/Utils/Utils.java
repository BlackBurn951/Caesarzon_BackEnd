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
        System.out.println("SONO NELLEMAIL SENDER 1");

        prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable","true");
        prop.put("mail.smtp.host","smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        System.out.println("SONO NELLEMAIL SENDER 2");

        Session session = getSession();

        System.out.println("SONO NELLEMAIL SENDER 3");

        try {
            // Creazione del messaggio email
            System.out.println("SONO NELLEMAIL SENDER 4");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("caesar.magnus.info@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Ripristino password");
            message.setText("Sesso");
            System.out.println("SONO NELLEMAIL SENDER 5");

            // Invio dell'email
            Transport.send(message);

            System.out.println("SONO NELLEMAIL SENDER 6");

            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Session getSession() {
        return Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("caesar.magnus.info@gmail.com", "dkmx anru vpjy cnvb");
            }
        });
    }
}
