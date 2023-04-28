package de.softwareprojekt.bestbowl.views.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import  com.vaadin.flow.component.formlayout.FormLayout;
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
 * @author Max Ziller
 */

public class DrinkForm extends FormLayout{

    TextField nameField = new TextField("Name");
    IntegerField stockField = new IntegerField("Bestand");
    IntegerField reorderPointField = new IntegerField("Meldebestand");
    NumberField priceField = new NumberField("Preis");
    ComboBox<String> varianteCB = new ComboBox<>("Variante");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
    Button saveButton = new Button("Sichern");
    Button cancelButton = new Button("Abbrechen");

    public DrinkForm(Binder<DrinkVariant> drinkVariantBinder,Binder<Drink> drinkBinder){
        setWidth("25%");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        stockField.setWidthFull();
        stockField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        reorderPointField.setWidthFull();
        reorderPointField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        priceField.setWidthFull();
        priceField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        varianteCB.setWidthFull();
        varianteCB.setAllowCustomValue(false);
        varianteCB.setItems("Smal", "Medium", "Large"); /* soll die verschiedenen Getränkegrößen repräsentieren */
        varianteCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);


        add(nameField, stockField, reorderPointField, varianteCB, priceField, checkboxLayout,
                createButtonLayout());

        drinkBinder.bind(nameField, Drink::getName, Drink::setName);
        drinkBinder.bind(stockField, Drink::getStockInMilliliters, Drink::setStockInMilliliters);
        drinkBinder.bind(reorderPointField,Drink::getReorderPoint, Drink::setReorderPoint);
        drinkVariantBinder.bind(priceField, DrinkVariant::getPrice, DrinkVariant::setPrice); /*Binder funktioniert noch nicht*/
        drinkBinder.bind(activeCheckbox, Drink::isActive, Drink::setActive);
    }

    private Component createButtonLayout(){
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
