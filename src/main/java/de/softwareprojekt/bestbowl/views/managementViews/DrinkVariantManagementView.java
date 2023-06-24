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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkVariantRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.validators.article.DrinkVariantValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Max Ziller
 */
@Route(value = "drinkVariantManagement", layout = MainView.class)
@PageTitle("Getränkevariantenverwaltung")
@RolesAllowed({ UserRole.OWNER })
public class DrinkVariantManagementView extends VerticalLayout {
    private final transient DrinkVariantRepository drinkVariantRepository;
    private final Binder<DrinkVariant> drinkVariantBinder = new Binder<>();
    private final Binder<Drink> drinkBinder = new Binder<>();
    private Grid<DrinkVariant> drinkVariantGrid;
    private FormLayout drinkVariantForm;
    private Select<Drink> drinkSelect;
    private DrinkVariant selectedDrinkVariant = null;
    private Label validationErrorLabel;
    private boolean editingNewDrinkVariant = false;

    /**
     * The DrinkVariantManagementView function is responsible for creating the view
     * that allows the user to manage drink variants.
     * The DrinkVariantManagementView function creates a new button, which allows
     * the user to create a new drink variant.
     * The DrinkVariantManagementView function also creates a grid form layout,
     * which displays all the existing drink variants and their properties in an
     * organized manner.
     *
     * @param drinkVariantRepository
     * @see #createNewDrinkVariantButton()
     * @see #createGridFormLayout()
     * @see #updateEditDrinkVariantLayoutState()
     */
    @Autowired
    public DrinkVariantManagementView(DrinkVariantRepository drinkVariantRepository) {
        this.drinkVariantRepository = drinkVariantRepository;
        setSizeFull();
        Button newDrinkVariantButton = createNewDrinkVariantButton();
        add(newDrinkVariantButton, createGridFormLayout());
        updateEditDrinkVariantLayoutState();
    }

    /**
     * The setSelectedDrinkVariant function sets the selectedDrinkVariant variable
     * to the DrinkVariant object passed in as a parameter.
     * It also updates the state of the EditDrinkVariantLayout and reads in data
     * from drinkVariantBinder.
     *
     * @param selectedDrinkVariant
     */
    public void setSelectedDrinkVariant(DrinkVariant selectedDrinkVariant) {
        this.selectedDrinkVariant = selectedDrinkVariant;
        updateEditDrinkVariantLayoutState();
        drinkVariantBinder.readBean(selectedDrinkVariant);
    }

    /**
     * The createNewDrinkVariantButton function creates a new Button object with the
     * text &quot;Neue Getränkevariante hinzufügen&quot; and sets its width to 100%.
     * It adds the LUMO_PRIMARY theme variant to it.
     * When clicked, it deselects all items in drinkVariantGrid, creates a new
     * DrinkVariant object and assigns it to selectedDrinkVariant.
     * The drinkSelect is set to NO_DRINK (which is an enum constant of type
     * Drink).
     *
     * @return A button
     */
    private Button createNewDrinkVariantButton() {
        Button button = new Button("Neue Getränkevariante hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            drinkVariantGrid.deselectAll();
            selectedDrinkVariant = new DrinkVariant();
            drinkVariantBinder.readBean(selectedDrinkVariant);
            editingNewDrinkVariant = true;
            updateEditDrinkVariantLayoutState();
            clearNumberFieldChildren(drinkVariantForm.getChildren());
        });
        return button;
    }

    /**
     * The updateEditDrinkVariantLayoutState function is used to update the state of
     * the edit drink variant layout.
     * It sets the text of a validation error label and enables or disables all
     * children in a form layout.
     */
    private void updateEditDrinkVariantLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(drinkVariantForm.getChildren(), selectedDrinkVariant != null);
    }

    /**
     * The createGridFormLayout function creates a HorizontalLayout, which contains
     * the drinkVariantGrid and the drinkVariantForm.
     * The layout is set to full size.
     *
     * @return A horizontallayout
     * @see #createDrinkVariantGrid()
     * @see #drinkVariantForm()
     */
    private HorizontalLayout createGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        drinkVariantGrid = createDrinkVariantGrid();
        drinkVariantForm = drinkVariantForm();
        layout.add(drinkVariantGrid, drinkVariantForm);
        return layout;
    }

    /**
     * The drinkVariantForm function creates a FormLayout that is used to edit the
     * selected DrinkVariant.
     * The form contains a ComboBox for selecting the Drink, an IntegerField for
     * entering the variant in ml and
     * a NumberField for entering the price of this variant. It also contains two
     * buttons: one to save changes and one to cancel them.
     *
     * @return A formlayout
     * @see #writeBean()
     * @see #validateDrinkVariantSave()
     * @see #saveToDbAndUpdateDrinkVariant()
     */
    private FormLayout drinkVariantForm() {
        FormLayout drinkVariantLayout = new FormLayout();
        drinkVariantLayout.setWidth("25%");

        drinkSelect = createDrinkSelect();

        NumberField priceField = new NumberField("Preis");
        IntegerField variantField = new IntegerField("Variante");

        Button saveButton = new Button("Speichern");
        Button cancelButton = new Button("Abbrechen");

        variantField.setWidthFull();
        variantField.setSuffixComponent(new Span("ml"));
        variantField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        variantField.setRequired(true);

        priceField.setWidthFull();
        priceField.setSuffixComponent(new Span("EUR"));
        priceField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        priceField.setRequired(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        saveButton.addClickListener(clickEvent -> {
            DrinkVariant undeditedDrinkVariant = new DrinkVariant(selectedDrinkVariant);
            if (writeBean()) {
                if (validateDrinkVariantSave()) {
                    saveToDbAndUpdateDrinkVariant();
                } else {
                    selectedDrinkVariant.copyValueOf(undeditedDrinkVariant);
                }
            }
        });
        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);

        drinkVariantLayout.add(drinkSelect, variantField, priceField, checkboxLayout, createValidationLabelLayout(),
                buttonLayout);

        drinkVariantBinder.withValidator(new DrinkVariantValidator());
        drinkVariantBinder.bind(drinkSelect, DrinkVariant::getDrink,
                (drinkVariant, drink) -> drinkVariant.setDrink(drink));
        drinkVariantBinder.bind(priceField, DrinkVariant::getPrice,
                (drinkVariant, price) -> drinkVariant.setPrice(Objects.requireNonNullElse(price, 0.0)));
        drinkVariantBinder.bind(variantField, DrinkVariant::getMl,
                (drinkVariant, ml) -> drinkVariant.setMl(Objects.requireNonNullElse(ml, 0)));
        drinkVariantBinder.bind(activeCheckbox, DrinkVariant::isActive,
                (drinkVariant, active) -> drinkVariant.setActive(Objects.requireNonNullElse(active, false)));
        return drinkVariantLayout;
    }

    /**
     * The createDrinkSelect function creates a Select for the Drink entity.
     * It is used in the createDrinkVariantForm function to add a drink to a new
     * DrinkVariant.
     *
     * @return A Select
     */
    private Select<Drink> createDrinkSelect() {
        Select<Drink> select = new Select<>();
        select.setLabel("Getränk");

        List<Drink> drinkList = Repos.getDrinkRepository().findAll();
        Set<Drink> drinkSet = new HashSet<>(drinkList.size() + 1);
        drinkSet.addAll(drinkList);
        ListDataProvider<Drink> dataProvider = new ListDataProvider<>(drinkSet);
        dataProvider
                .setSortComparator((SerializableComparator<Drink>) (o1, o2) -> o1.getName().compareTo(o2.getName()));

        select.setWidthFull();
        select.setItems(dataProvider);
        select.setPlaceholder("-");
        select.setEmptySelectionAllowed(false);
        // select.setItemLabelGenerator(Drink::getName);
        select.setItemLabelGenerator(drink -> {
            if (drink == null) {
                return "kein Getränk";
            }
            return drink.getName();
        });
        select.setRequiredIndicatorVisible(true);
        select.addThemeVariants(SelectVariant.LUMO_SMALL);

        return select;
    }

    /**
     * The createValidationLabelLayout function creates a VerticalLayout that
     * contains the validationErrorLabel.
     * The validationErrorLabel is used to display error messages when the user
     * tries to save an invalid DrinkVariant.
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
     * The writeBean function is used to write the values of a DrinkVariant object
     * into the form fields.
     *
     * @return True if the DrinkVariant was successfully written to the form fields,
     *         false otherwise
     */
    private boolean writeBean() {
        try {
            drinkVariantBinder.writeBean(selectedDrinkVariant);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    /**
     * The saveToDbAndUpdateDrinkVariant function saves the selected drink variant
     * to the database and updates
     * the grid view. If a new drink variant is being edited, it will be added to
     * the grid view. Otherwise,
     * if an existing drink variant is being edited, it will be refreshed in place
     * in the grid view. Finally,
     * this function resets all fields of editLayout and shows a notification that
     * informs about successful saving.
     *
     * @see #resetEditLayout()
     */
    private void saveToDbAndUpdateDrinkVariant() {
        drinkVariantRepository.save(selectedDrinkVariant);
        if (editingNewDrinkVariant) {
            drinkVariantGrid.getListDataView().addItem(selectedDrinkVariant);
        } else {
            drinkVariantGrid.getListDataView().refreshItem(selectedDrinkVariant);
        }
        resetEditLayout();
        Notifications.showInfo("Getränkevariante gespeichert");
    }

    /**
     * The resetEditLayout function is used to reset the edit layout.
     * It deselects all items in the drinkVariantGrid, sets selectedDrinkVariant to
     * null, and sets editingNewDrinkVariant to false.
     * Then it creates a new Drink object with one Drink Variant and reads that into
     * the binder.
     * Finally, it updates the state of Edit Drinks Layout and clears all number
     * fields in drink variant form children.
     *
     * @see #updateEditDrinkVariantLayoutState()
     */
    private void resetEditLayout() {
        drinkVariantGrid.deselectAll();
        selectedDrinkVariant = null;
        editingNewDrinkVariant = false;

        Drink drink = new Drink();
        drink.addDrinkVariant(new DrinkVariant());
        drinkBinder.readBean(drink);

        updateEditDrinkVariantLayoutState();
        clearNumberFieldChildren(drinkVariantForm.getChildren());
    }

    /**
     * The validateDrinkVariantSave function checks if the selected drink variant is
     * valid.
     * It does so by checking if a drink has been selected, and whether there
     * are any duplicate variants in the database.
     * If either of these conditions are met, it returns false; otherwise it returns
     * true.
     *
     * @return True if the selecteddrinkvariant
     */
    private boolean validateDrinkVariantSave() {
        if (selectedDrinkVariant.getDrink() == null) {
            validationErrorLabel.setText("Ein Getränk muss ausgewählt sein!");
            return false;
        }

        Optional<DrinkVariant> dbDrinkVariant = drinkVariantRepository.findById(selectedDrinkVariant.getId());

        Set<Integer> drinkVariantMlSet = drinkVariantRepository.findAllMlForDrink(selectedDrinkVariant.getDrink());
        dbDrinkVariant.ifPresent(drinkVariant -> drinkVariantMlSet.remove(drinkVariant.getMl()));
        if (drinkVariantMlSet.contains(selectedDrinkVariant.getMl())) {
            validationErrorLabel.setText("Ein Getränkevariante mit dieser größe existiert bereits");
            return false;
        }
        return true;
    }

    /**
     * The createDrinkVariantGrid function creates a grid that displays all
     * DrinkVariants in the database.
     * The grid is filterable and sortable by each column.
     *
     * @return A grid
     * @see #updateEditDrinkVariantLayoutState()
     */
    private Grid<DrinkVariant> createDrinkVariantGrid() {
        Grid<DrinkVariant> grid = new Grid<>(DrinkVariant.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<DrinkVariant> idColumn = grid.addColumn(DrinkVariant::getId).setHeader("ID");
        Grid.Column<DrinkVariant> nameColum = grid.addColumn(drinkVariant -> drinkVariant.getDrink().getName())
                .setHeader("Getränk");
        Grid.Column<DrinkVariant> mlColumn = grid.addColumn(DrinkVariant::getMl).setHeader("Variante (ml)");
        Grid.Column<DrinkVariant> priceColumn = grid
                .addColumn(drinkVariant -> formatDouble(drinkVariant.getPrice()) + "€").setHeader("Preis");
        Grid.Column<DrinkVariant> activeColumn = grid
                .addColumn(drinkVariant -> drinkVariant.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<DrinkVariant> drinkVariantList = drinkVariantRepository.findAll();
        GridListDataView<DrinkVariant> dataView = grid.setItems(drinkVariantList);
        dataView.setSortOrder(drinkVariant -> drinkVariant.getDrink().getName() + drinkVariant.getMl(),
                SortDirection.ASCENDING);

        DrinkVariantFilter drinkVariantFilter = new DrinkVariantFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", drinkVariantFilter::setId));
        headerRow.getCell(nameColum).setComponent(createFilterHeaderString("Getränk", drinkVariantFilter::setName));
        headerRow.getCell(mlColumn).setComponent(createFilterHeaderInteger("Variante", drinkVariantFilter::setMl));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderString("Preis", drinkVariantFilter::setPrice));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktive", "Inaktiv", drinkVariantFilter::setActive));

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<DrinkVariant> optionalDrinkVariant = e.getFirstSelectedItem();
                if (optionalDrinkVariant.isPresent()) {
                    selectedDrinkVariant = optionalDrinkVariant.get();
                    drinkVariantBinder.readBean(selectedDrinkVariant);
                    editingNewDrinkVariant = false;
                } else {
                    selectedDrinkVariant = null;
                    drinkVariantBinder.readBean(null);
                }
            }
            updateEditDrinkVariantLayoutState();
        });
        return grid;
    }

    private static class DrinkVariantFilter {
        private final GridListDataView<DrinkVariant> dataView;
        private String id;
        private String name;
        private String milliliter;
        private String price;
        private Boolean active;

        /**
         * The DrinkVariantFilter function is a filter function that filters the
         * DrinkVariant grid by name, price and amount.
         *
         * @param dataView
         */
        public DrinkVariantFilter(GridListDataView<DrinkVariant> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * The test function is used to filter the grid.
         * It checks if the given drinkVariant matches all the filters.
         *
         * @param drinkVariant
         * @return The result of the matches function
         */
        public boolean test(DrinkVariant drinkVariant) {
            boolean matchesId = matches(String.valueOf(drinkVariant.getId()), id);
            boolean matchesDrink = matches((drinkVariant.getDrink().getName()), name);
            boolean matchesMilliliter = matches(String.valueOf(drinkVariant.getMl()), milliliter);
            boolean matchesPrice = matches(formatDouble(drinkVariant.getPrice()) + "€", price);
            boolean matchesActive = active == null || active == drinkVariant.getDrink().isActive();
            return matchesId && matchesDrink && matchesMilliliter && matchesPrice && matchesActive;
        }

        /**
         * The matches function is used to filter the data in the grid.
         * It checks if a given value matches a search term.
         *
         * @param value      Check if the search term is null or empty
         * @param searchTerm Compare the value of the searchterm to a string value
         * @return True if the searchterm is null or empty,
         */
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        /**
         * The setId function sets the id of a drink variant.
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * The setName function sets the name of a drink variant.
         *
         * @param name
         */
        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        /**
         * The setMl function sets the milliliter variable to the given String.
         *
         * @param milliliter
         */
        public void setMl(String milliliter) {
            this.milliliter = milliliter;
            dataView.refreshAll();
        }

        /**
         * The setPrice function sets the price of a drink variant.
         *
         * @param price
         */
        public void setPrice(String price) {
            this.price = price;
            dataView.refreshAll();
        }

        /**
         * The setActive function is used to set the active state of a drink variant.
         *
         * @param active
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}