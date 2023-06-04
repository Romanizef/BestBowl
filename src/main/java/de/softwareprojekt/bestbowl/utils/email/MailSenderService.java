package de.softwareprojekt.bestbowl.utils.email;

import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringNotEmpty;

/**
 * Controller class for sending emails.
 *
 * @author Matija Kopschek
 */
// @Service
public class MailSenderService {
    private final MailSenderUtil sender = new MailSenderUtil();
    private BowlingCenter bowlingCenter;

    /**
     * Uses the emailUtils sendInvoice-method to send an email. Sets all the
     * important email
     * information. The email is sent to the client with an attachment of the
     * receipt. Can be used in other classes to
     * automatically send emails.
     *
     * @param booking
     */
    public void sendInvoiceMail(BowlingAlleyBooking booking) {
        if (!getConnectionParameters()) {
            return;
        }

        String senderMail = bowlingCenter.getSenderEmail(); // The E-Mail of the transmitter
        String password = bowlingCenter.getPassword(); // specific password created for this program
        String smtphost = bowlingCenter.getSmtpHost(); // smtp.gmail.com: host is gmail.com
        String smtpport = bowlingCenter.getSmtpPort(); // 465: is the port number for SSL, if TLS is being used then 587

        // String recepientMail = booking.getClient().getEmail(); // From class Customer
        String recepientMail = bowlingCenter.getReceiverEmail(); // Nur zu test Zwecken
        String recepientLastName = booking.getClient().getLastName(); // From class Customer
        String recepientFirstName = booking.getClient().getFirstName(); // From class Customer
        int invoiceNumber = booking.getId(); // BookingID as invoiceNumber

        sender.login(smtphost, smtpport, senderMail, password);
        String subject = "Ihre Best Bowl Rechnung";
        String mailText = "Sehr geehrter " + recepientFirstName + " " + recepientLastName + ",\n"
                + "\nDanke für Ihren Einkauf!\nIhre Rechnungsnummer ist:" + invoiceNumber;

        try {
            sender.sendAttachmentMail(senderMail, "Best Bowl", recepientMail, subject, mailText, booking);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * * Uses the emailUtils sendMessageOnlyMail-method to send an email. It
     * sets all the important email
     * information. The email is sent to the client. Can be used in other classes to
     * automatically send emails.
     *
     * @param booking
     */
    public void sendBookingConfirmationMail(BowlingAlleyBooking booking) {
        if (!getConnectionParameters()) {
            return;
        }

        String senderMail = bowlingCenter.getSenderEmail(); // The E-Mail of the transmitter
        String password = bowlingCenter.getPassword(); // specific password created for this program
        String smtphost = bowlingCenter.getSmtpHost(); // smtp.gmail.com: host is gmail.com
        String smtpport = bowlingCenter.getSmtpPort(); // 465: is the port number for SSL, if TLS is being used then 587

        // String recepientMail = booking.getClient().getEmail(); // From class Customer
        String recepientMail = bowlingCenter.getReceiverEmail(); // Nur zu test Zwecken
        String recepientLastName = booking.getClient().getLastName(); // From class Customer
        String recepientFirstName = booking.getClient().getFirstName(); // From class Customer
        int bookingNumber = booking.getId(); // BookingID as bookingNumber

        sender.login(smtphost, smtpport, senderMail, password);
        String subject = "Ihre Best Bowl Buchungsbestätigung";
        String mailText = "Sehr geehrter " + recepientFirstName + " " + recepientLastName + ",\n"
                + "\nIhre Buchung wurde bei uns vermerkt!\nIhre Rechnungsnummer ist:" + bookingNumber + "."
                + "\n\nVielen Dank für ihren Einkauf\nIhr Best Bowl-Team";

        try {
            sender.sendMessageOnlyMail(senderMail, "Best Bowl", recepientMail, subject, mailText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * * Uses the emailUtils sendMessageOnlyMail-method to send an email. It
     * sets all the important email
     * information. The email is sent to the client. Can be used in other classes to
     * automatically send emails.
     *
     * @param booking
     */
    public void sendBookingCancelationMail(BowlingAlleyBooking booking) {
        if (!getConnectionParameters()) {
            return;
        }

        String senderMail = bowlingCenter.getSenderEmail(); // The E-Mail of the transmitter
        String password = bowlingCenter.getPassword(); // specific password created for this program
        String smtphost = bowlingCenter.getSmtpHost(); // smtp.gmail.com: host is gmail.com
        String smtpport = bowlingCenter.getSmtpPort(); // 465: is the port number for SSL, if TLS is being used then 587

        // String recepientMail = booking.getClient().getEmail(); // From class Customer
        String recepientMail = bowlingCenter.getReceiverEmail(); // Nur zu test Zwecken
        String recepientLastName = booking.getClient().getLastName(); // From class Customer
        String recepientFirstName = booking.getClient().getFirstName(); // From class Customer
        int bookingNumber = booking.getId(); // BookingID as bookingNumber

        sender.login(smtphost, smtpport, senderMail, password);
        String subject = "Ihre Best Bowl Buchungsbestätigung";
        String mailText = "Sehr geehrter " + recepientFirstName + " " + recepientLastName + ",\n"
                + "\nSchade das sie ihre Buchung mit der Rechnungsnummer: " + bookingNumber + " storniert haben."
                + "\n\nWir hoffen sie bald bei uns wieder zu sehen\nIhr Best Bowl-Team";

        try {
            sender.sendMessageOnlyMail(senderMail, "Best Bowl", recepientMail, subject, mailText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * reads the parameters from the db and checks them
     *
     * @return if the parameters are present
     */
    private boolean getConnectionParameters() {
        bowlingCenter = Repos.getBowlingCenterRepository().getBowlingCenter();
        if (bowlingCenter == null) {
            return false;
        }
        return (isStringNotEmpty(bowlingCenter.getSenderEmail(), bowlingCenter.getPassword(),
                bowlingCenter.getSmtpHost(), bowlingCenter.getSmtpPort()));
    }
}