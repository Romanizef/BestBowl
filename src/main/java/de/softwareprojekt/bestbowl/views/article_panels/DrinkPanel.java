package de.softwareprojekt.bestbowl.views.article_panels;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;
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
     * The DrinkPanel function creates a panel for each drink that is available at
     * the bowling alley.
     * The panel contains the name of the drink and an IntegerField for each variant
     * of this drink.
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

    public FormLayout getVariantLayout() {
        return variantLayout;
    }

    /**
     * The createIntegerField function creates an IntegerField for a given
     * DrinkVariant.
     * The IntegerField is used to select the amount of drinks that should be
     * ordered.
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
     * The resetIntegerField function resets all IntegerFields in the variantLayout
     * to 0.
     */
    public void resetIntegerField() {
        variantLayout.getChildren().forEach(component -> {
            if (component instanceof IntegerField integerField) {
                integerField.setValue(0);
            }
        });
    }

    /**
     * The addCSS function adds CSS styling to the DrinkPanel.
     * The border is set to 2px solid #00bcd4, the border-radius is set to 30px,
     * the background-color is set to #00bcd410 and padding is 10px on all sides.
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