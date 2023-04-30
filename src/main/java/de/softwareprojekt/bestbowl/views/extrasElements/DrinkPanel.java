package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class DrinkPanel extends HorizontalLayout {

    public DrinkPanel(Drink drink) {
        FormLayout variantLayout = new FormLayout();
        variantLayout.setResponsiveSteps(new ResponsiveStep("200px", 3));

        Label label = new Label(drink.getName());
        label.setMinWidth("250px");
        label.setMaxWidth("250px");
        List<DrinkVariant> drinkVariantList = new ArrayList<>(drink.getDrinkVariants());
        drinkVariantList.sort(Comparator.comparingInt(DrinkVariant::getMl));

        for (DrinkVariant drinkVariant : drinkVariantList) {
            variantLayout.add(createIntegerField(drinkVariant));
        }

        setWidthFull();
        addCSS();
        setAlignItems(Alignment.CENTER);
        add(label, variantLayout);
    }

    public IntegerField createIntegerField(DrinkVariant drinkVariant) {
        IntegerField mlField = new IntegerField();
        mlField.setValue(0);
        mlField.setStepButtonsVisible(true);
        mlField.setMin(0);
        mlField.setSuffixComponent(new Span(drinkVariant.getMl() + "ml"));
        mlField.setMax(drinkVariant.getDrink().getStockInMilliliters() / drinkVariant.getMl());


        mlField.addValueChangeListener(e -> {
        });
        return mlField;
    }

    private void addCSS() {
        getStyle().set("border", "2px solid #338CFF");

        getStyle().set("background-color", "#338CFF04");
        getStyle().set("padding", "10px");
        getStyle().set("border-radius", "10px");
    }
}
