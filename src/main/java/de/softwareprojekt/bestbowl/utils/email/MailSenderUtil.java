package de.softwareprojekt.bestbowl.utils.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * @author Matija Kopschek
 */
public class MailSenderUtil {

    protected Session mailSession; // Session which represents the connection with the E-Mail-Server

    // Connecting with the Email-Server via smtpPortAddress and smtpHostAddress and Loging into your Email account
    public void login(String smtpHostAddress, String smtpPortAddress, String username, String password) {
        // List of properties that are important for the log in for the email server
        Properties properties = new Properties();
        addProperties(smtpHostAddress, smtpPortAddress, properties);

        Authenticator authenticator = loginAuthentificator(username, password);
        this.mailSession = Session.getInstance(properties, authenticator); // creating a new session and loging in
        System.out.println("Successfull Login!");
    }

    // Adding all necessary properties for a successful connection with SMTP-Server
    private void addProperties(String smtpHostAddress, String smtpPortAddress, Properties properties) {
        properties.put("mail.smtp.host", smtpHostAddress); // smtp-server host name
        properties.put("mail.smtp.socketFactory.port", smtpPortAddress); // smtp-server port number
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // using ssl
        properties.put("mail.smtp.auth", "true"); // smtp-server has an authentication process (see loginAuthenticator()-method)
        properties.put("mail.smtp.port", smtpPortAddress); // smtp-server port number
    }

    // Logging into the Mail-Server with user name and password
    private Authenticator loginAuthentificator(String username, String password) {
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        return authenticator;
    }

    // Sending the Email with its contents (Subject, message) from the transmitter
    // to the receiver
    public void send(String transmitterMail, String transmitterName, String receiverAddresses, String subject,
                     String mailText) throws MessagingException, IllegalStateException, UnsupportedEncodingException {

        loginCheck();
        // MimeMessage allows attachments to the Email (for the receipt.pdf)
        MimeMessage mimeMessage = new MimeMessage(mailSession);
        headerSettings(mimeMessage);
        messageSettings(transmitterMail, transmitterName, receiverAddresses, subject, mailText, mimeMessage);

        System.out.println("E-Mail is being sent...");
        Transport.send(mimeMessage);
        System.out.println("E-Mail sent");
    }

    private Message messageSettings(String transmitterMail, String transmitterName, String receiverAddresses,
                                    String subject, String mailText, MimeMessage mimeMessage)
            throws MessagingException, UnsupportedEncodingException {

        mimeMessage.setFrom(new InternetAddress(transmitterMail, transmitterName)); // Emailaddress + name is being transmitted
        mimeMessage.setReplyTo(InternetAddress.parse(transmitterMail, false)); // Transmitter information (senderMail + dont
        // strict enforce RFC822 syntax)
        mimeMessage.setSubject(subject, "UTF-8"); // subject for email being set with UTF-8 encoding (e.g. Umlaute)
        mimeMessage.setText(mailText, "UTF-8"); // message for email being set with UTF-8 encoding (e.g. Umlaute)
        mimeMessage.setSentDate(new Date()); // current Date
        //TODO Anh�nge


        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverAddresses, false)); // Recipient is only one, no cc																							// person no cc
        return mimeMessage;
    }

    private Message headerSettings(MimeMessage mimeMessage) throws MessagingException {
        mimeMessage.addHeader("Content-type", "text/HTML; charset=UTF-8"); // So the message gets displayed correctly in HTML with UTF8 encoding (e.g. Umlaute)
        mimeMessage.addHeader("format", "flowed"); // formatting
        mimeMessage.addHeader("Content-Transfer-Encoding", "8bit"); // character encoding
        return mimeMessage;
    }

    // Checking if your logged into your mailSession, login()-method executed
    private void loginCheck() {
        if (mailSession == null) {
            throw new IllegalStateException("Please Log in!");
        }
    }
}
