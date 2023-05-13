package de.softwareprojekt.bestbowl.views.form;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;

import java.time.LocalDate;
import java.util.Locale;

import static de.softwareprojekt.bestbowl.utils.Utils.toDateString;

/**
 * Creates the Form for the Shoe Entity.
 *
 * @author Max Ziller
 */
public class BowlingShoeForm extends FormLayout {
    public DatePicker boughtAtField = new DatePicker("Kaufdatum");
    public TextField dateField = new TextField("Kaufdatum");
    IntegerField sizeField = new IntegerField("Größe");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");

    /**
     * Constructor for the ShoeForm. Creates a boughtAt and size Field and a
     * checkbox for the active status.
     * The {@code Binder} binds the fields to the entity.
     *
     * @param shoeBinder
     *
     */
    public BowlingShoeForm(Binder<BowlingShoe> shoeBinder) {
        boughtAtField.setWidthFull();
        boughtAtField.setLocale(Locale.GERMANY);
        boughtAtField.setValue(LocalDate.now());
        boughtAtField.setVisible(false);

        dateField.setWidthFull();
        dateField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        dateField.setVisible(true);

        sizeField.setWidthFull();
        sizeField.setSuffixComponent(new Span("Größe"));
        sizeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);

        add(boughtAtField,dateField, sizeField, checkboxLayout);

       // shoeBinder.bind(dateField, bowlingShoe -> toDateString(bowlingShoe.getBoughtAt()), BowlingShoe::setBoughtAt);
        shoeBinder.bind(sizeField, BowlingShoe::getSize, BowlingShoe::setSize);
        shoeBinder.bind(activeCheckbox, BowlingShoe::isActive, BowlingShoe::setActive);
    }
}
