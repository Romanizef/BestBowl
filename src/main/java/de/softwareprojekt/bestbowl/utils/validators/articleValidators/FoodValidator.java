package de.softwareprojekt.bestbowl.utils.validators.articleValidators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import de.softwareprojekt.bestbowl.jpa.entities.foodEntities.Food;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringMinNChars;

public class FoodValidator implements Validator<Food> {
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
        return ValidationResult.ok();
    }
}
