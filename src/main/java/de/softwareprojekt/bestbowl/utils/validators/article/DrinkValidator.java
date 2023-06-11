package de.softwareprojekt.bestbowl.utils.validators.article;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;

/**
 * The DrinkValidator class validates the attributes of a Drink object.
 * 
 * @author Max
 */
public class DrinkValidator implements Validator<Drink> {

    /**
     * The apply function is called by the binder to validate a drink.
     * It checks if the drink has a name with at least 2 characters, if the is
     * enough stock of the drink and if the reorder point of the drink is positive.
     * 
     * @param drink
     * @param context
     *
     * @return A validationresult
     */

    @Override
    public ValidationResult apply(Drink drink, ValueContext context) {
        if (!isStringMinNChars(drink.getName(), 2)) {
            return ValidationResult.error("Name muss mindestens 2 Zeichen lang sein");
        }
        if (drink.getStockInMilliliters() < 0) {
            return ValidationResult.error("Bestand muss positiv sein");
        }
        if (drink.getReorderPoint() < -1) {
            return ValidationResult.error("Meldebestand muss positiv sein oder -1 für keine Meldung");
        }
        return ValidationResult.ok();
    }
}