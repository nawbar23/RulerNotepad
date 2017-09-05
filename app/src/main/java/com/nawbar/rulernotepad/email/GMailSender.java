package com.nawbar.rulernotepad.email;

import android.os.Environment;
import android.util.Pair;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.security.Security;
import java.util.List;
import java.util.Properties;

/**
 * Created by Bartosz Nawrot on 01.09.2017.
 */

public class GMailSender extends javax.mail.Authenticator {
    private String mailHost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String sender, String recipient, String subject, String body, List<Pair<File, String>> attachments) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setSender(new InternetAddress(sender));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);

        BodyPart textPart = new MimeBodyPart();
        //messageBodyPart1.setContent(body, "text/html");// for a html email
        textPart.setText(body);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        for (Pair<File, String> a : attachments) {
            MimeBodyPart photoPart = new MimeBodyPart();
            DataSource source = new FileDataSource(a.first.getAbsolutePath());
            photoPart.setDataHandler(new DataHandler(source));
            photoPart.setFileName(a.second);
            multipart.addBodyPart(photoPart);
        }

        message.setContent(multipart);
        Transport.send(message);
    }

    public synchronized void sendHtmlMail(String sender, String recipient, String subject, String body, List<Pair<File, String>> attachments) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setSender(new InternetAddress(sender));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);

        BodyPart textPart = new MimeBodyPart();
        textPart.setContent(body, "text/html");// for a html email

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        for (Pair<File, String> a : attachments) {
            MimeBodyPart photoPart = new MimeBodyPart();
            DataSource source = new FileDataSource(a.first.getAbsolutePath());
            photoPart.setDataHandler(new DataHandler(source));
            photoPart.setFileName(a.second);
            multipart.addBodyPart(photoPart);
        }

        message.setContent(multipart);
        Transport.send(message);
    }
}
