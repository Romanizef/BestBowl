package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class DrinkPanel extends HorizontalLayout {

    private Label label;

    private FormLayout variantLayout;

    public DrinkPanel(Drink drink, BowlingAlleyBooking bowlingAlleyBooking, Map<String, DrinkBooking> drinkBookingMap) {
        variantLayout = new FormLayout();
        variantLayout.setResponsiveSteps(new ResponsiveStep("200px", 3));

        label = new Label(drink.getName());
        label.setMinWidth("250px");
        label.setMaxWidth("250px");
        List<DrinkVariant> drinkVariantList = new ArrayList<>(drink.getDrinkVariants());
        drinkVariantList.sort(Comparator.comparingInt(DrinkVariant::getMl));

        for (DrinkVariant drinkVariant : drinkVariantList) {
            variantLayout.add(createIntegerField(drinkVariant, bowlingAlleyBooking, drinkBookingMap));
        }

        setWidthFull();
        addCSS();
        setAlignItems(Alignment.CENTER);
        add(label, variantLayout);

    }


    public DrinkPanel(DrinkBooking drinkBooking) {
        Label label = new Label(drinkBooking.getName());
        label.setMinWidth("250px");
        label.setMaxWidth("250px");

        IntegerField drinkAmountField = new IntegerField();
        drinkAmountField.setValue(drinkBooking.getAmount());
        drinkAmountField.setStepButtonsVisible(true);
        drinkAmountField.setMin(0);
        drinkAmountField.setMax(drinkBooking.getAmount());

        setWidthFull();
        addCSS();
        setAlignItems(Alignment.CENTER);
        add(label, drinkAmountField);
    }

    public IntegerField createIntegerField(DrinkVariant drinkVariant, BowlingAlleyBooking bowlingAlleyBooking, Map<String, DrinkBooking> drinkBookingMap) {
        IntegerField mlField = new IntegerField();
        mlField.setValue(0);
        mlField.setStepButtonsVisible(true);
        mlField.setMin(0);
        mlField.setSuffixComponent(new Span(drinkVariant.getMl() + "ml"));
        mlField.setMax(drinkVariant.getDrink().getStockInMilliliters() / drinkVariant.getMl());

        mlField.addValueChangeListener(e -> {
            DrinkBooking temp = new DrinkBooking(drinkVariant, bowlingAlleyBooking);
            DrinkBooking drinkBooking = drinkBookingMap.getOrDefault(temp.getName(), temp);

            drinkBooking.setAmount(e.getValue());
            drinkBookingMap.put(drinkBooking.getName(), drinkBooking);
        });
        return mlField;
    }

    public void resetIntegerField(){
        variantLayout.getChildren().forEach(component -> {
            if (component instanceof IntegerField integerField){
                integerField.setValue(0);
            }
        });
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    private void addCSS() {
        getStyle().set("border", "2px solid #338CFF")
                .set("background-color", "#338CFF10").set("padding", "10px")
                .set("border-radius", "30px");
    }
}
