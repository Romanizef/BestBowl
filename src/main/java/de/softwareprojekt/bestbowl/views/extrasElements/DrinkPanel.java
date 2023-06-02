package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.PANEL_COLOR_DRINK;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createResponsiveSteps;

/**
 * Class for the Drink Panel in the ExtrasView.
 *
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class DrinkPanel extends HorizontalLayout {
    private final FormLayout variantLayout;

    /**
     * Constructor for the Drink Panel.
     *
     * @param drink
     * @param bowlingAlleyBooking
     * @param drinkBookingMap
     * @see #addCSS()
     * @see #createIntegerField(DrinkVariant, BowlingAlleyBooking, Map)
     */
    public DrinkPanel(Drink drink, BowlingAlleyBooking bowlingAlleyBooking, Map<String, DrinkBooking> drinkBookingMap) {
        variantLayout = new FormLayout();
        variantLayout.setResponsiveSteps(createResponsiveSteps(200, 5));

        Label label = new Label(drink.getName());
        label.setMinWidth("250px");
        label.setMaxWidth("250px");
        label.getStyle().set("text-align", "left");

        List<DrinkVariant> drinkVariantList = new ArrayList<>(drink.getDrinkVariants());
        drinkVariantList.sort(Comparator.comparingInt(DrinkVariant::getMl));

        for (DrinkVariant drinkVariant : drinkVariantList) {
            if (drinkVariant.isActive()) {
                variantLayout.add(createIntegerField(drinkVariant, bowlingAlleyBooking, drinkBookingMap));
            }
        }

        addCSS();
        setAlignItems(Alignment.CENTER);
        setWidthFull();
        add(label, variantLayout);
    }

    /**
     * Creates an IntegerField for the Drink Panel.
     *
     * @param drinkVariant
     * @param bowlingAlleyBooking
     * @param drinkBookingMap
     * @return {@code IntegerField}
     */
    public IntegerField createIntegerField(DrinkVariant drinkVariant, BowlingAlleyBooking bowlingAlleyBooking,
                                           Map<String, DrinkBooking> drinkBookingMap) {
        IntegerField mlField = new IntegerField();
        mlField.setValue(0);
        mlField.setStepButtonsVisible(true);
        mlField.setMin(0);
        mlField.setSuffixComponent(new Span(drinkVariant.getMl() + "ml"));
        mlField.setMax(drinkVariant.getDrink().getStockInMilliliters() / drinkVariant.getMl());

        mlField.addValueChangeListener(e -> {
            Integer value = e.getValue();
            if (value == null) {
                value = 0;
            }
            if (value < mlField.getMin() || value > mlField.getMax()) {
                mlField.setValue(e.getOldValue());
                Notifications.showError("Nicht genügend vom Getränk: " + drinkVariant.getDrink().getName());
                return;
            }

            DrinkBooking temp = new DrinkBooking(drinkVariant, bowlingAlleyBooking);
            DrinkBooking drinkBooking = drinkBookingMap.getOrDefault(temp.getName(), temp);

            drinkBooking.setAmount(value);
            if (drinkBooking.getAmount() == 0) {
                drinkBookingMap.remove(drinkBooking.getName());
            } else {
                drinkBookingMap.put(drinkBooking.getName(), drinkBooking);
            }
        });
        return mlField;
    }

    /**
     * Resets the IntegerField of the Drink Panel to the value 0.
     */
    public void resetIntegerField() {
        variantLayout.getChildren().forEach(component -> {
            if (component instanceof IntegerField integerField) {
                integerField.setValue(0);
            }
        });
    }

    /**
     * Adds CSS style to the Drink Panel.
     */
    private void addCSS() {
        getStyle()
                .set("border", "2px solid " + PANEL_COLOR_DRINK)
                .set("border-radius", "30px")
                .set("background-color", PANEL_COLOR_DRINK + "10")
                .set("padding", "10px")
                .set("padding-left", "15px")
                .set("padding-right", "20px");
    }
}
