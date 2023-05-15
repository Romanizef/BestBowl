package de.softwareprojekt.bestbowl.views.form;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import static de.softwareprojekt.bestbowl.utils.Utils.toDateString;

/**
 * Creates the Form for the Shoe Entity.
 *
 * @author Max Ziller
 */
public class BowlingShoeForm extends FormLayout {
    public DateTimePicker boughtAtField = new DateTimePicker("Kaufdatum");
    IntegerField sizeField = new IntegerField("Größe");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");

    /**
     * Constructor for the ShoeForm. Creates a boughtAt and size Field and a
     * checkbox for the active status.
     * The {@code Binder} binds the fields to the entity.
     *
     * @param shoeBinder
     */
    public BowlingShoeForm(Binder<BowlingShoe> shoeBinder) {
        boughtAtField.setWidthFull();
        boughtAtField.setLocale(Locale.GERMANY);

        sizeField.setWidthFull();
        sizeField.setSuffixComponent(new Span("Größe"));
        sizeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);

        add(boughtAtField, sizeField, checkboxLayout);

        shoeBinder.bind(boughtAtField, bowlingShoe -> {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(bowlingShoe.getBoughtAt()), ZoneId.systemDefault());
        },(bowlingShoe, localDateTime) -> {
            bowlingShoe.setBoughtAt(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000);
        });
        shoeBinder.bind(sizeField, BowlingShoe::getSize, BowlingShoe::setSize);
        shoeBinder.bind(activeCheckbox, BowlingShoe::isActive, BowlingShoe::setActive);
    }
}
