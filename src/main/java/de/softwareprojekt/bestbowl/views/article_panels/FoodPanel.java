package de.softwareprojekt.bestbowl.views.article_panels;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.food.Food;
import de.softwareprojekt.bestbowl.jpa.entities.food.FoodBooking;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;

import java.util.Map;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.PANEL_COLOR_FOOD;

/**
 * Class for the Food Panel in the ExtrasView.
 *
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class FoodPanel extends HorizontalLayout {
    private final IntegerField foodAmountField;

    /**
     * The FoodPanel function is a constructor for the FoodPanel class.
     * It creates a new FoodPanel object, which consists of two components:
     * A label and an integer field. The label displays the name of the food item,
     * while
     * the integer field allows users to specify how many portions they want to
     * order.
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
                Notifications.showError("Nicht genügend von der Speise: " + food.getName());
                return;
            }

            FoodBooking temp = new FoodBooking(food, bowlingAlleyBooking);
            FoodBooking foodBooking = foodBookingMap.getOrDefault(temp.getName(), temp);
            foodBooking.setAmount(value);
            if (foodBooking.getAmount() == 0) {
                foodBookingMap.remove(foodBooking.getName());
            } else {
                foodBookingMap.put(foodBooking.getName(), foodBooking);
            }
        });

        addCSS();
        setAlignItems(Alignment.CENTER);
        setFlexGrow(0);
        add(label, foodAmountField);
    }

    public IntegerField getFoodAmountField() {
        return foodAmountField;
    }

    /**
     * The addCSS function adds CSS styling to the FoodPanel.
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
     * The resetFoodAmountFieldValue function resets the value of the
     * foodAmountField to 0.
     */
    public void resetFoodAmountFieldValue() {
        this.foodAmountField.setValue(0);
    }
}