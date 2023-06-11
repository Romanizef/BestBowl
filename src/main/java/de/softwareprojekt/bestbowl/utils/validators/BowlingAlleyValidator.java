package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;

/**
 * @author Matija
 */
public class BowlingAlleyValidator implements Validator<BowlingAlley> {

    /**
     * The apply function is used to validate the input of a BowlingAlley object.
     * It checks if the ID is greater than 0, which it should be.
     * 
     * @param alley   Access the value of the field that is being validated
     * @param context Get the current value of a field
     *
     * @return A validationresult
     */
    @Override
    public ValidationResult apply(BowlingAlley alley, ValueContext context) {
        if (alley.getId() <= 0) {
            return ValidationResult.error("ID muss größer als 0 sein");
        }
        return ValidationResult.ok();
    }
}