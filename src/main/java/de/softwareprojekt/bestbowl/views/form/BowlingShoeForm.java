package de.softwareprojekt.bestbowl.views.form;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.utils.validators.BowlingShoeValidator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Creates the Form for the Shoe Entity.
 *
 * @author Max Ziller
 */
public class BowlingShoeForm extends FormLayout {

    /**
     * Constructor for the ShoeForm. Creates a boughtAt and size Field and a
     * checkbox for the active status.
     * The {@code Binder} binds the fields to the entity.
     *
     * @param shoeBinder
     */
    public BowlingShoeForm(Binder<BowlingShoe> shoeBinder) {
        DateTimePicker boughtAtField = new DateTimePicker("Kaufdatum");
        boughtAtField.setWidthFull();
        boughtAtField.setLocale(Locale.GERMANY);

        IntegerField sizeField = new IntegerField("Größe");
        sizeField.setWidthFull();
        sizeField.setSuffixComponent(new Span("Größe"));
        sizeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        sizeField.setRequired(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
        checkboxLayout.add(activeCheckbox);

        add(boughtAtField, sizeField, checkboxLayout);

        shoeBinder.withValidator(new BowlingShoeValidator());
        shoeBinder.bind(boughtAtField, bowlingShoe -> LocalDateTime.ofInstant(Instant.ofEpochMilli(bowlingShoe.getBoughtAt()),
                ZoneId.systemDefault()), (bowlingShoe, localDateTime) -> {
            bowlingShoe.setBoughtAt(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        });
        shoeBinder.bind(sizeField, BowlingShoe::getSize, BowlingShoe::setSize);
        shoeBinder.bind(activeCheckbox, BowlingShoe::isActive, BowlingShoe::setActive);
    }
}
