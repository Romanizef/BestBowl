package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;

import java.util.Map;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.PANEL_COLOR_FOOD;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class FoodPanel extends HorizontalLayout {
    private final IntegerField foodAmountField;

    public FoodPanel(Food food, BowlingAlleyBooking bowlingAlleyBooking, Map<String, FoodBooking> foodBookingMap) {
        Label label = new Label(food.getName());
        label.setMinWidth("250px");
        label.setMaxWidth("250px");
        label.getStyle().set("text-align", "left");

        foodAmountField = new IntegerField();
        foodAmountField.setValue(0);
        foodAmountField.setStepButtonsVisible(true);
        foodAmountField.setMin(0);
        foodAmountField.setMax(food.getStock());

        foodAmountField.addValueChangeListener(e -> {
            FoodBooking temp = new FoodBooking(food, bowlingAlleyBooking);
            FoodBooking foodBooking = foodBookingMap.getOrDefault(temp.getName(), temp);
            foodBooking.setAmount(e.getValue());
            foodBookingMap.put(foodBooking.getName(), foodBooking);
        });

        addCSS();
        setAlignItems(Alignment.CENTER);
        setFlexGrow(0);
        add(label, foodAmountField);
    }

    private void addCSS() {
        getStyle()
                .set("border", "2px solid " + PANEL_COLOR_FOOD)
                .set("border-radius", "50px")
                .set("background-color", PANEL_COLOR_FOOD + "10")
                .set("padding", "10px")
                .set("padding-left", "15px")
                .set("padding-right", "20px");
    }

    public void resetFoodAmountFieldValue() {
        this.foodAmountField.setValue(0);
    }
}
