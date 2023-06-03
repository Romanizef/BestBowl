package de.softwareprojekt.bestbowl.utils.validators.articleValidators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoeEntities.BowlingShoe;

public class BowlingShoeValidator implements Validator<BowlingShoe> {
    @Override
    public ValidationResult apply(BowlingShoe bowlingShoe, ValueContext context) {
        if (bowlingShoe.getSize() < 20 || bowlingShoe.getSize() > 70) {
            return ValidationResult.error("Größe muss zwischen 20 und 70 sein");
        }
        return ValidationResult.ok();
    }
}
