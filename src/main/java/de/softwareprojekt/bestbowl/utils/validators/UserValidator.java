package de.softwareprojekt.bestbowl.utils.validators;

import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.isStringOnlyLetters;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.isStringValidEmail;
import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.isStringWithoutDoubleSpaces;
import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;
import static de.softwareprojekt.bestbowl.utils.Utils.isStringNotEmpty;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.User;

/**
 * @author Marten Voß
 */
public class UserValidator implements Validator<User> {

    /**
     * The apply function is used to validate the user input.
     * 
     * @param user    Access the fields of the user object
     * @param context Get the current locale
     *
     * @return A validationresult
     */
    @Override
    public ValidationResult apply(User user, ValueContext context) {
        if (!isStringMinNChars(user.getName(), 3)) {
            return ValidationResult.error("Name muss min. 3 Zeichen lang sein!");
        }
        if (!isStringValidEmail(user.getEmail())) {
            return ValidationResult.error("Ungültige E-Mail!");
        }
        if (!isStringNotEmpty(user.getEncodedPassword())) {
            return ValidationResult.error("Passwort darf nicht leer sein!");
        }
        if (!isStringMinNChars(user.getSecurityQuestion(), 3)) {
            return ValidationResult.error("Sicherheitsfrage muss min. 3 Zeichen lang sein!");
        }
        if (!isStringMinNChars(user.getSecurityQuestionAnswer(), 1)) {
            return ValidationResult.error("Sicherheitsantwort muss min. 1 Zeichen lang sein!");
        }
        if (!isStringNotEmpty(user.getRole())) {
            return ValidationResult.error("Der Benutzer muss einer Rolle zugeordnet sein!");
        }
        if (!isStringOnlyLetters(user.getName())) {
            return ValidationResult.error("Nur Buchstaben sind im Namen erlaubt!");
        }
        if (!isStringWithoutDoubleSpaces(user.getSecurityQuestion())
                || !isStringWithoutDoubleSpaces(user.getSecurityQuestionAnswer())) {
            return ValidationResult.error("Sicherheitsfrage und Antwort dürfen nicht leer sein!");
        }
        return ValidationResult.ok();
    }
}