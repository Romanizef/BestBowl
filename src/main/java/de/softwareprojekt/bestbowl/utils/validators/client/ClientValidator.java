package de.softwareprojekt.bestbowl.utils.validators.client;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.*;

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
        if (client.getAddress().getHouseNr().isEmpty()) {
            return ValidationResult.error("Hausnummer muss größer als 0 sein!");
        }
        if (!isStringValidHouseNumber(client.getAddress().getHouseNr())) {
            return ValidationResult.error("Hausnummer im falschen Format. Z.B.: 10a");
        }
        if (!isStringValidPostalcode(createStringPostalcodeForClient(client))) {
            return ValidationResult.error("PLZ muss zwischen 01000 und 99999 sein!");
        }
        if(!isStringValidIban(client.getIban())) {
            return ValidationResult.error("Keine IBAN! Bitte nur bis zu 30 Zahlen oder Großbuchstaben hinzufügen.");
        }
        if (!isStringMinNChars(client.getAddress().getCity(), 3)) {
            return ValidationResult.error("Stadt muss min. 3 Zeichen lang sein!");
        }
        if (!isStringOnlyLetters(client.getFirstName())) {
            if (isStringWithoutDoubleSpaces(client.getFirstName()))
                return ValidationResult
                        .error("Im Vornamen sind nur Buchstaben erlaubt! Vorname darf auch nicht leer sein!");
        }
        if (!isStringOnlyLetters(client.getLastName())) {
            if (isStringWithoutDoubleSpaces(client.getLastName()))
                return ValidationResult
                        .error("Im Nachnamen sind nur Buchstaben erlaubt! Nachname darf auch nicht leer sein!");
        }
        if (!isStringOnlyLetters(client.getAddress().getCity())) {
            if (isStringWithoutDoubleSpaces(client.getAddress().getCity()))
                return ValidationResult
                        .error("Im Stadtnamen sind nur Buchstaben erlaubt! Stadtname darf auch nicht leer sein!");
        }
        if (!isStringOnlyLetters(client.getAddress().getStreet())) {
            if (isStringWithoutDoubleSpaces(client.getAddress().getStreet()))
                return ValidationResult
                        .error("Im Straßennamen sind nur Buchstaben erlaubt! Straßenname darf auch nicht leer sein!");
        }
        return ValidationResult.ok();
    }
}