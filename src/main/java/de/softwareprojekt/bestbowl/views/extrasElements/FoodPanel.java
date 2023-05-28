package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;

import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class FoodPanel extends HorizontalLayout {
    private IntegerField foodAmountField;
    private Label foodLabel;

    public FoodPanel(Food food) {
        foodLabel = new Label(food.getName());
        foodLabel.setMinWidth("250px");
        foodLabel.setMaxWidth("250px");

        foodAmountField = new IntegerField();
        foodAmountField.setValue(0);
        foodAmountField.setStepButtonsVisible(true);
        foodAmountField.setMin(0);
        foodAmountField.setMax(food.getStock());

        addCSS();
        setAlignItems(Alignment.CENTER);
        setFlexGrow(0);
        add(foodLabel, foodAmountField);
    }

    public FoodPanel(FoodBooking foodBooking) {
        Label label = new Label(foodBooking.getName());
        label.setMinWidth("250px");
        label.setMaxWidth("250px");

        IntegerField foodAmountField = new IntegerField();
        foodAmountField.setValue(foodBooking.getAmount());
        foodAmountField.setStepButtonsVisible(true);
        foodAmountField.setMin(0);
        foodAmountField.setMax(foodBooking.getAmount());

        setWidthFull();
        addCSS();
        setAlignItems(Alignment.CENTER);
        add(label, foodAmountField);
    }

    public IntegerField getFoodAmountField() {
        return foodAmountField;
    }
    public void setFoodAmountField(IntegerField foodAmountField) {
        this.foodAmountField = foodAmountField;
    }

    public Label getFoodLabel() {
        return foodLabel;
    }
    public void setFoodLabel(Label foodLabel) {
        this.foodLabel = foodLabel;
    }

    public IntegerField createIntegerField(Food food, FoodBooking foodBooking) {
        IntegerField sizeField = new IntegerField();
        sizeField.setValue(0);
        sizeField.setStepButtonsVisible(true);
        sizeField.setMin(0);
        sizeField.setMax(food.getStock()/ foodBooking.getAmount());

        sizeField.addValueChangeListener(e -> {
            // TODO
        });
        return sizeField;
    }

     private void addCSS() {
        getStyle().set("border", "2px solid #b3f542").set("background-color", "#b3f54210").set("padding", "10px")
                .set("margin-bottom", "10px").set("border-radius", "50px");
    }

    public void resetFoodAmountFieldValue() {
        this.foodAmountField.setValue(0);
    }
}
