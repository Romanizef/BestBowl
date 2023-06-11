package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;

/**
 * @author Matija
 */
public class BowlingAlleyValidator implements Validator<BowlingAlley> {
    @Override
    public ValidationResult apply(BowlingAlley alley, ValueContext context) {
        if (alley.getId() <= 0) {
            return ValidationResult.error("ID muss größer als 0 sein");
        }
        return ValidationResult.ok();
    }
}
