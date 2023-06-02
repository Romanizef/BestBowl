package de.softwareprojekt.bestbowl.views.extrasElements;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.PANEL_COLOR_FOOD;

import java.util.Map;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;

/**
 * Class for the Food Panel in the ExtrasView.
 * 
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class FoodPanel extends HorizontalLayout {
    private final IntegerField foodAmountField;

    /**
     * Constructor for the FoodPanel.
     * 
     * @param food
     * @param bowlingAlleyBooking
     * @param foodBookingMap
     * @see #addCSS()
     */
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
            Integer value = e.getValue();
            if (value == null) {
                value = 0;
            }
            if (value < foodAmountField.getMin() || value > foodAmountField.getMax()) {
                foodAmountField.setValue(e.getOldValue());
                return;
            }

            FoodBooking temp = new FoodBooking(food, bowlingAlleyBooking);
            FoodBooking foodBooking = foodBookingMap.getOrDefault(temp.getName(), temp);
            foodBooking.setAmount(value);
            foodBookingMap.put(foodBooking.getName(), foodBooking);
        });

        addCSS();
        setAlignItems(Alignment.CENTER);
        setFlexGrow(0);
        add(label, foodAmountField);
    }

    /**
     * Adds CSS style to the FoodPanel.
     */
    private void addCSS() {
        getStyle()
                .set("border", "2px solid " + PANEL_COLOR_FOOD)
                .set("border-radius", "50px")
                .set("background-color", PANEL_COLOR_FOOD + "10")
                .set("padding", "10px")
                .set("padding-left", "15px")
                .set("padding-right", "20px");
    }

    /**
     * Resets the FoodAmountField to the value 0.
     */
    public void resetFoodAmountFieldValue() {
        this.foodAmountField.setValue(0);
    }
}
