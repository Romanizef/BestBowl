package de.softwareprojekt.bestbowl.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableComparator;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.client.Association;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The VaadinUtils class contains utility methods for Vaadin components.
 *
 * @author Marten Voß
 */
public class VaadinUtils {
    public static final String PANEL_COLOR_ALLEY = "#ff7377";
    public static final String PANEL_COLOR_DRINK = "#338cff";
    public static final String PANEL_COLOR_FOOD = "#b3f542";
    public static final String PANEL_COLOR_SHOE = "#ffae1a";
    public static final String VAADIN_PRIMARY_BLUE = "#0065e9";

    /**
     * The VaadinUtils function is a collection of static methods that are used to
     * simplify the creation and configuration of Vaadin components.
     */
    private VaadinUtils() {
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
     * Important: the filter needs to work with Boolean as a 3-state variable (null
     * = no filter)
     *
     * @param filterChangeConsumer method reference that takes the changed value
     * @param displayValueTrue     text to be displayed for true
     * @param displayValueFalse    text to be displayed for false
     * @return component to be used as a filter
     */
    public static Component createFilterHeaderBoolean(String displayValueTrue, String displayValueFalse,
                                                      Consumer<Boolean> filterChangeConsumer) {
        Select<String> comboBox = new Select<>();
        comboBox.setItems("*", displayValueTrue, displayValueFalse);
        comboBox.setValue("*");
        comboBox.setWidthFull();
        comboBox.addThemeVariants(SelectVariant.LUMO_SMALL);
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

    /**
     * Creates a Select with all associations in the dropdown
     *
     * @param label Name of the select
     * @return select
     */
    public static Select<Association> createAssociationSelect(String label) {
        Select<Association> select = new Select<>();
        select.setLabel(label);

        List<Association> associationList = Repos.getAssociationRepository().findAll();
        Set<Association> associationSet = new HashSet<>(associationList.size() + 1);
        associationSet.add(Association.NO_ASSOCIATION);
        associationSet.addAll(associationList);
        ListDataProvider<Association> dataProvider = new ListDataProvider<>(associationSet);
        dataProvider.setSortComparator(
                (SerializableComparator<Association>) (o1, o2) -> o1.getName().compareTo(o2.getName()));

        select.setItems(dataProvider);
        select.setValue(Association.NO_ASSOCIATION);
        select.setItemLabelGenerator(Association::getName);

        return select;
    }

    /**
     * Recursively set the enabled flag for all child components
     *
     * @param children stream of child components
     * @param enabled  new value for enabled
     */
    public static void setChildrenEnabled(Stream<Component> children, boolean enabled) {
        children.forEach(component -> {
            if (component instanceof HasOrderedComponents parent) {
                setChildrenEnabled(parent.getChildren(), enabled);
            }
            if (component instanceof HasEnabled child) {
                child.setEnabled(enabled);
            }
        });
    }

    /**
     * Recursively clear the value for all number field type child components
     *
     * @param children stream of all child elements
     */
    public static void clearNumberFieldChildren(Stream<Component> children) {
        children.forEach(component -> {
            if (component instanceof HasOrderedComponents parent) {
                clearNumberFieldChildren(parent.getChildren());
            }
            if (component instanceof IntegerField child) {
                child.setValue(null);
                child.setInvalid(false);
            }
            if (component instanceof NumberField child) {
                child.setValue(null);
                child.setInvalid(false);
            }
        });
    }

    /**
     * @param confirmationQuestion text of the dialog
     * @param confirmAnswer        text of the confirm button
     * @param cancelAnswer         text of the cancel button
     * @param onConfirm            code to be executed on confirmation
     */
    public static void showConfirmationDialog(String confirmationQuestion, String confirmAnswer, String cancelAnswer,
                                              Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Bestätigen");
        dialog.setText(confirmationQuestion);

        dialog.setCancelable(true);
        dialog.setCancelText(cancelAnswer);

        dialog.setConfirmText(confirmAnswer);
        dialog.addConfirmListener(event -> onConfirm.run());

        dialog.open();
    }

    /**
     * @param stepSizePx step size in pixel
     * @param steps      amount of steps to be created
     * @return an array containing the steps
     */
    public static FormLayout.ResponsiveStep[] createResponsiveSteps(int stepSizePx, int steps) {
        FormLayout.ResponsiveStep[] responsiveSteps = new FormLayout.ResponsiveStep[steps];
        responsiveSteps[0] = new FormLayout.ResponsiveStep("0px", 1);
        for (int i = 1; i < steps; i++) {
            responsiveSteps[i] = new FormLayout.ResponsiveStep((stepSizePx * (i + 1)) + "px", i + 1);
        }
        return responsiveSteps;
    }
}