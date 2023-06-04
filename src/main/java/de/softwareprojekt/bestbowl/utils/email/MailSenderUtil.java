package de.softwareprojekt.bestbowl.utils.email;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.utils.pdf.PDFUtils;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

/**
 * Creates an email
 * 
 * @author Matija Kopschek
 */
public class MailSenderUtil {

    protected Session mailSession; // Session which represents the connection with the E-Mail-Server
    private BodyPart messageBodyPart;
    private Multipart multipart;
    private BodyPart attachmentBodyPart;

    /**
     * Connecting with the Email-Server via smtpPortAddress and smtpHostAddress and
     * Loging into your Email account
     * 
     * @param smtpHostAddress
     * @param smtpPortAddress
     * @param username
     * @param password
     * @see #addProperties(String, String, Properties)
     * @see #loginAuthentificator(String, String)
     */
    public void login(String smtpHostAddress, String smtpPortAddress, String username, String password) {
        // List of properties that are important for the log in for the email server
        Properties properties = new Properties();
        addProperties(smtpHostAddress, smtpPortAddress, properties);

        Authenticator authenticator = loginAuthentificator(username, password);
        this.mailSession = Session.getInstance(properties, authenticator); // creating a new session and loging in
    }

    /**
     * Adding all necessary properties for a successful connection with SMTP-Server
     * 
     * @param smtpHostAddress
     * @param smtpPortAddress
     * @param properties
     */
    private void addProperties(String smtpHostAddress, String smtpPortAddress, Properties properties) {
        properties.put("mail.smtp.host", smtpHostAddress); // smtp-server host name
        properties.put("mail.smtp.socketFactory.port", smtpPortAddress); // smtp-server port number
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // using ssl
        properties.put("mail.smtp.auth", "true"); // smtp-server has an authentication process (see
                                                  // loginAuthenticator()-method)
        properties.put("mail.smtp.port", smtpPortAddress); // smtp-server port number
    }

    /**
     * Logging into the Mail-Server with user name and password
     * 
     * @param username
     * @param password
     * @return {@code  Authenticator}
     */
    private Authenticator loginAuthentificator(String username, String password) {
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        return authenticator;
    }

    /**
     * Sending the Email with its contents (Subject, message etc.) from the
     * transmitter
     * to the receiver. This sending Method sends an attachment of the receipt to
     * the client.
     * 
     * @param transmitterMail
     * @param transmitterName
     * @param receiverAddresses
     * @param subject
     * @param mailText
     * @param booking
     * @throws MessagingException
     * @throws IllegalStateException
     * @throws UnsupportedEncodingException
     * @see #headerSettings(MimeMessage)
     * @see #attachmentMailSettings(String, String, String, String, String,
     *      MimeMessage,
     *      BowlingAlleyBooking)
     * @see #loginCheck()
     */
    public void sendAttachmentMail(String transmitterMail, String transmitterName, String receiverAddress, String subject,
            String mailText, BowlingAlleyBooking booking)
            throws MessagingException, IllegalStateException, UnsupportedEncodingException {

        loginCheck();
        // MimeMessage allows attachments to the Email (for the receipt.pdf)
        MimeMessage mimeMessage = new MimeMessage(mailSession);
        headerSettings(mimeMessage);
        attachmentMailSettings(transmitterMail, transmitterName, receiverAddress, subject, mailText, mimeMessage,
                booking);

        Transport.send(mimeMessage);
    }

    /**
     * Sending the Email with its contents (Subject, message etc.) from the
     * transmitter
     * to the receiver. This sending Method sends an attachment of the receipt to
     * the client.
     * 
     * @param transmitterMail
     * @param transmitterName
     * @param receiverAddresses
     * @param subject
     * @param mailText
     * @param booking
     * @throws MessagingException
     * @throws IllegalStateException
     * @throws UnsupportedEncodingException
     * @see #headerSettings(MimeMessage)
     * @see #attachmentMailSettings(String, String, String, String, String,
     *      MimeMessage,
     *      BowlingAlleyBooking)
     * @see #loginCheck()
     */
    public void sendMessageOnlyMail(String transmitterMail, String transmitterName, String receiverAddress,
            String subject, String mailText)
            throws MessagingException, IllegalStateException, UnsupportedEncodingException {

        loginCheck();
        MimeMessage mimeMessage = new MimeMessage(mailSession);
        headerSettings(mimeMessage);
        messageOnlyMailSettings(transmitterMail, transmitterName, receiverAddress, subject, mailText,
                mimeMessage);

        Transport.send(mimeMessage);
    }

    /**
     * Sets all the important information for the booking confirmation Email
     * (transmitter, receiver, subject, message, attachment)
     * 
     * @param transmitterMail
     * @param transmitterName
     * @param receiverAddresses
     * @param subject
     * @param mailText
     * @param mimeMessage
     * @param booking
     * @return {@code Message}
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private Message messageOnlyMailSettings(String transmitterMail, String transmitterName,
            String receiverAddresses, String subject, String mailText, MimeMessage mimeMessage)
            throws MessagingException, UnsupportedEncodingException {

        // Emailaddress + name is being transmitted
        mimeMessage.setFrom(new InternetAddress(transmitterMail, transmitterName));
        // Transmitter information (senderMail + dont strict enforce RFC822 syntax)
        mimeMessage.setReplyTo(InternetAddress.parse(transmitterMail, false));
        mimeMessage.setSubject(subject, "UTF-8"); // subject for email being set with UTF-8 encoding (e.g. Umlaute)
        mimeMessage.setSentDate(new Date()); // current Date
        mimeMessage.setText(mailText, "UTF-8");
        // Only one Recipient. no cc
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverAddresses, false));
        return mimeMessage;
    }

    /**
     * Sets all the important information for the invoice Email (transmitter,
     * receiver,
     * subject, message, attachment)
     * 
     * @param transmitterMail
     * @param transmitterName
     * @param receiverAddresses
     * @param subject
     * @param mailText
     * @param mimeMessage
     * @param booking
     * @return {@code Message}
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private Message attachmentMailSettings(String transmitterMail, String transmitterName, String receiverAddresses,
            String subject, String mailText, MimeMessage mimeMessage, BowlingAlleyBooking booking)
            throws MessagingException, UnsupportedEncodingException {

        messageBodyPart = new MimeBodyPart();
        multipart = new MimeMultipart();
        attachmentBodyPart = new MimeBodyPart();
        // Emailaddress + name is being transmitted
        mimeMessage.setFrom(new InternetAddress(transmitterMail, transmitterName));
        // Transmitter information (senderMail + dont strict enforce RFC822 syntax)
        mimeMessage.setReplyTo(InternetAddress.parse(transmitterMail, false));

        mimeMessage.setSubject(subject, "UTF-8"); // subject for email being set with UTF-8 encoding (e.g. Umlaute)
        mimeMessage.setSentDate(new Date()); // current Date

        // Part 1 is message
        messageBodyPart.setText(mailText);
        multipart.addBodyPart(messageBodyPart);

        // Part 2 is attachment
        // Create pdf from pdfUtils and add to multipart
        DataSource source = new ByteArrayDataSource(PDFUtils.createInvoicePdf(booking), "application/pdf");
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName("rechnung.pdf");
        multipart.addBodyPart(attachmentBodyPart);

        mimeMessage.setContent(multipart);
        // Only one Recipient
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverAddresses, false));
        // no cc
        return mimeMessage;
    }

    /**
     * Sets all the important information for the email header
     * 
     * @param mimeMessage
     * @return {@code MimeMessage}
     * @throws MessagingException
     */
    private Message headerSettings(MimeMessage mimeMessage) throws MessagingException {
        mimeMessage.addHeader("Content-type", "text/HTML; charset=UTF-8"); // So the message gets displayed correctly in
                                                                           // HTML with UTF8 encoding (e.g. Umlaute)
        mimeMessage.addHeader("format", "flowed"); // formatting
        mimeMessage.addHeader("Content-Transfer-Encoding", "8bit"); // character encoding
        return mimeMessage;
    }

    /**
     * Checking if your logged into your mailSession, login()-method executed
     */
    private void loginCheck() {
        if (mailSession == null) {
            throw new IllegalStateException("Please Log in!");
        }
    }
}
