package de.softwareprojekt.bestbowl.utils.validators.article;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoe;

/**
 * The BowlingShoeValidatorclass validates the attributes of a BowlingShoe
 * object.
 * 
 * @author Max
 */
public class BowlingShoeValidator implements Validator<BowlingShoe> {

    /**
     * The apply function is used to validate the input of a BowlingShoe object.
     * It checks if the size of the shoe is between 20 and 70. If not, it returns an
     * error message.
     * 
     * @param bowlingShoe
     * @param context
     * 
     * @return Validationresult
     */
    @Override
    public ValidationResult apply(BowlingShoe bowlingShoe, ValueContext context) {
        if (bowlingShoe.getSize() < 30 || bowlingShoe.getSize() > 50) {
            return ValidationResult.error("Größe muss zwischen 30 und 50 sein");
        }
        return ValidationResult.ok();
    }
}