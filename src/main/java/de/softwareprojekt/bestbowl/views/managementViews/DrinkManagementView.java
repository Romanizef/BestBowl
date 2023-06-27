package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
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
import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.articleForms.DrinkForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Max Ziller
 */
@Route(value = "drinkManagement", layout = MainView.class)
@PageTitle("Getränkeverwaltung")
@RolesAllowed({UserRole.OWNER})
@CssImport(value = "./styles/styles.css", themeFor = "vaadin-grid")
public class DrinkManagementView extends VerticalLayout {
    private final transient DrinkRepository drinkRepository;
    private final Binder<Drink> drinkBinder = new Binder<>();
    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");
    private final Button saveAndOpenDrinkVariantButton = new Button("Speichern & neue Variante anlegen");
    private Grid<Drink> drinkGrid;
    private DrinkForm drinkForm;
    private Drink selectedDrink = null;
    private Label validationErrorLabel;
    private boolean editingNewDrink = false;

    /**
     * The DrinkManagementView function is responsible for creating the view that
     * allows users to manage drinks.
     * It creates a button that allows users to create new drinks, and it also
     * creates a grid of all existing drinks.
     * The grid contains buttons for editing and deleting each drink, as well as
     * buttons for adding or removing variants from each drink.
     *
     * @param drinkRepository
     * @see #createNewDrinkButton()
     * @see #createDrinkGridFormLayout()
     * @see #updateEditDrinkLayoutState()
     */
    @Autowired
    public DrinkManagementView(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
        setSizeFull();
        Button newDrinkButton = createNewDrinkButton();
        HorizontalLayout drinkGridFormLayout = createDrinkGridFormLayout();
        add(newDrinkButton, drinkGridFormLayout);
        updateEditDrinkLayoutState();
    }

    /**
     * The createNewDrinkButton function creates a new Button object with the text
     * &quot;Neues Getränk hinzufügen&quot;.
     * The button is used to create new drinks.
     *
     * @return A button
     * @see #updateEditDrinkLayoutState()
     */
    private Button createNewDrinkButton() {
        Button button = new Button("Neues Getränk hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            drinkGrid.deselectAll();
            selectedDrink = new Drink();
            drinkBinder.readBean(selectedDrink);
            saveAndOpenDrinkVariantButton.setEnabled(true);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            editingNewDrink = true;
            updateEditDrinkLayoutState();
            clearNumberFieldChildren(drinkForm.getChildren());
        });
        return button;
    }

    /**
     * The updateEditDrinkLayoutState function is used to update the state of the
     * editDrinkLayout.
     * It sets the validationErrorLabel text to an empty string and enables or
     * disables all children of drinkForm depending on whether a drink has been
     * selected.
     */
    private void updateEditDrinkLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(drinkForm.getChildren(), selectedDrink != null);
    }

    /**
     * The createDrinkGridFormLayout function creates a HorizontalLayout that
     * contains the drinkGrid and the drinkForm.
     * The layout is set to full size, so it will fill up all available space in its
     * parent component.
     *
     * @return A horizontallayout
     */
    private HorizontalLayout createDrinkGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        drinkGrid = createDrinkGrid();
        layout.add(drinkGrid, createDrinkFormLayout());
        return layout;
    }

    /**
     * The createDrinkFormLayout function creates a layout for the drink form.
     *
     * @return A verticallayout
     */
    private VerticalLayout createDrinkFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("25%");
        drinkForm = new DrinkForm(drinkBinder);
        layout.add(drinkForm, createValidationLabelLayout(), createButton());
        return layout;
    }

    /**
     * The createValidationLabelLayout function creates a VerticalLayout that
     * contains the validationErrorLabel.
     * The validationErrorLabel is used to display error messages when the user
     * tries to save an invalid DrinkForm.
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
     * @return True if the writing was successful, false otherwise
     */
    private boolean writeBean() {
        try {
            drinkBinder.writeBean(selectedDrink);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    /**
     * The createButton function creates a button that allows the user to save and
     * open a drink variant.
     *
     * @return A vertical layout with two buttons, the savebutton and the
     * cancelbutton
     * @see #writeBean()
     * @see #validateDrinkSave()
     * @see #saveToDbAndUpdateDrink()
     */
    private Component createButton() {
        VerticalLayout buttonOrderLayout = new VerticalLayout();
        buttonOrderLayout.setWidthFull();
        buttonOrderLayout.setPadding(false);
        saveAndOpenDrinkVariantButton.setEnabled(false);
        saveAndOpenDrinkVariantButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveAndOpenDrinkVariantButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();

        saveButton.setEnabled(false);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        cancelButton.setEnabled(false);
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        saveAndOpenDrinkVariantButton.addClickListener(clickEvent -> {
            Drink undeditedDrink = new Drink(selectedDrink);
            if (writeBean()) {
                if (validateDrinkSave()) {
                    Drink drinkDummy = selectedDrink;
                    saveToDbAndUpdateDrink();
                    UI.getCurrent().navigate(DrinkVariantManagementView.class).ifPresent(view -> {
                        DrinkVariant drinkVariant = new DrinkVariant();
                        drinkVariant.setDrink(drinkDummy);
                        view.setSelectedDrinkVariant(drinkVariant);
                    });
                } else {
                    selectedDrink.copyValuesOf(undeditedDrink);
                }
            }
        });

        saveButton.addClickListener(clickEvent -> {
            Drink undeditedDrink = new Drink(selectedDrink);
            if (writeBean()) {
                if (validateDrinkSave()) {
                    saveToDbAndUpdateDrink();
                } else {
                    selectedDrink.copyValuesOf(undeditedDrink);
                }
            }
        });

        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        buttonOrderLayout.add(buttonLayout, saveAndOpenDrinkVariantButton);
        buttonOrderLayout.setFlexGrow(1, saveAndOpenDrinkVariantButton);

        return buttonOrderLayout;
    }

    /**
     * The saveToDbAndUpdateDrink function saves the selected drink to the database
     * and updates
     * the grid. If a new drink is being edited, it will be added to the grid.
     * Otherwise,
     * it will refresh that item in the grid.
     *
     * @see #resetEditLayout()
     */
    private void saveToDbAndUpdateDrink() {
        drinkRepository.save(selectedDrink);
        if (editingNewDrink) {
            drinkGrid.getListDataView().addItem(selectedDrink);
        } else {
            drinkGrid.getListDataView().refreshItem(selectedDrink);
        }
        resetEditLayout();
        Notifications.showInfo("Getränk gespeichert");
    }

    /**
     * The resetEditLayout function is used to reset the edit layout of the drink
     * view.
     * It deselects all drinks in the grid, sets selectedDrink to null, sets
     * editingNewDrink to false and disables both save buttons.
     * Then it creates a new Drink object with one DrinkVariant and reads this into
     * the binder.
     * Finally, it updates the state of edit drink layout and clears all number
     * fields in children of drinkForm (which are not visible).
     */
    private void resetEditLayout() {
        drinkGrid.deselectAll();
        selectedDrink = null;
        editingNewDrink = false;
        saveAndOpenDrinkVariantButton.setEnabled(false);
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);

        Drink drink = new Drink();
        drink.addDrinkVariant(new DrinkVariant());
        drinkBinder.readBean(drink);

        updateEditDrinkLayoutState();
        clearNumberFieldChildren(drinkForm.getChildren());
    }

    /**
     * The validateDrinkSave function checks if the selected drink is already in the
     * database.
     * If it is, then it removes that drink from the set of drinks and checks if
     * there are any other drinks with that name.
     * If there are no other drinks with that name, then we can save this new drink
     * to our database.
     *
     * @return A boolean
     */
    private boolean validateDrinkSave() {
        Optional<Drink> dbDrink = drinkRepository.findById(selectedDrink.getId());
        // name duplicate check
        Set<String> drinkNameSet = drinkRepository.findAllNames();
        dbDrink.ifPresent(drink -> {
            drinkNameSet.remove(drink.getName());
        });
        if (drinkNameSet.contains(selectedDrink.getName())) {
            validationErrorLabel.setText("Ein Getränk mit diesem Namen existiert bereits");
            return false;
        }
        return true;
    }

    /**
     * The createDrinkGrid function creates a Grid of Drink objects.
     * The grid is populated with all the drinks in the database, and each column
     * can be filtered by its respective value.
     * The grid also has a selection listener that enables/disables buttons
     * depending on whether an item is selected.
     *
     * @return A grid
     * @see #updateEditDrinkLayoutState()
     */
    private Grid<Drink> createDrinkGrid() {
        Grid<Drink> grid = new Grid<>(Drink.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<Drink> idColumn = grid.addColumn(Drink::getId).setHeader("ID");
        Grid.Column<Drink> nameColumn = grid.addColumn("name").setHeader("Name");
        Grid.Column<Drink> stockColumn = grid.addColumn("stockInMilliliters").setHeader("Bestand (ml)");
        Grid.Column<Drink> reorderPointColumn = grid.addColumn("reorderPoint").setHeader("Meldebestand (ml)");
        Grid.Column<Drink> activeColumn = grid.addColumn(drink -> drink.isActive() ? "Aktiv" : "Inaktiv")
                .setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");
        grid.setClassNameGenerator(drink -> {
            if (drink.getStockInMilliliters() < drink.getReorderPoint() && drink.isActive()) {
                return "highlighted";
            } else {
                return "normal";
            }
        });
        List<Drink> drinkList = drinkRepository.findAll();
        GridListDataView<Drink> dataView = grid.setItems(drinkList);

        DrinkManagementView.DrinkFilter drinkFilter = new DrinkManagementView.DrinkFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", drinkFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", drinkFilter::setName));
        headerRow.getCell(stockColumn).setComponent(createFilterHeaderInteger("Bestand", drinkFilter::setStock));
        headerRow.getCell(reorderPointColumn)
                .setComponent(createFilterHeaderInteger("Meldebestand", drinkFilter::setReorderPoint));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", drinkFilter::setActive));
        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Drink> optionalDrink = e.getFirstSelectedItem();
                saveButton.setEnabled(true);
                saveAndOpenDrinkVariantButton.setEnabled(true);
                cancelButton.setEnabled(true);
                if (optionalDrink.isPresent()) {
                    selectedDrink = optionalDrink.get();
                    drinkBinder.readBean(selectedDrink);
                    editingNewDrink = false;
                } else {
                    selectedDrink = null;
                    drinkBinder.readBean(null);
                    saveButton.setEnabled(false);
                    saveAndOpenDrinkVariantButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                }
            }
            updateEditDrinkLayoutState();
        });
        return grid;
    }

    private static class DrinkFilter {
        private final GridListDataView<Drink> dataView;
        private String id;
        private String name;
        private String stock;
        private String reorderPoint;
        private Boolean active;

        /**
         * The DrinkFilter function is used to filter the drinks in the grid.
         * It filters by name, price and category.
         *
         * @param dataView
         */
        public DrinkFilter(GridListDataView<Drink> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * The test function is used to filter the grid.
         * It checks if the given drink matches all the filters.
         * If it does, it returns true and will be displayed in the grid.
         *
         * @param drink Pass the current drink object in the list to be tested against
         * @return True if all the fields match
         */
        public boolean test(Drink drink) {
            boolean matchesId = matches(String.valueOf(drink.getId()), id);
            boolean matchesName = matches(drink.getName(), name);
            boolean matchesStock = matches(String.valueOf(drink.getStockInMilliliters()), stock);
            boolean matchesReorderPoint = matches(String.valueOf(drink.getReorderPoint()), reorderPoint);
            boolean matchesActive = active == null || active == drink.isActive();
            return matchesId && matchesName && matchesStock && matchesReorderPoint && matchesActive;
        }

        /**
         * The matches function checks if the searchTerm is null or empty. If it is,
         * then we return true because
         * that means there's no filter and everything should be shown. Otherwise, we
         * check if the value contains
         * the searchTerm (ignoring case). If it does, then we return true as well. This
         * function will be used to
         * determine whether a drink should be displayed in our grid or not based on
         * what's typed into our filter field.
         *
         * @param value      Compare the value of the searchterm parameter
         * @param searchTerm Search for the value of a specific column
         * @return True if the searchterm is null or empty or is contained
         */
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        /**
         * The setId function is used to set the id of a drink.
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * The setName function sets the name of a drink.
         *
         * @param name
         */
        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        /**
         * The setStock function is used to set the stock of a drink.
         *
         * @param stock
         */
        public void setStock(String stock) {
            this.stock = stock;
            dataView.refreshAll();
        }

        /**
         * The setReorderPoint function sets the reorder point of a drink.
         *
         * @param reorderPoint
         */
        public void setReorderPoint(String reorderPoint) {
            this.reorderPoint = reorderPoint;
            dataView.refreshAll();
        }

        /**
         * The setActive function is used to set the active state of a drink.
         *
         * @param active
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}