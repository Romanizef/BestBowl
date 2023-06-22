package de.softwareprojekt.bestbowl.utils.validators.client;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.client.Association;
import de.softwareprojekt.bestbowl.utils.validators.PatternValidator;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;

/**
 * @author Matija
 */
public class AssociationValidator extends PatternValidator implements Validator<Association> {

    /**
     * The apply function is used to validate the input of a user.
     * 
     * @param association Access the properties of the association object
     * @param context     Get the current value of a field
     *
     * @return A validationresult
     */
    @Override
    public ValidationResult apply(Association association, ValueContext context) {
        if (!isStringMinNChars(association.getName(), 2)) {
            return ValidationResult.error("Vereinsname muss länger als 2 Buchstaben sein!");
        }
        if (!isStringOnlyLettersAndSpecialChars(association.getName())) {
            return ValidationResult.error("Nur Buchstaben und die Zeichen / . - im Namen erlaubt!");
        }
        if (association.getDiscount() < 0) {
            return ValidationResult.error("Rabatt muss positiv sein!");
        }
        if (association.getDiscount() > 100) {
            return ValidationResult.error("Rabatt kann nicht über 100% sein!");
        }
        return ValidationResult.ok();
    }
}