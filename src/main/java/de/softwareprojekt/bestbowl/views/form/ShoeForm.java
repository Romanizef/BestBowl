package de.softwareprojekt.bestbowl.views.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;

import java.time.LocalDate;
import java.util.Locale;

public class ShoeForm extends FormLayout {
    DatePicker boughtAtField = new DatePicker("Kaufdatum");
    IntegerField sizeField = new IntegerField("Größe");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
    Button saveButton = new Button("Sichern");
    Button cancelButton = new Button("Abbrechen");

    public ShoeForm(Binder<BowlingShoe> shoeBinder){
        setWidth("25%");
        boughtAtField.setWidthFull();
        boughtAtField.setLocale(Locale.GERMANY);
        boughtAtField.setValue(LocalDate.now());
        sizeField.setWidthFull();
        sizeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);

        add(boughtAtField, sizeField, checkboxLayout, createButtonLayout());

       //shoeBinder.bind(boughtAtField, BowlingShoe::getBoughtAt, BowlingShoe::setBoughtAt);
        shoeBinder.bind(sizeField, BowlingShoe::getSize, BowlingShoe::setSize);
        shoeBinder.bind(activeCheckbox, BowlingShoe::isActive, BowlingShoe::setActive);
    }
    private Component createButtonLayout(){
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);

        saveButton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);

        return buttonLayout;
    }

}
