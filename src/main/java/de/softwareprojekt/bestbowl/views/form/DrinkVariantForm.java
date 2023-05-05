package de.softwareprojekt.bestbowl.views.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;

/**
 * Creates the Form for the DrinkVariant Entity
 * 
 * @author Max Ziller
 */
public class DrinkVariantForm extends FormLayout {
    TextField nameField = new TextField("Name");
    NumberField priceField = new NumberField("Preis");
    IntegerField variantField = new IntegerField("Variante");
    // ComboBox<String> variantCB = new ComboBox<>("Variante");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");

    Button saveButton = new Button("Sichern");
    Button cancelButton = new Button("Abbrechen");

    /**
     * Constructor for the DrinkVariantForm. Creates a name field, a variant field
     * and a price field.
     * A checkbox is also generated for the active status of the drink.
     * The {@code Binder} binds the fields to the entity.
     * 
     * @param drinkVariantBinder
     * @param drinkBinder
     * @see #createButtonLayout()
     */
    public DrinkVariantForm(Binder<DrinkVariant> drinkVariantBinder, Binder<Drink> drinkBinder) {
        setWidth("25%");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        /*
         * variantCB.setWidthFull();
         * variantCB.setAllowCustomValue(false);
         * variantCB.setItems("Klein 250ml", "Mittel 500ml", "Groß 750ml"); //noch nicht
         * final
         * variantCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
         */

        variantField.setWidthFull();
        variantField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        priceField.setWidthFull();
        priceField.setSuffixComponent(new Span("EUR"));
        priceField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);

        add(nameField, variantField, priceField, checkboxLayout,
                createButtonLayout());

        drinkBinder.bind(nameField, Drink::getName, Drink::setName); /* Binder funktioniert noch nicht */
        drinkVariantBinder.bind(priceField, DrinkVariant::getPrice, DrinkVariant::setPrice);
        drinkVariantBinder.bind(variantField, DrinkVariant::getMl, DrinkVariant::setMl);
    }

    /**
     * Creates the button layout for the form. A save and a cancel Button are
     * created too. They are activated with the Enter and Escape Key.
     * 
     * @return {@code HorizontalLayout}
     */
    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);

        saveButton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);

        return buttonLayout;
    }
}
