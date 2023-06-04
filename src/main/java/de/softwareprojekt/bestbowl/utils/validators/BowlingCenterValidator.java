package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;
import static de.softwareprojekt.bestbowl.utils.Utils.isStringValidEmail;

public class BowlingCenterValidator implements Validator<BowlingCenter> {

    @Override
    public ValidationResult apply(BowlingCenter bowlingCenter, ValueContext context) {
        if (!isStringMinNChars(bowlingCenter.getDisplayName(), 2)) {
            return ValidationResult.error("Anzeigename muss mindestens 2 Zeichen lang sein");
        }
        if (!isStringMinNChars(bowlingCenter.getBusinessName(), 2)) {
            return ValidationResult.error("Geschäftsname muss mindestens 2 Zeichen lang sein");
        }
        if (!isStringMinNChars(bowlingCenter.getStreet(), 2)) {
            return ValidationResult.error("Straße muss mindestens 2 Zeichen lang sein");
        }
        if (bowlingCenter.getHouseNr() <= 0) {
            return ValidationResult.error("Hausnummer muss größer als 0 sein");
        }
        if (bowlingCenter.getPostCode() <= 1000 || bowlingCenter.getPostCode() > 99_999) {
            return ValidationResult.error("PLZ muss zwischen 1001 und 99999 sein");
        }
        if (!isStringMinNChars(bowlingCenter.getCity(), 2)) {
            return ValidationResult.error("Stadt muss mindestens 2 Zeichen lang sein");
        }
        if (bowlingCenter.getBowlingAlleyPricePerHour() < 0) {
            return ValidationResult.error("Der Bahn Preis pro Stunde muss positiv sein");
        }
        if (bowlingCenter.getBowlingShoePrice() < 0) {
            return ValidationResult.error("Der Bowling Schuh Preis muss positiv sein");
        }
        if (bowlingCenter.getSenderEmail() != null && bowlingCenter.getSenderEmail().length() > 0 && !isStringValidEmail(bowlingCenter.getSenderEmail())) {
            return ValidationResult.error("Ungültige E-Mail");
        }
        if (bowlingCenter.getReceiverEmail() != null && bowlingCenter.getReceiverEmail().length() > 0 && !isStringValidEmail(bowlingCenter.getReceiverEmail())) {
            return ValidationResult.error("Ungültige E-Mail");
        }
        return ValidationResult.ok();
    }
}
