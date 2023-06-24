package de.softwareprojekt.bestbowl.utils.validators.article;

import static de.softwareprojekt.bestbowl.utils.validators.PatternValidator.isStringWithoutSpaces;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;

/**
 * The DrinkVariantValidator class validates the attributes of a DrinkVariant
 * object.
 * 
 * @author Max
 */
public class DrinkVariantValidator implements Validator<DrinkVariant> {

    /**
     * The apply function is called when the user tries to save a drink variant.
     * It checks if the amount of ml and price are valid.
     * 
     * @param drinkVariant
     * @param context
     *
     * @return A validationresult
     */
    @Override
    public ValidationResult apply(DrinkVariant drinkVariant, ValueContext context) {
        if (drinkVariant.getDrink() == null) {
            return ValidationResult.error("Getränk muss ausgewählt sein!");
        }
        if (drinkVariant.getDrink() != null) {
            if (isStringWithoutSpaces(drinkVariant.getDrink().getName()))
                return ValidationResult.error("Getränk darf nicht leer sein!");
        }
        if (drinkVariant.getMl() <= 0) {
            return ValidationResult.error("Menge der Variante muss größer 0 sein!");
        }
        if (drinkVariant.getMl() > drinkVariant.getDrink().getStockInMilliliters()) {
            return ValidationResult.error("Menge der Variante ist größer als die verfügbare Menge des Getränks!");
        }
        if (drinkVariant.getPrice() < 0) {
            return ValidationResult.error("Preis muss positiv sein!");
        }
        return ValidationResult.ok();
    }
}