package de.softwareprojekt.bestbowl.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
     * Generates a Checkbox to be used as a filter in a Grid header
     *
     * @param filterChangeConsumer method reference that takes the changed value
     * @param defaultValue         initial value of the Checkbox
     * @return component to be used as a filter
     */
    public static Component createFilterHeaderBoolean(Consumer<Boolean> filterChangeConsumer, boolean defaultValue) {
        Checkbox checkbox = new Checkbox();
        checkbox.setWidthFull();
        checkbox.setValue(defaultValue);
        checkbox.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        return checkbox;
    }
}
