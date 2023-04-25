package de.softwareprojekt.bestbowl.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.function.Consumer;

/**
 * @author Marten Voß
 */
public class VaadinUtils {
    private VaadinUtils() {
    }

    /**
     * Displays a message for 3000ms in the screen center.
     *
     * @param text the text to be displayed
     */
    public static void showNotification(String text) {
        Notification notification = Notification.show(text, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    /**
     * Generates a TextField to be used as a filter in a Grid header
     *
     * @param columnName           name to be displayed in the placeholder text
     * @param filterChangeConsumer method reference that takes the changed value
     * @return component to be used as a filter
     */
    public static Component createFilterHeaderString(String columnName, Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setPlaceholder("Filtern nach '" + columnName + "' ...");
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        return textField;
    }

    /**
     * Generates a IntegerField to be used as a filter in a Grid header
     *
     * @param columnName           name to be displayed in the placeholder text
     * @param filterChangeConsumer method reference that takes the changed value
     * @return component to be used as a filter
     */
    public static Component createFilterHeaderInteger(String columnName, Consumer<String> filterChangeConsumer) {
        IntegerField integerField = new IntegerField();
        integerField.setPlaceholder("Filtern nach '" + columnName + "' ...");
        integerField.setValueChangeMode(ValueChangeMode.EAGER);
        integerField.setClearButtonVisible(true);
        integerField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        integerField.setWidthFull();
        integerField.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                filterChangeConsumer.accept("");
            } else {
                filterChangeConsumer.accept(e.getValue().toString());
            }
        });
        return integerField;
    }

    /**
     * Generates a ComboBox to be used as a filter in a Grid header
     * Important: the filter needs to work with Boolean as a 3-state variable (null = no filter)
     *
     * @param filterChangeConsumer method reference that takes the changed value
     * @param displayValueTrue     text to be displayed for true
     * @param displayValueFalse    text to be displayed for false
     * @return component to be used as a filter
     */
    public static Component createFilterHeaderBoolean(String displayValueTrue, String displayValueFalse, Consumer<Boolean> filterChangeConsumer) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setAllowCustomValue(false);
        comboBox.setItems("*", displayValueTrue, displayValueFalse);
        comboBox.setValue("*");
        comboBox.setWidthFull();
        comboBox.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        comboBox.addValueChangeListener(e -> {
            if (comboBox.getValue().equals(displayValueTrue)) {
                filterChangeConsumer.accept(Boolean.TRUE);
            } else if (comboBox.getValue().equals(displayValueFalse)) {
                filterChangeConsumer.accept(Boolean.FALSE);
            } else {
                filterChangeConsumer.accept(null);
            }
        });
        return comboBox;
    }
}
