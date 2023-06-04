package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.jpa.entities.client.Association;
import de.softwareprojekt.bestbowl.jpa.repositories.client.AssociationRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.validators.client.AssociationValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * Creates a view for all the associations to be created, managed and
 * inactivated
 *
 * @author Matija Kopschek
 * @author Marten Voß
 */
@Route(value = "associationManagement", layout = MainView.class)
@PageTitle("Vereinsverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class AssociationManagementView extends VerticalLayout {
    private final transient AssociationRepository associationRepository;
    private final Binder<Association> binder = new Binder<>();
    private Grid<Association> associationGrid;
    private FormLayout editLayout;
    private Association selectedAssociation = null;
    private Label validationErrorLabel;
    private boolean editingNewAssociation = false;

    /**
     * Constructor for the AssociationManagementView.
     * Instantiates the grid Layout, the new association button
     * and the association repo
     *
     * @param associationRepository
     * @see #createNewAssociationButton()
     * @see #createGridLayout()
     */
    @Autowired
    public AssociationManagementView(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
        setSizeFull();
        Button newAssociationButton = createNewAssociationButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newAssociationButton, gridLayout);
        updateEditLayoutState();
    }

    /**
     * Creates a new button for adding a new association.
     * If the button is clicked the editing Layout becomes available
     * and a new association with all the needed data is added.
     *
     * @return {@code button}
     * @see #updateEditLayoutState()
     */
    private Button createNewAssociationButton() {
        Button button = new Button("Neuen Verein hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            associationGrid.deselectAll();
            selectedAssociation = new Association();
            binder.readBean(selectedAssociation);
            editingNewAssociation = true;
            updateEditLayoutState();
            clearNumberFieldChildren(editLayout.getChildren());
        });
        return button;
    }

    /**
     * Creates a new {@code HorizontalLayout} for the grid and the editing Layout
     *
     * @return {@code layout}
     * @see #createGrid()
     * @see #createEditLayout()
     */
    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        associationGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(associationGrid, editLayout);
        return layout;
    }

    /**
     * Creates a grid based on all the fields of the association class
     * and adds an filter for all rows.
     * {@code If} one of the gridlines is selected the editing Layout is refreshed
     * with all the data out of the currently selected line.
     * {@code Else} the editing Layout is reset to default.
     *
     * @return {@code grid}
     * @see #updateEditLayoutState()
     * @see #resetEditLayout()
     */
    private Grid<Association> createGrid() {
        Grid<Association> grid = new Grid<>(Association.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<Association> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<Association> nameColumn = grid.addColumn("name").setHeader("Name");
        Grid.Column<Association> discountColumn =
                grid.addColumn(association -> formatDouble(association.getDiscount()) + "%").setHeader("Rabatt");
        Grid.Column<Association> activeColumn =
                grid.addColumn(association -> association.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");
        List<Association> associationList = associationRepository.findAll();
        GridListDataView<Association> dataView = grid.setItems(associationList);
        AssociationFilter associationFilter = new AssociationFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", associationFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", associationFilter::setName));
        headerRow.getCell(discountColumn)
                .setComponent(createFilterHeaderString("Rabatt", associationFilter::setDiscount));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", associationFilter::setActive));
        associationFilter.setActive(true);

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Association> optionalAssociation = e.getFirstSelectedItem();
                if (optionalAssociation.isPresent()) {
                    selectedAssociation = optionalAssociation.get();
                    binder.readBean(selectedAssociation);
                    editingNewAssociation = false;
                    updateEditLayoutState();
                } else {
                    resetEditLayout();
                }
            }
        });
        return grid;
    }

    /**
     * Creates a new {@code FormLayout} with two {@code TextFields} for
     * the association name and the discount and also a Checkbox for
     * the active option.
     * Validates all Inputs and binds them with the association class in
     * the database.
     *
     * @return {@code layout}
     */
    private FormLayout createEditLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidth("25%");

        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        nameField.setRequired(true);

        NumberField discountField = new NumberField("Rabatt");
        discountField.setWidthFull();
        discountField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        discountField.setRequired(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");

        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);
        layout.add(nameField, discountField, checkboxLayout, createValidationLabelLayout(), buttonLayoutConfig());

        binder.withValidator(new AssociationValidator());
        binder.bind(nameField, Association::getName, Association::setName);
        binder.bind(discountField, Association::getDiscount, Association::setDiscount);
        binder.bind(activeCheckbox, Association::isActive, Association::setActive);
        return layout;
    }

    /**
     * Ceates a new {@code HorizontalLayout} for the {@code cancelButton} and
     * {@code saveButton}
     *
     * @return {@code buttonLayout}
     * @see #cancelButtonConfig(Button)
     * @see #saveButtonConfig(Button)
     */
    private HorizontalLayout buttonLayoutConfig() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        Button saveButton = new Button("Speichern");
        Button cancelButton = new Button("Abbrechen");
        buttonLayout.add(cancelButtonConfig(cancelButton), saveButtonConfig(saveButton));
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        return buttonLayout;
    }

    /**
     * Creates a {@code saveButton} for the editing Layout.
     * When pressed it shows a confirmation notification
     * and saves all User input to the database
     *
     * @param saveButton
     * @return {@code saveButton}
     * @see #saveToDb()
     * @see #writeBean()
     */
    private Button saveButtonConfig(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        saveButton.addClickListener(clickEvent -> {
            String uneditedName = Objects.requireNonNullElse(selectedAssociation.getName(), "");
            Set<String> associationNameSet = associationRepository.findAllNames();
            if (!editingNewAssociation) {
                associationNameSet.remove(uneditedName);
            }
            if (writeBean()) {
                if (associationNameSet.contains(selectedAssociation.getName())) {
                    validationErrorLabel.setText("Dieser Name wird bereits verwendet");
                    selectedAssociation.setName(uneditedName);
                    return;
                }
                saveToDb();
            }
        });
        return saveButton;
    }

    /**
     * Creates a {@code cancelButton} for the editing Layout.
     * When pressed it shows a confirmation notification and deletes all user input
     *
     * @param cancelButton
     * @return {@code cancelButton}
     * @see #resetEditLayout()
     */
    private Button cancelButtonConfig(Button cancelButton) {
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
        cancelButton.addClickListener(clickEvent -> {
            Notifications.showInfo("Bearbeitung abgebrochen");
            resetEditLayout();
        });
        return cancelButton;
    }

    /**
     *
     */
    private void updateEditLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(editLayout.getChildren(), selectedAssociation != null);
    }

    /**
     * Deletes all the User input in the editing layout
     * and resets it to default
     */
    private void resetEditLayout() {
        associationGrid.deselectAll();
        selectedAssociation = null;
        editingNewAssociation = false;

        Association client = new Association();
        binder.readBean(client);

        updateEditLayoutState();
        clearNumberFieldChildren(editLayout.getChildren());
    }

    /**
     * If the input is missing or wrong this layout
     * with the {@code validationErrorLabel} is portrayed
     *
     * @return {@code validationLabelLayout}
     */
    private VerticalLayout createValidationLabelLayout() {
        VerticalLayout validationLabelLayout = new VerticalLayout();
        validationLabelLayout.setWidthFull();
        validationLabelLayout.setPadding(false);
        validationLabelLayout.setMargin(false);
        validationLabelLayout.setAlignItems(Alignment.CENTER);

        validationErrorLabel = new Label();
        validationErrorLabel.getStyle().set("color", "red");

        validationLabelLayout.add(validationErrorLabel);
        return validationLabelLayout;
    }

    /**
     * Writes changes from the bound fields to the given bean if all validators
     * pass.
     */
    private boolean writeBean() {
        try {
            binder.writeBean(selectedAssociation);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    /**
     * Saving the updated/new association to the database
     */
    private void saveToDb() {
        associationRepository.save(selectedAssociation);
        if (editingNewAssociation) {
            associationGrid.getListDataView().addItem(selectedAssociation);
        } else {
            associationGrid.getListDataView().refreshItem(selectedAssociation);
        }
        resetEditLayout();
        Notifications.showInfo("Verein gespeichert");
    }

    private static class AssociationFilter {
        private final GridListDataView<Association> dataView;
        private String id;
        private String name;
        private String discount;
        private Boolean active;

        /**
         * Constructor for AssociationFilter
         *
         * @param dataView
         */
        public AssociationFilter(GridListDataView<Association> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * Cheking if grid filter row names match association-class fields
         *
         * @param association
         * @return {@code matchesId} {@code matchesName} {@code matchesDiscount}
         * {@code matchesActive}
         */
        public boolean test(Association association) {
            boolean matchesId = matches(String.valueOf(association.getId()), id);
            boolean matchesName = matches(association.getName(), name);
            boolean matchesDiscount = matches(formatDouble(association.getDiscount()) + "%", discount);
            boolean matchesActive = active == null || active == association.isActive();
            return matchesId && matchesDiscount && matchesName && matchesActive;
        }

        /**
         * Setter for association id
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * Setter for association name
         *
         * @param name
         */
        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        /**
         * Setter for association discount
         *
         * @param discount
         */
        public void setDiscount(String discount) {
            this.discount = discount;
            dataView.refreshAll();
        }

        /**
         * Setter for active setting of association
         *
         * @param active
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
