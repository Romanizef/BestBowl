package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.*;

/**
 * @author Matija
 */
public class BowlingCenterValidator implements Validator<BowlingCenter> {

    /**
     * The apply function is used to validate the input of a BowlingCenter object.
     * It checks if all fields are valid and returns an error message if not.
     *
     * @param bowlingCenter Access the values of the fields in the form
     * @param context       Get the current value of the field
     * @return A validationresult
     */
    @Override
    public ValidationResult apply(BowlingCenter bowlingCenter, ValueContext context) {
        if (!isStringMinNChars(bowlingCenter.getDisplayName(), 2)) {
            return ValidationResult.error("Anzeigename muss mindestens 2 Zeichen lang sein!");
        }
        if (!isStringOnlyLettersAndSpecialChars(bowlingCenter.getDisplayName())) {
            if (isStringWithoutDoubleSpaces(bowlingCenter.getDisplayName()))
                return ValidationResult
                        .error("In der Anzeigename sind nur Buchstaben erlaubt! Anzeigename darf nicht leer sein!");
        }
        if (!isStringMinNChars(bowlingCenter.getBusinessName(), 2)) {
            return ValidationResult.error("Geschäftsname muss mindestens 2 Zeichen lang sein!");
        }
        if (!isStringOnlyLettersAndSpecialChars(bowlingCenter.getBusinessName())) {
            if (isStringWithoutDoubleSpaces(bowlingCenter.getBusinessName()))
                return ValidationResult
                        .error("In der Geschäftsnamen sind nur Buchstaben erlaubt! Anzeigename darf nicht leer sein!");
        }
        if (!isStringMinNChars(bowlingCenter.getStreet(), 2)) {
            return ValidationResult.error("Straße muss mindestens 2 Zeichen lang sein!");
        }
        if (!isStringOnlyLetters(bowlingCenter.getStreet())) {
            if (isStringWithoutDoubleSpaces(bowlingCenter.getStreet()))
                return ValidationResult.error("Im Straßennamen sind nur Buchstaben erlaubt!");
        }
        if (bowlingCenter.getHouseNr() == null) {
            return ValidationResult.error("Hausnummer muss größer als 0 sein!");
        }
        if (!isStringValidHouseNumber(bowlingCenter.getHouseNr())) {
            return ValidationResult.error("Hausnummer nicht im richtigen Format. Z. B.: 10a!");
        }
        if (!isStringValidPostalcode(createStringPostalcodeForBowlingcenter(bowlingCenter))) {
            return ValidationResult.error("PLZ muss zwischen 01000 und 99999 sein!");
        }
        if (!isStringMinNChars(bowlingCenter.getCity(), 2)) {
            return ValidationResult.error("Stadt muss mindestens 2 Zeichen lang sein!");
        }
        if (!isStringOnlyLetters(bowlingCenter.getCity())) {
            if (isStringWithoutDoubleSpaces(bowlingCenter.getCity()))
                return ValidationResult.error("Im Stadtnamen sind nur Buchstaben erlaubt!");
        }
        if (bowlingCenter.getBowlingAlleyPricePerHour() < 0) {
            return ValidationResult.error("Der Bahn Preis pro Stunde muss positiv sein!");
        }
        if (bowlingCenter.getBowlingShoePrice() < 0) {
            return ValidationResult.error("Der Bowling Schuh Preis muss positiv sein!");
        }
        if (bowlingCenter.getMinShoeSize() < 1) {
            return ValidationResult.error("Die minimale Schuhgröße muss größer 0 sein!");
        }
        if (bowlingCenter.getMaxShoeSize() < 1) {
            return ValidationResult.error("Die maximale Schuhgröße muss größer 0 sein!");
        }
        if (bowlingCenter.getMaxShoeSize() < bowlingCenter.getMinShoeSize()) {
            return ValidationResult
                    .error("Die maximale Schuhgröße muss größer oder gleich der minimalen Schuhgröße sein!");
        }
        if (bowlingCenter.getSenderEmail() != null && bowlingCenter.getSenderEmail().length() > 0
                && !isStringValidEmail(bowlingCenter.getSenderEmail())) {
            return ValidationResult.error("Ungültige E-Mail");
        }
        if (bowlingCenter.getReceiverEmail() != null && bowlingCenter.getReceiverEmail().length() > 0
                && !isStringValidEmail(bowlingCenter.getReceiverEmail())) {
            return ValidationResult.error("Ungültige E-Mail");
        }
        if (!isStringValidSMTPHost(bowlingCenter.getSmtpHost())) {
            return ValidationResult.error("Ungültiger SMTP Host. Format: smtp.example.com");
        }
        if (!isStringValidSMTPPort(bowlingCenter.getSmtpPort())) {
            return ValidationResult.error("Ungültiger SMTP Port. Format: 123");
        }
        return ValidationResult.ok();
    }
}