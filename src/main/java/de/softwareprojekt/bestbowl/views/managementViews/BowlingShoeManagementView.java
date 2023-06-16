package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.articleForms.BowlingShoeForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.toDateString;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Max Ziller
 */
@Route(value = "shoeManagement", layout = MainView.class)
@PageTitle("Schuhverwaltung")
@RolesAllowed({UserRole.OWNER})
public class BowlingShoeManagementView extends VerticalLayout {
    private final transient BowlingShoeRepository bowlingShoeRepository;
    private final Binder<BowlingShoe> bowlingShoeBinder = new Binder<>();
    private final Button saveButton = new Button("Sichern");
    private final Button cancelButton = new Button("Abbrechen");
    private Grid<BowlingShoe> bowlingShoeGrid;
    private BowlingShoeForm bowlingShoeForm;
    private BowlingShoe selectedShoe = null;
    private Label validationErrorLabel;
    private boolean editingNewBowlingShoe = false;

    /**
     * The BowlingShoeManagementView function is responsible for the creation of a
     * view that allows the user to manage bowling shoes.
     * The BowlingShoeManagementView function creates a newBowlingShoeButton, which
     * allows the user to create new bowling shoes.
     * The BowlingShoeManagementView function also creates a gridFormLayout, which
     * contains both an editable grid and form for editing existing bowling shoes.
     *
     * @param bowlingShoeRepository
     */
    @Autowired
    public BowlingShoeManagementView(BowlingShoeRepository bowlingShoeRepository) {
        this.bowlingShoeRepository = bowlingShoeRepository;
        setSizeFull();
        Button newBowlingShoeButton = createNewBowlingShoeButton();
        HorizontalLayout bowlingShoeGridFormLayout = createBowlingShoeGridFormLayout();
        add(newBowlingShoeButton, bowlingShoeGridFormLayout);
        updateEditBowlingShoeLayoutState();
    }

    /**
     * The createNewBowlingShoeButton function creates a new Button object with the
     * text &quot;Neue Schuhe hinzufügen&quot; and sets its width to 100%.
     * It then adds the LUMO_PRIMARY theme variant to it.
     * When clicked, it deselects all items in the bowlingShoeGrid, creates a new
     * BowlingShoe object called selectedShoe and reads this into bowlingShoeBinder.
     * The saveButton is enabled while cancelButton is disabled.
     * editingNewBowlingShoe is set to false and updateEditBowlingShoeLayoutState()
     * as well as clearNumberFieldChildren(bowlingShoeForm.getChildren()) are
     * called.
     *
     * @return A button
     * @see #updateEditBowlingShoeLayoutState()
     */
    private Button createNewBowlingShoeButton() {
        Button button = new Button("Neue Schuhe hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            bowlingShoeGrid.deselectAll();
            selectedShoe = new BowlingShoe();
            bowlingShoeBinder.readBean(selectedShoe);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            editingNewBowlingShoe = false;
            updateEditBowlingShoeLayoutState();
            clearNumberFieldChildren(bowlingShoeForm.getChildren());
        });
        return button;
    }

    /**
     * The updateEditBowlingShoeLayoutState function is used to update the state of
     * the edit bowling shoe layout.
     * It sets the text of a validation error label and enables or disables all
     * children in a form depending on whether
     * there is currently a selected shoe.
     */
    private void updateEditBowlingShoeLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(bowlingShoeForm.getChildren(), selectedShoe != null);
    }

    /**
     * The createBowlingShoeGridFormLayout function creates a HorizontalLayout that
     * contains the bowlingShoeGrid and the BowlingShoeForm.
     * The bowlingShoeGrid is created by calling createBowlingShoeGrid().
     * The BowlingShoeForm is created by calling createBowlingShoeFormLayout().
     *
     * @return A horizontallayout
     * @see #createBowlingShoeGrid()
     * @see #createBowlingShoeFormLayout()
     */
    private HorizontalLayout createBowlingShoeGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        bowlingShoeGrid = createBowlingShoeGrid();
        layout.add(bowlingShoeGrid, createBowlingShoeFormLayout());
        return layout;
    }

    /**
     * The createBowlingShoeFormLayout function creates a layout for the bowling
     * shoe form.
     *
     * @return A verticallayout
     */
    private VerticalLayout createBowlingShoeFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("25%");
        bowlingShoeForm = new BowlingShoeForm(bowlingShoeBinder);
        layout.add(bowlingShoeForm, createValidationLabelLayout(), createButton());
        return layout;
    }

    /**
     * The createValidationLabelLayout function creates a VerticalLayout that
     * contains the validationErrorLabel.
     * The validationErrorLabel is used to display error messages when the user
     * tries to save an invalid BowlingShoeForm.
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
     * The writeBean function is used to write the values of a form into an object.
     *
     * @return True if the bean was successfully written else false
     */
    private boolean writeBean() {
        try {
            bowlingShoeBinder.writeBean(selectedShoe);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    /**
     * The createButton function creates a HorizontalLayout containing two buttons:
     * saveButton and cancelButton. The saveButton is enabled when the user has made
     * changes to the form,
     * and disabled otherwise. When clicked, it saves the changes to the database if
     * they are valid, or resets them otherwise.
     * The cancel button is enabled when there are unsaved changes in the form, and
     * disabled otherwise. When clicked it resets all unsaved changes in
     * BowlingShoeForm (the editable fields). Both buttons have an icon next to
     * their text label for better visual feedback of what they do.
     *
     * @return A horizontallayout
     * @see #saveToDnAndUpdateBowlingShoe()
     */
    public Component createButton() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        saveButton.setEnabled(false);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        cancelButton.setEnabled(false);
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        saveButton.addClickListener(clickEvent -> {
            BowlingShoe undeditedBowlingShoe = new BowlingShoe();
            if (writeBean()) {
                saveToDnAndUpdateBowlingShoe();
            } else {
                selectedShoe.copyValueOf(undeditedBowlingShoe);
            }
        });
        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        return buttonLayout;
    }

    /**
     * The saveToDnAndUpdateBowlingShoe function saves the selected shoe to the
     * database and updates
     * the grid. If a new shoe is being edited, it will be added to the grid.
     * Otherwise, it will refresh
     * that item in place. After saving/updating, resetEditLayout() is called and a
     * notification is shown
     *
     * @see #resetEditLayout()
     */
    private void saveToDnAndUpdateBowlingShoe() {
        bowlingShoeRepository.save(selectedShoe);
        if (editingNewBowlingShoe) {
            bowlingShoeGrid.getListDataView().addItem(selectedShoe);
        } else {
            bowlingShoeGrid.getListDataView().refreshItem(selectedShoe);
        }
        resetEditLayout();
        Notifications.showInfo("Schuh gespeichert");
    }

    /**
     * The resetEditLayout function is used to reset the edit layout of the bowling
     * shoe view.
     * It deselects all items in the grid, sets selectedShoe to null, sets
     * editingNewBowlingShoe to false and disables both saveButton and cancelButton.
     * Finally, it calls updateEditBowlingShoeLayoutState() which updates the state
     * of all components in this function's scope (e.g. enables/disables them).
     *
     * @see #updateEditBowlingShoeLayoutState()
     */
    private void resetEditLayout() {
        bowlingShoeGrid.deselectAll();
        selectedShoe = null;
        editingNewBowlingShoe = false;
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);

        updateEditBowlingShoeLayoutState();
        clearNumberFieldChildren(bowlingShoeForm.getChildren());
    }

    /**
     * The createBowlingShoeGrid function creates a Grid of BowlingShoes.
     * It also adds the ability to filter by ID, boughtAt, size and active.
     * The function returns the created grid.
     *
     * @return A grid&lt;bowlingshoe&gt; object
     */
    private Grid<BowlingShoe> createBowlingShoeGrid() {
        Grid<BowlingShoe> shoeGrid = new Grid<>(BowlingShoe.class);
        shoeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        shoeGrid.removeAllColumns();
        Grid.Column<BowlingShoe> idColumn = shoeGrid.addColumn(BowlingShoe::getId).setHeader("ID");
        Grid.Column<BowlingShoe> boughtAtColumn = shoeGrid.addColumn(s -> toDateString(s.getBoughtAt()), "dd mm yyyy")
                .setHeader("Kaufdatum");
        Grid.Column<BowlingShoe> sizeColumn = shoeGrid.addColumn("size").setHeader("Größe");
        Grid.Column<BowlingShoe> activeColumn = shoeGrid.addColumn(s -> s.isActive() ? "Aktiv" : "Inaktiv")
                .setHeader("Aktiv");
        shoeGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        shoeGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        shoeGrid.setWidth("75%");
        shoeGrid.setHeight("100%");

        List<BowlingShoe> shoeList = bowlingShoeRepository.findAll();
        GridListDataView<BowlingShoe> dataView = shoeGrid.setItems(shoeList);

        BowlingShoeManagementView.BowlingShoeFilter shoeFilter = new BowlingShoeManagementView.BowlingShoeFilter(
                dataView);
        shoeGrid.getHeaderRows().clear();
        HeaderRow headerRow = shoeGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", shoeFilter::setId));
        headerRow.getCell(boughtAtColumn).setComponent(createFilterHeaderString("Kaufdatum", shoeFilter::setBoughtAt));
        headerRow.getCell(sizeColumn).setComponent(createFilterHeaderInteger("Größe", shoeFilter::setSize));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", shoeFilter::setActive));
        shoeGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<BowlingShoe> optionalBowlingShoe = e.getFirstSelectedItem();
                saveButton.setEnabled(true);
                cancelButton.setEnabled(true);
                if (optionalBowlingShoe.isPresent()) {
                    selectedShoe = optionalBowlingShoe.get();
                    bowlingShoeBinder.readBean(selectedShoe);
                    editingNewBowlingShoe = false;
                } else {
                    selectedShoe = null;
                    bowlingShoeBinder.readBean(null);
                    saveButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                }
            }
            updateEditBowlingShoeLayoutState();
        });
        return shoeGrid;
    }

    private static class BowlingShoeFilter {
        private final GridListDataView<BowlingShoe> dataView;
        private String id;
        private String boughtAt;
        private String size;
        private Boolean active;

        /**
         * The BowlingShoeFilter function is a filter function that filters the dataView
         * by the given parameters.
         *
         * @param dataView
         */
        public BowlingShoeFilter(GridListDataView<BowlingShoe> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * The test function is used to filter the grid.
         * It checks if the shoe matches all the given filters.
         * If a filter is null, it will be ignored and always return true for that
         * filter.
         *
         * @param shoe
         * @return True if the shoe matches all the criteria
         */
        public boolean test(BowlingShoe shoe) {
            boolean matchesId = matches(String.valueOf(shoe.getId()), id);
            boolean matchesBoughtAt = matches(Utils.toDateString(shoe.getBoughtAt()), boughtAt);
            boolean matchesSize = matches(String.valueOf(shoe.getSize()), size);
            boolean matchesActive = active == null || active == shoe.isActive();
            return matchesId && matchesBoughtAt && matchesSize && matchesActive;
        }

        /**
         * The matches function is used to filter the grid by a search term.
         * It returns true if the value contains the searchTerm, or if no searchTerm has
         * been entered yet.
         *
         * @param value      Check if the searchterm is null or empty
         * @param searchTerm Search for a specific term in the list
         * @return True if the searchterm is null or empty, or if the value contains the
         * searchterm
         */
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        /**
         * The setId function is used to set the id of a bowling shoe.
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * The setBoughtAt function sets the boughtAt variable to the value of its
         * parameter.
         * It then refreshes all data in the dataView object.
         *
         * @param boughtAt Set the value of the boughtat variable
         */
        public void setBoughtAt(String boughtAt) {
            this.boughtAt = boughtAt;
            dataView.refreshAll();
        }

        /**
         * The setSize function is used to filter the grid by size.
         *
         * @param size
         */
        public void setSize(String size) {
            this.size = size;
            dataView.refreshAll();
        }

        /**
         * The setActive function is used to set the active state of a bowling shoe.
         *
         * @param active Set the active variable to true or false
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}