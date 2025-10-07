package com.walletapp.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for sending emails using SMTP.
 * Loads configuration from the {@code email.properties} file to avoid hardcoding sensitive credentials.
 */
public class EmailUtil {

    /** SMTP properties loaded from email.properties */
    private static final Properties emailProps = new Properties();

    static {
        try (InputStream input = EmailUtil.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find email.properties");
            }
            emailProps.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading email.properties", e);
        }
    }

    /**
     * Sends an email using SMTP configuration from {@code email.properties}.
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body text
     * @throws MessagingException if sending fails
     */
    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        String host = emailProps.getProperty("mail.smtp.host");
        String port = emailProps.getProperty("mail.smtp.port");
        String fromEmail = emailProps.getProperty("mail.from");
        String fromPassword = emailProps.getProperty("mail.password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}
