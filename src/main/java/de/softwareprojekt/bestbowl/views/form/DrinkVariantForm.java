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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;

public class DrinkVariantForm extends FormLayout {
    TextField nameField = new TextField("Name");
    NumberField priceField = new NumberField("Preis");
    ComboBox<String> varianteCB = new ComboBox<>("Variante");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");

    Button saveButton = new Button("Sichern");
    Button cancelButton = new Button("Abbrechen");

    public DrinkVariantForm(Binder<DrinkVariant> drinkVariantBinder,Binder<Drink> drinkBinder){
        setWidth("25%");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        varianteCB.setWidthFull();
        varianteCB.setAllowCustomValue(false);
        varianteCB.setItems("Klein 250ml", "Mittel 500ml", "Groß 750ml");
        varianteCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        priceField.setWidthFull();
        priceField.setSuffixComponent(new Span("EUR"));
        priceField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);

        add(nameField, varianteCB, priceField, checkboxLayout,
                createButtonLayout());

        drinkBinder.bind(nameField, Drink::getName, Drink::setName);
        drinkVariantBinder.bind(priceField, DrinkVariant::getPrice, DrinkVariant::setPrice); /*Binder funktioniert noch nicht*/
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
