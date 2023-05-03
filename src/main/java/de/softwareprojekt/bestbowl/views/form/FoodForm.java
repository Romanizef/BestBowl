package de.softwareprojekt.bestbowl.views.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import  com.vaadin.flow.component.formlayout.FormLayout;
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
import de.softwareprojekt.bestbowl.jpa.entities.Food;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.showNotification;

/**
 * @author Max Ziller
 */

public class FoodForm extends FormLayout{
    TextField nameField = new TextField("Name");
    IntegerField stockField = new IntegerField("Bestand");
    IntegerField reorderPointField = new IntegerField("Meldebestand");
    NumberField priceField = new NumberField("Preis");
    Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
    Button saveButton = new Button("Sichern");
    Button cancelButton = new Button("Abbrechen");


    public FoodForm(Binder<Food> foodBinder){
        setWidth("25%");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        stockField.setWidthFull();
        stockField.setSuffixComponent(new Span("Stück"));
        stockField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        reorderPointField.setWidthFull();
        reorderPointField.setSuffixComponent(new Span("Stück"));
        reorderPointField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        priceField.setWidthFull();
        priceField.setSuffixComponent(new Span("EUR"));
        priceField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        checkboxLayout.add(activeCheckbox);


        add(nameField, stockField, reorderPointField, priceField, checkboxLayout,
            createButtonLayout());

        foodBinder.bind(nameField, Food::getName, Food::setName);
        foodBinder.bind(stockField, Food::getStock, Food::setStock);
        foodBinder.bind(reorderPointField, Food::getReorderPoint, Food::setReorderPoint);
        foodBinder.bind(priceField, Food::getPrice, Food::setPrice);
        foodBinder.bind(activeCheckbox, Food::isActive, Food::setActive);
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

        saveButton.addClickListener(clickEvent -> {
            // TODO Kunde in die Datenbank speichern
            //resetEditLayout();
            showNotification("Speise gespeichert");
        });
        cancelButton.addClickListener(clickEvent -> {
           //resetEditLayout();
            showNotification("Bearbeitung abgebrochen");
        });
        return buttonLayout;
    }

   /* private void resetEditLayout() {
        foodGrid.deselectAll();
        selectedFood = null;

        Food food = new Food();
        binder.readBean(food);

        updateEditLayoutState();
        setValueForIntegerFieldChildren(editLayout.getChildren(), null);
    */
}

