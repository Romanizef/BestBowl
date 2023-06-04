package de.softwareprojekt.bestbowl.utils.validators.client;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.client.Association;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;

/**
 * @author Matija
 */
public class AssociationValidator implements Validator<Association> {

    @Override
    public ValidationResult apply(Association association, ValueContext context) {
        if (!isStringMinNChars(association.getName(), 2)) {
            return ValidationResult.error("Vereinsname muss länger als 2 Zeichen sein");
        }
        if (association.getDiscount() < 0) {
            return ValidationResult.error("Rabatt muss positiv sein");
        }
        return ValidationResult.ok();
    }
}
