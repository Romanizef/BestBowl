package de.softwareprojekt.bestbowl.utils.email;

import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringNotEmpty;

/**
 * MailSenderService is a class that is used to send emails.
 *
 * @author Matija Kopschek
 */
public class MailSenderService {
    private static final String DISCLAIMER = "\n\n\nDISCLAIMER: This e-mail is from an educational project and has no real-world meaning";
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

        String recepientMail = bowlingCenter.getReceiverEmail();
        if (!isStringNotEmpty(recepientMail)) {
            recepientMail = booking.getClient().getEmail();
        }
        String recepientLastName = booking.getClient().getLastName(); // From class Customer
        String recepientFirstName = booking.getClient().getFirstName(); // From class Customer
        int invoiceNumber = booking.getId(); // BookingID as invoiceNumber

        sender.login(smtphost, smtpport, senderMail, password);
        String subject = "Ihre " + bowlingCenter.getDisplayName() + " Rechnung";
        String mailText = "Sehr geehrte(r) " + recepientFirstName + " " + recepientLastName + ",\n"
                + "\nDanke für Ihren Einkauf!\nIhre Rechnungsnummer ist: " + invoiceNumber
                + DISCLAIMER;

        try {
            sender.sendAttachmentMail(senderMail, bowlingCenter.getDisplayName(), recepientMail, subject, mailText, booking);
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

        String recepientMail = bowlingCenter.getReceiverEmail();
        if (!isStringNotEmpty(recepientMail)) {
            recepientMail = booking.getClient().getEmail();
        }
        String recepientLastName = booking.getClient().getLastName(); // From class Customer
        String recepientFirstName = booking.getClient().getFirstName(); // From class Customer
        int bookingNumber = booking.getId(); // BookingID as bookingNumber

        sender.login(smtphost, smtpport, senderMail, password);
        String subject = "Ihre " + bowlingCenter.getDisplayName() + " Buchungsbestätigung";
        String mailText = "Sehr geehrte(r) " + recepientFirstName + " " + recepientLastName + ",\n"
                + "\nIhre Buchung wurde bei uns vermerkt!\nIhre Rechnungsnummer ist: " + bookingNumber + "."
                + "\n\nVielen Dank für ihren Einkauf\nIhr " + bowlingCenter.getDisplayName() + "-Team"
                + DISCLAIMER;

        try {
            sender.sendMessageOnlyMail(senderMail, bowlingCenter.getDisplayName(), recepientMail, subject, mailText);
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

        String recepientMail = bowlingCenter.getReceiverEmail();
        if (!isStringNotEmpty(recepientMail)) {
            recepientMail = booking.getClient().getEmail();
        }
        String recepientLastName = booking.getClient().getLastName(); // From class Customer
        String recepientFirstName = booking.getClient().getFirstName(); // From class Customer
        int bookingNumber = booking.getId(); // BookingID as bookingNumber

        sender.login(smtphost, smtpport, senderMail, password);
        String subject = "Ihre " + bowlingCenter.getDisplayName() + " Stornierung";
        String mailText = "Sehr geehrte(r) " + recepientFirstName + " " + recepientLastName + ",\n"
                + "\nSchade das sie ihre Buchung mit der Rechnungsnummer: " + bookingNumber + " storniert haben."
                + "\n\nWir hoffen sie bald bei uns wieder zu sehen\nIhr " + bowlingCenter.getDisplayName() + "-Team"
                + DISCLAIMER;

        try {
            sender.sendMessageOnlyMail(senderMail, bowlingCenter.getDisplayName(), recepientMail, subject, mailText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The getConnectionParameters function is used to retrieve the connection
     * parameters from the database.
     *
     * @return A boolean value
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