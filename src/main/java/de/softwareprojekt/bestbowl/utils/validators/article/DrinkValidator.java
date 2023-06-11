package de.softwareprojekt.bestbowl.utils.validators.article;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;

/**
 * @author Max
 */
public class DrinkValidator implements Validator<Drink> {
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
