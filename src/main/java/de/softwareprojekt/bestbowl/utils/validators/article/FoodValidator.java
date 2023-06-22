package de.softwareprojekt.bestbowl.utils.validators.article;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;
import static de.softwareprojekt.bestbowl.utils.Utils.isStringOnlyLetters;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.food.Food;

/**
 * The FoodValidator class validates the attributes of a Food
 * object.
 * 
 * @author Max
 */
public class FoodValidator implements Validator<Food> {

    /**
     * The apply function is used to validate the Food object.
     * It checks if the name is at least 2 characters long, if the price is above 0,
     * if the stock is above 0 and if the reorder point is not negative.
     *
     * @param food
     * @param context
     *
     * @return A validationresult
     */
    @Override
    public ValidationResult apply(Food food, ValueContext context) {
        if (!isStringMinNChars(food.getName(), 2)) {
            return ValidationResult.error("Name muss mindestens 2 Zeichen lang sein");
        }
        if (food.getPrice() < 0) {
            return ValidationResult.error("Preis muss positiv sein");
        }
        if (food.getStock() < 0) {
            return ValidationResult.error("Bestand muss positiv sein");
        }
        if (food.getReorderPoint() < -1) {
            return ValidationResult.error("Meldebestand muss positiv sein oder -1 für keine Meldung");
        }
        if (!isStringOnlyLetters(food.getName())) {
            return ValidationResult.error("Im Namen sind nur Buchstaben erlaubt!");
        }
        return ValidationResult.ok();
    }
}