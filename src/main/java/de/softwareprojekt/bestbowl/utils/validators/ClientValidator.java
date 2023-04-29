package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import de.softwareprojekt.bestbowl.jpa.entities.Client;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;
import static de.softwareprojekt.bestbowl.utils.Utils.isStringValidEmail;

public class ClientValidator implements Validator<Client> {
    @Override
    public ValidationResult apply(Client client, ValueContext context) {
        if (!isStringMinNChars(client.getFirstName(), 2)) {
            return ValidationResult.error("Vorname muss min. 2 Zeichen lang sein");
        }
        if (!isStringMinNChars(client.getLastName(), 2)) {
            return ValidationResult.error("Nachname muss min. 2 Zeichen lang sein");
        }
        if (!isStringValidEmail(client.getEmail())) {
            return ValidationResult.error("Ungültige E-Mail");
        }
        if (client.getAddress() == null) {
            return ValidationResult.error("Adresse ist null");
        }
        if (!isStringMinNChars(client.getAddress().getStreet(), 3)) {
            return ValidationResult.error("Straße muss min. 3 Zeichen lang sein");
        }
        if (client.getAddress().getHouseNr() <= 0) {
            return ValidationResult.error("Hausnummer muss größer als 0 sein");
        }
        if (client.getAddress().getPostCode() <= 0) {
            return ValidationResult.error("PLZ muss größer als 0 sein");
        }
        if (!isStringMinNChars(client.getAddress().getCity(), 3)) {
            return ValidationResult.error("Stadt muss min. 3 Zeichen lang sein");
        }
        return ValidationResult.ok();
    }
}
