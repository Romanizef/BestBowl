package de.softwareprojekt.bestbowl.views.articleForms;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;

import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.Drink;
import de.softwareprojekt.bestbowl.utils.validators.articleValidators.DrinkValidator;

/**
 * Creates the Form for the Drink Entity
 *
 * @author Max Ziller
 */
public class DrinkForm extends FormLayout {

    /**
     * Constructor for the DrinkForm. Creates the name, stock and reorderPoint
     * Fields a checkbox for the active status and the save and cancel Buttons.
     *
     * @param drinkBinder
     */
    public DrinkForm(Binder<Drink> drinkBinder) {
        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        nameField.setRequired(true);
        IntegerField stockField = new IntegerField("Bestand");
        stockField.setWidthFull();
        stockField.setSuffixComponent(new Span("ml"));
        stockField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        stockField.setRequired(true);
        IntegerField reorderPointField = new IntegerField("Meldebestand (-1 für keine Meldung)");
        reorderPointField.setWidthFull();
        reorderPointField.setSuffixComponent(new Span("ml"));
        reorderPointField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        reorderPointField.setRequired(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
        checkboxLayout.add(activeCheckbox);

        add(nameField, stockField, reorderPointField, checkboxLayout);

        drinkBinder.withValidator(new DrinkValidator());
        drinkBinder.bind(nameField, Drink::getName, Drink::setName);
        drinkBinder.bind(stockField, Drink::getStockInMilliliters, Drink::setStockInMilliliters);
        drinkBinder.bind(reorderPointField, Drink::getReorderPoint, Drink::setReorderPoint);
        drinkBinder.bind(activeCheckbox, Drink::isActive, Drink::setActive);
    }
}
