package de.softwareprojekt.bestbowl.utils.validators.client;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.createStringPostalcodeForClient;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.isStringOnlyLetters;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.isStringValidEmail;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.isStringValidPostalcode;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.client.Client;

/**
 * @author Marten
 */
public class ClientValidator implements Validator<Client> {

    /**
     * The apply function is used to validate the Client object.
     * It checks if all fields are valid and returns a ValidationResult.ok() if they
     * are, or an error message otherwise.
     * 
     * @param client  Access the fields of a client object
     * @param context Get the current value of a field
     *
     * @return A validationresult
     */
    @Override
    public ValidationResult apply(Client client, ValueContext context) {
        if (!isStringMinNChars(client.getFirstName(), 2)) {
            return ValidationResult.error("Vorname muss min. 2 Zeichen lang sein!");
        }
        if (!isStringMinNChars(client.getLastName(), 2)) {
            return ValidationResult.error("Nachname muss min. 2 Zeichen lang sein!");
        }
        if (!isStringValidEmail(client.getEmail())) {
            return ValidationResult.error("Ungültige E-Mail!");
        }
        if (client.getComment().length() > 255) {
            return ValidationResult.error("Kommentar darf nicht länger als 255 Zeichen sein!");
        }
        if (client.getAddress() == null) {
            return ValidationResult.error("Keine Adresse existiert!");
        }
        if (!isStringMinNChars(client.getAddress().getStreet(), 3)) {
            return ValidationResult.error("Straße muss min. 3 Zeichen lang sein!");
        }
        if (client.getAddress().getHouseNr() <= 0) {
            return ValidationResult.error("Hausnummer muss größer als 0 sein!");
        }
        if (!isStringValidPostalcode(createStringPostalcodeForClient(client))) {
            return ValidationResult.error("PLZ muss zwischen 01000 und 99999 sein!");
        }
        if (!isStringMinNChars(client.getAddress().getCity(), 3)) {
            return ValidationResult.error("Stadt muss min. 3 Zeichen lang sein!");
        }
        if (!isStringOnlyLetters(client.getFirstName()) || !isStringOnlyLetters(client.getLastName())
                || !isStringOnlyLetters(client.getAddress().getCity())
                || !isStringOnlyLetters(client.getAddress().getStreet())) {
            return ValidationResult.error("Im Vor/-Nachnamen, in der Stadt und Straße sind nur Buchstaben erlaubt!");
        }
        return ValidationResult.ok();
    }
}