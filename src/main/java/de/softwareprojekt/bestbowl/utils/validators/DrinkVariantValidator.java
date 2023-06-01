package de.softwareprojekt.bestbowl.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;

public class DrinkVariantValidator implements Validator<DrinkVariant> {
    @Override
    public ValidationResult apply(DrinkVariant drinkVariant, ValueContext context) {
        if(drinkVariant.getMl() <= 0) {
            return ValidationResult.error("Menge muss größer 0 sein");
        }
        if(drinkVariant.getPrice() < 0) {
            return ValidationResult.error("Preis muss positiv sein");
        }
        return ValidationResult.ok();
    }
}
