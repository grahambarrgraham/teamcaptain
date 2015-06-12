package org.rrabarg.teamcaptain.channel.email;

import org.rrabarg.teamcaptain.channel.Email;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.Properties;

/**
 * Created by graham on 06/06/15.
 */
//@Component
@SuppressWarnings("unused")
public class SmtpSender {

    private String host;
    private String user;
    private String password;

    @PostConstruct
    public void setup() {
        Properties props = System.getProperties();
        props.put("mail.smtps.host","smtp.gmail.com");
        props.put("mail.smtps.auth","true");

        host = "smtp.gmail.com";
        user = "admin@tovare.com";
        password = "<insert password here>";
    }

    public void sendMessage(Email email) throws MessagingException {

//        Session session = Session.getInstance(props, null);
//        Message msg = new MimeMessage(session);
//
//        msg.setFrom(new InternetAddress(email.getFromAddress()));
//        msg.setRecipients(Message.RecipientType.TO,
//                InternetAddress.parse(email.getFromAddress(), false));
//        msg.setSubject(email.getSubject());
//        msg.setText(email.getBody());
//        msg.setHeader("X-Mailer", "Team Captain");
//        msg.setSentDate(email.getTimestamp());
//        SMTPTransport t =
//                (SMTPTransport) session.getTransport("smtps");
//        t.connect(host, user, password);
//        t.sendMessage(msg, msg.getAllRecipients());
//        t.close();
    }

}