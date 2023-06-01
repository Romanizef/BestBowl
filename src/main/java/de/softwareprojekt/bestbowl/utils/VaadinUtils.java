package de.softwareprojekt.bestbowl.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.spring.security.AuthenticationContext;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.Association;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Marten Voß
 */
public class VaadinUtils {
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

    /**
     * Creates a ComboBox with all associations in the dropdown
     *
     * @param label Name of the ComboBox
     * @return ComboBox
     */
    public static ComboBox<Association> createAssociationCB(String label) {
        ComboBox<Association> comboBox = new ComboBox<>(label);

        List<Association> associationList = Repos.getAssociationRepository().findAll();
        Set<Association> associationSet = new HashSet<>(associationList.size() + 1);
        associationSet.add(Association.NO_ASSOCIATION);
        associationSet.addAll(associationList);
        ListDataProvider<Association> dataProvider = new ListDataProvider<>(associationSet);
        dataProvider.setSortComparator((SerializableComparator<Association>) (o1, o2) -> o1.getName().compareTo(o2.getName()));

        comboBox.setItems(dataProvider);
        comboBox.setValue(Association.NO_ASSOCIATION);
        comboBox.setAllowCustomValue(false);
        comboBox.setItemLabelGenerator(Association::getName);

        return comboBox;
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
     * Recursively set the value for all IntegerField child components
     *
     * @param children stream of all child elements
     * @param value    new value for all IntegerFields
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
     * @param authenticationContext current authentication context
     * @param role                  role to be checked
     * @return if the current user has that role
     */
    public static boolean isCurrentUserInRole(AuthenticationContext authenticationContext, String role) {
        Optional<UserDetails> user = authenticationContext.getAuthenticatedUser(UserDetails.class);
        if (user.isPresent()) {
            UserDetails userDetails = user.get();
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                if (authority.getAuthority().toLowerCase().contains(role.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param confirmationQuestion text of the dialog
     * @param confirmAnswer        text of the confirm button
     * @param cancelAnswer         text of the cancel button
     * @param onConfirm            code to be executed on confirmation
     */
    public static void showConfirmationDialog(String confirmationQuestion, String confirmAnswer, String cancelAnswer, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Bestätigen");
        dialog.setText(confirmationQuestion);

        dialog.setCancelable(true);
        dialog.setCancelText(cancelAnswer);

        dialog.setConfirmText(confirmAnswer);
        dialog.addConfirmListener(event -> onConfirm.run());

        dialog.open();
    }

    public static FormLayout.ResponsiveStep[] createResponsiveSteps(int stepSizePx, int steps) {
        FormLayout.ResponsiveStep[] responsiveSteps = new FormLayout.ResponsiveStep[steps];
        for (int i = 0; i < steps; i++) {
            responsiveSteps[i] = new FormLayout.ResponsiveStep((stepSizePx * (i + 1)) + "px", i + 1);
        }
        return responsiveSteps;
    }
}
