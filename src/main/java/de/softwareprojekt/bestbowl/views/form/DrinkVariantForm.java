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
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.Association;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;

import java.util.ArrayList;
import java.util.List;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createAssociationCB;

/**
 * Creates the Form for the DrinkVariant Entity
 *
 * @author Max Ziller
 */
public class DrinkVariantForm extends FormLayout {
    TextField nameField = new TextField("Name");
    NumberField priceField = new NumberField("Preis");
    IntegerField variantField = new IntegerField("Variante");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");

    /**
     * Constructor for the DrinkVariantForm. Creates a name field, a variant field
     * and a price field.
     * A checkbox is also generated for the active status of the drink.
     * The {@code Binder} binds the fields to the entity.
     *
     * @param drinkVariantBinder
     * @param drinkBinder
     *
     */
    public DrinkVariantForm(Binder<DrinkVariant> drinkVariantBinder, Binder<Drink> drinkBinder) {

        ComboBox<Drink>  drinkCB = new ComboBox<>();
        drinkCB.setWidthFull();
        drinkCB.setItems(); // Noch keine Daten in der ComboBox
        drinkCB.setAllowCustomValue(false);
        drinkCB.setPlaceholder("-");
        drinkCB.setRequiredIndicatorVisible(true);
        drinkCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        variantField.setWidthFull();
        variantField.setSuffixComponent(new Span("ml"));
        variantField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        priceField.setWidthFull();
        priceField.setSuffixComponent(new Span("EUR"));
        priceField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);

        add(drinkCB,nameField, variantField, priceField, checkboxLayout);

       // drinkVariantBinder.bind(nameField, drinkVariant -> drinkVariant.getDrink().getName(),
         //      ((drinkVariant, s) -> drinkVariant.getDrink().setName(s)));

        drinkVariantBinder.bind(priceField, DrinkVariant::getPrice, DrinkVariant::setPrice);
        drinkVariantBinder.bind(variantField, DrinkVariant::getMl, DrinkVariant::setMl);
       // drinkVariantBinder.bind(activeCheckbox,drinkVariant -> drinkVariant.getDrink().isActive(),
        //       ((drinkVariant, s) -> drinkVariant.getDrink().setActive(s)));
    }
}
