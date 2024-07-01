package org.caesar.userservice.Utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class Utils {

    private Properties prop;

    public boolean emailSender(String username, String userEmail, String otp) {

        prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable","true");
        prop.put("mail.smtp.host","smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        Session session = getSession();

        try {
            // Creazione del messaggio email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("caesar.magnus.info@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Ripristino password");
            message.setText("<p style=\"text-align: left;\">&nbsp;</p>\n" +
                    "<p>&nbsp;</p>\n" +
                    "<table border=\"10\" width=\"690\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\n" +
                    "    <tbody>\n" +
                    "    <tr>\n" +
                    "        <td style=\"background-color: #91defa; padding: 20px; text-align: center;\">\n" +
                    "            <h1 style=\"color: #333333;\">Verifica la tua identit&agrave;</h1>\n" +
                    "            <h3 style=\"text-align: left;\"><strong>Gentile "+username+"</strong></h3>\n" +
                    "            <h4 style=\"text-align: left;\"><strong>Inserisci questo codice sul sito della Banca Caesar Magnus per verificare la tua identit&agrave;</strong></h4>\n" +
                    "            <h2><strong>Codice OTP:</strong></h2>\n" +
                    "            <h1>"+otp+"</h1>\n" +
                    "            <p style=\"text-align: left;\">&nbsp;</p>\n" +
                    "            <h3 style=\"text-align: center;\"><strong>Questo codice scadr&agrave; tra 10 minuti.</strong></h3>\n" +
                    "            <p style=\"text-align: left;\"><strong>Se non riconosci l'indirizzo caesar.magnus.info@gmail.com, puoi ignorare questa email.</strong></p>\n" +
                    "            <p style=\"text-align: left;\"><strong>Ti preghiamo di non rispondere a questa email.</strong></p>\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    </tbody>\n" +
                    "</table>");

            // Invio dell'email
            Transport.send(message);

            return true;

        } catch (MessagingException e) {
            log.debug("Errore nell'invio dell'email");
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
