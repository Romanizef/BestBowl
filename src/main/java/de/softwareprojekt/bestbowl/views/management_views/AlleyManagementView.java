package de.softwareprojekt.bestbowl.views.management_views;

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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.validators.BowlingAlleyValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Matija
 */
@Route(value = "alleyManagement", layout = MainView.class)
@PageTitle("Bahnverwaltung")
@RolesAllowed({UserRole.OWNER})
public class AlleyManagementView extends VerticalLayout {
    private final transient BowlingAlleyRepository bowlingAlleyRepository;
    private final Binder<BowlingAlley> binder = new Binder<>();
    private Grid<BowlingAlley> bowlingAlleyGrid;
    private FormLayout editLayout;
    private BowlingAlley selectedBowlingAlley = null;
    private Label validationErrorLabel;
    private boolean editingNewBowlingAlley = false;

    /**
     * The AlleyManagementView function is responsible for creating the view that
     * allows the user to manage bowling alleys.
     * The function creates a button that allows the user to create new bowling
     * alleys, and a grid layout containing all existing bowling alleys.
     * The grid layout contains an editable text field for each attribute of a
     * BowlingAlley object, as well as buttons allowing users to save or delete
     * their changes.
     *
     * @param bowlingAlleyRepository bowlingAlleyRepository Access the database
     * @see #updateEditLayoutState()
     * @see #createNewBowlingAlleyButton()
     * @see #createGridLayout()
     */
    @Autowired
    public AlleyManagementView(BowlingAlleyRepository bowlingAlleyRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        setSizeFull();
        Button newBowlingAlleyButton = createNewBowlingAlleyButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newBowlingAlleyButton, gridLayout);
        updateEditLayoutState();
    }

    /**
     * The createNewBowlingAlleyButton function creates a new Button object, sets
     * its width to full and adds the LUMO_PRIMARY theme variant.
     * It also adds a click listener that deselects all items in the
     * bowlingAlleyGrid, creates a new BowlingAlley object and binds it to binder.
     * The editingNewBowlingAlley boolean is set to true and updateEditLayoutState()
     * is called.
     *
     * @return A button
     */
    private Button createNewBowlingAlleyButton() {
        Button button = new Button("Neue Bahn hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            bowlingAlleyGrid.deselectAll();
            selectedBowlingAlley = new BowlingAlley();
            binder.readBean(selectedBowlingAlley);
            editingNewBowlingAlley = true;
            updateEditLayoutState();
        });
        return button;
    }

    /**
     * The createGridLayout function creates a HorizontalLayout, which is then
     * populated with the bowlingAlleyGrid and editLayout.
     * The bowlingAlleyGrid is created by calling the createGrid function, while the
     * editLayout is created by calling the createEditLayout function.
     *
     * @return A horizontallayout
     */
    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        bowlingAlleyGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(bowlingAlleyGrid, editLayout);
        return layout;
    }

    /**
     * The createGrid function creates a grid with the following columns:
     * ID, Active. The grid is populated by all BowlingAlleys in the database.
     * A filter header row is added to each column, allowing for filtering of data
     * in that column.
     *
     * @return A grid
     */
    private Grid<BowlingAlley> createGrid() {
        Grid<BowlingAlley> grid = new Grid<>(BowlingAlley.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<BowlingAlley> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<BowlingAlley> activeColumn = grid
                .addColumn(association -> association.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<BowlingAlley> bowlingAlleyList = bowlingAlleyRepository.findAll();
        GridListDataView<BowlingAlley> dataView = grid.setItems(bowlingAlleyList);
        BowlingAlleyFilter associationFilter = new BowlingAlleyFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", associationFilter::setId));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", associationFilter::setActive));

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<BowlingAlley> optionalBowlingAlley = e.getFirstSelectedItem();
                if (optionalBowlingAlley.isPresent()) {
                    selectedBowlingAlley = optionalBowlingAlley.get();
                    binder.readBean(selectedBowlingAlley);
                    editingNewBowlingAlley = false;
                    updateEditLayoutState();
                } else {
                    resetEditLayout();
                }
            }
        });
        return grid;
    }

    /**
     * The createEditLayout function creates a FormLayout that is used to edit the
     * selected BowlingAlley.
     * The layout contains an IntegerField for the ID, a Checkbox for whether or not
     * it's active and two buttons: one to save changes and one to cancel them.
     *
     * @return A formlayout
     */
    private FormLayout createEditLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidth("25%");

        IntegerField idField = new IntegerField("ID");
        idField.setWidthFull();
        idField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        idField.setRequired(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");

        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);
        layout.add(idField, checkboxLayout, createValidationLabelLayout(), buttonLayoutConfig());

        binder.withValidator(new BowlingAlleyValidator());
        binder.bind(idField, BowlingAlley::getId, (alley, i) -> alley.setId(Objects.requireNonNullElse(i, 0)));
        binder.bind(activeCheckbox, BowlingAlley::isActive,
                (bowlingAlley, active) -> bowlingAlley.setActive(Objects.requireNonNullElse(active, false)));
        return layout;
    }

    /**
     * The buttonLayoutConfig function creates a HorizontalLayout, which contains
     * two buttons: saveButton and cancelButton.
     *
     * @return The buttonlayout
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
     * The saveButtonConfig function configures the saveButton.
     *
     * @param saveButton
     */
    private Button saveButtonConfig(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        saveButton.addClickListener(clickEvent -> {
            if (writeBean()) {
                saveToDb();
            }
        });
        return saveButton;
    }

    /**
     * The cancelButtonConfig function configures the cancelButton. It sets the icon
     * and add a click listener.
     *
     * @param cancelButton
     * @return A button
     */
    private Button cancelButtonConfig(Button cancelButton) {
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
        cancelButton.addClickListener(clickEvent -> resetEditLayout());
        return cancelButton;
    }

    /**
     * The updateEditLayoutState function is used to update the state of the
     * editLayout.
     * It sets the validationErrorLabel text to an empty string and enables or
     * disables all children of editLayout depending on whether a bowling alley has
     * been selected.
     */
    private void updateEditLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(editLayout.getChildren(), selectedBowlingAlley != null);
    }

    /**
     * The resetEditLayout function resets the editLayout to its default state.
     * This means that all fields are cleared and the layout is disabled.
     */
    private void resetEditLayout() {
        bowlingAlleyGrid.deselectAll();
        selectedBowlingAlley = null;

        BowlingAlley bowlingAlley = new BowlingAlley();
        binder.readBean(bowlingAlley);

        updateEditLayoutState();
        clearNumberFieldChildren(editLayout.getChildren());
    }

    /**
     * The createValidationLabelLayout function creates a VerticalLayout that
     * contains the validationErrorLabel.
     * The validationErrorLabel is used to display error messages when the user
     * tries to save an invalid BowlingAlley.
     *
     * @return A verticallayout
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
     * The writeBean function is used to write the values of a bean into the binder.
     * The function returns true if it was successful, false otherwise.
     *
     * @return True if the bean is successfully written else false
     */
    private boolean writeBean() {
        try {
            binder.writeBean(selectedBowlingAlley);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    /**
     * The saveToDb function saves the selectedBowlingAlley to the database.
     * If a new bowling alley is being created, it adds it to the grid.
     * Otherwise, it refreshes the item in question and resets all edit fields.
     *
     * @see #resetEditLayout()
     */
    private void saveToDb() {
        bowlingAlleyRepository.save(selectedBowlingAlley);
        if (editingNewBowlingAlley) {
            bowlingAlleyGrid.getListDataView().addItem(selectedBowlingAlley);
        } else {
            bowlingAlleyGrid.getListDataView().refreshItem(selectedBowlingAlley);
        }
        resetEditLayout();
        Notifications.showInfo("Bahn gespeichert");
    }

    private static class BowlingAlleyFilter {
        private final GridListDataView<BowlingAlley> dataView;
        private String id;
        private Boolean active;

        /**
         * The BowlingAlleyFilter function is used to filter the bowling alleys in the
         * grid.
         * It filters by name, address and number of lanes.
         *
         * @param dataView
         */
        public BowlingAlleyFilter(GridListDataView<BowlingAlley> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * The test function is used to filter the bowlingAlleys in the grid.
         * It checks if a given BowlingAlley matches all the given search criteria.
         *
         * @param bowlingAlley
         * @return A boolean value
         */
        public boolean test(BowlingAlley bowlingAlley) {
            boolean matchesId = matches(String.valueOf(bowlingAlley.getId()), id);
            boolean matchesActive = active == null || active == bowlingAlley.isActive();
            return matchesId && matchesActive;
        }

        /**
         * The setId function is used to set the id of a bowling alley.
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * The setActive function is used to set the active status of a bowling alley.
         *
         * @param active
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }

}