package de.softwareprojekt.bestbowl.email;

//Author: Matija Kopschek
public class MailSenderTester {
	public static void main(String[] args) {

		String senderMail = "bestbowl11@gmail.com"; // The E-Mail of the transmitter
		String password = "cekikwtbwzgrdcfn"; // specific password created for this program
		String smtphost = "smtp.gmail.com"; // smtp.gmail.com: host is gmail.com
		String smtpport = "465"; // 465: is the port number for SSL, if TLS is being used then 587

		String recepientMail = "bestbowl11@gmail.com"; // From class Customer
		String recepientlastname = "Mustermann"; // From class Customer
		String recepientname = "Max"; // From class Customer
		String invoiceNumber = "12345";

		MailSender sender = new MailSender();
		sender.login(smtphost, smtpport, senderMail, password);

		try {
			sender.send(senderMail, "Best Bowl", recepientMail, "Ihre Rechnung",
					"Sehr geehrter " + recepientname + " " +  recepientlastname + ",\n"
							+ "\nDanke für Ihren Einkauf!\nIhre Rechnungsnummer ist:" + invoiceNumber
							+ "\n\nUnser Team wünscht Ihnen einen schönen Tag!\nBest Bowl");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
