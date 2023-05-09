package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.Association;
import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;

/**
 * @author Matija
 */
public class AssociationValidator implements Validator<Association> {

    @Override
    public ValidationResult apply(Association association, ValueContext context) {
        if (!isStringMinNChars(association.getName(), 2)) {
            return ValidationResult.error("Vereinsname muss länger als 2 Zeichen sein!");
        }
        if (association.getDiscount() < 0.0) {
            return ValidationResult.error("Rabatt muss unter 0% sein!");
        }
        return ValidationResult.ok();
    }

}
