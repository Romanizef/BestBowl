package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.User;

import static de.softwareprojekt.bestbowl.utils.Utils.*;

/**
 * @author Marten Voß
 */
public class UserValidator implements Validator<User> {
    @Override
    public ValidationResult apply(User user, ValueContext context) {
        if (!isStringMinNChars(user.getName(), 3)) {
            return ValidationResult.error("Name muss min. 3 Zeichen lang sein");
        }
        if (!isStringValidEmail(user.getEmail())) {
            return ValidationResult.error("Ungültige E-Mail");
        }
        if (!isStringNotEmpty(user.getEncodedPassword())) {
            return ValidationResult.error("Passwort darf nicht leer sein");
        }
        if (!isStringMinNChars(user.getSecurityQuestion(), 3)) {
            return ValidationResult.error("Sicherheitsfrage muss min. 3 Zeichen lang sein");
        }
        if (!isStringMinNChars(user.getSecurityQuestionAnswer(), 1)) {
            return ValidationResult.error("Sicherheitsantwort muss min. 1 Zeichen lang sein");
        }
        if (!isStringNotEmpty(user.getRole())) {
            return ValidationResult.error("Der Benutzer muss einer Rolle zugeordnet sein");
        }
        return ValidationResult.ok();
    }
}
