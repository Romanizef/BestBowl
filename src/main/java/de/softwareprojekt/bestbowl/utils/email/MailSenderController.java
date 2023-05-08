package de.softwareprojekt.bestbowl.utils.email;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;

/**
 * Controller class for sending emails.
 * 
 * @author Matija Kopschek
 */
public class MailSenderController {

    /**
     * Uses the emailUtils send-method to send an email and sets all the email
     * information. The email is sent to the client. Can be used in other classes to
     * automatically send emails
     * 
     * @param booking
     */
    public void sendMail(BowlingAlleyBooking booking) {
        BowlingCenter bowlingCenter = new BowlingCenter();

        String senderMail = bowlingCenter.getEmail(); // The E-Mail of the transmitter
        String password = bowlingCenter.getPassword(); // specific password created for this program
        String smtphost = bowlingCenter.getSmtpHost(); // smtp.gmail.com: host is gmail.com
        String smtpport = bowlingCenter.getSmtpPort(); // 465: is the port number for SSL, if TLS is being used then 587

        // String recepientMail = booking.getClient().getEmail(); // From class Customer
        String recepientMail = "bestbowl11@gmailcom"; // TODO nur zum testen nachher wieder rausnehmen
        String recepientLastName = booking.getClient().getLastName(); // From class Customer
        String recepientFirstName = booking.getClient().getFirstName(); // From class Customer
        int invoiceNumber = booking.getId(); // BookingID as invoiceNumber

        MailSenderUtil sender = new MailSenderUtil();
        sender.login(smtphost, smtpport, senderMail, password);
        String subject = "Ihre Best Bowl Rechnung";
        String mailText = "Sehr geehrter " + recepientFirstName + " " + recepientLastName + ",\n"
                + "\nDanke für Ihren Einkauf!\nIhre Rechnungsnummer ist:" + invoiceNumber;

        try {
            sender.send(senderMail, "Best Bowl", recepientMail, subject, mailText, booking);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}