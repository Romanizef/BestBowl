package de.softwareprojekt.bestbowl.views.form;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.utils.validators.FoodValidator;

/**
 * Creates the Form for the Food Entity.
 *
 * @author Max Ziller
 */
public class FoodForm extends FormLayout {

    /**
     * Constructor for the FoodForm. Creates a name, stock, reorderPoint and price
     * Fields and a checkbox for the active status.
     * The {@code Binder} binds the fields to the entity.
     *
     * @param foodBinder
     */
    public FoodForm(Binder<Food> foodBinder) {
        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        nameField.setRequired(true);
        IntegerField stockField = new IntegerField("Bestand");
        stockField.setWidthFull();
        stockField.setSuffixComponent(new Span("Stück"));
        stockField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        stockField.setRequired(true);
        IntegerField reorderPointField = new IntegerField("Meldebestand (-1 für keine Meldung)");
        reorderPointField.setWidthFull();
        reorderPointField.setSuffixComponent(new Span("Stück"));
        reorderPointField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        reorderPointField.setRequired(true);
        NumberField priceField = new NumberField("Preis");
        priceField.setWidthFull();
        priceField.setSuffixComponent(new Span("EUR"));
        priceField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        priceField.setRequired(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
        checkboxLayout.add(activeCheckbox);


        add(nameField, stockField, reorderPointField, priceField, checkboxLayout);

        foodBinder.withValidator(new FoodValidator());
        foodBinder.bind(nameField, Food::getName, Food::setName);
        foodBinder.bind(stockField, Food::getStock, Food::setStock);
        foodBinder.bind(reorderPointField, Food::getReorderPoint, Food::setReorderPoint);
        foodBinder.bind(priceField, Food::getPrice, Food::setPrice);
        foodBinder.bind(activeCheckbox, Food::isActive, Food::setActive);
    }
}
