package de.softwareprojekt.bestbowl.views.form;

import com.vaadin.flow.component.Component;
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
import de.softwareprojekt.bestbowl.utils.enums.UserRole;

/**
 * Creates the Form for the Drink Entity
 * 
 * @author Max Ziller
 */
public class DrinkForm extends FormLayout {
    TextField nameField = new TextField("Name");
    IntegerField stockField = new IntegerField("Bestand");
    IntegerField reorderPointField = new IntegerField("Meldebestand");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
    Button saveButton = new Button("Sichern");
    Button cancelButton = new Button("Abbrechen");

    /**
     * Constructor for the DrinkForm. Creates the name, stock and reorderPoint
     * Fields a checkbox for the active status and the save and cancel Buttons.
     * 
     * @param drinkBinder
     * @see #createButtonLayout()
     */
    public DrinkForm(Binder<Drink> drinkBinder) {
        setWidth("25%");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        stockField.setWidthFull();
        stockField.setSuffixComponent(new Span("ml"));
        stockField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        reorderPointField.setWidthFull();
        reorderPointField.setSuffixComponent(new Span("ml"));
        reorderPointField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);

        add(nameField, stockField, reorderPointField, checkboxLayout,
                createButtonLayout());

        drinkBinder.bind(nameField, Drink::getName, Drink::setName);
        drinkBinder.bind(stockField, Drink::getStockInMilliliters, Drink::setStockInMilliliters);
        drinkBinder.bind(reorderPointField, Drink::getReorderPoint, Drink::setReorderPoint);
        drinkBinder.bind(activeCheckbox, Drink::isActive, Drink::setActive);
    }

    /**
     * Creates the Button Layout for the DrinkForm, including a saveButton and a
     * cancelButton. They are activated with the Enter and Escape Key.
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
