package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.Component;
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
import de.softwareprojekt.bestbowl.jpa.entities.food.Food;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.articleForms.FoodForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Max Ziller
 */
@Route(value = "foodManagement", layout = MainView.class)
@PageTitle("Speisenverwaltung")
@RolesAllowed({UserRole.OWNER})
@CssImport(value = "./styles/styles.css", themeFor = "vaadin-grid")
public class FoodManagementView extends VerticalLayout {
    private final transient FoodRepository foodRepository;
    private final Binder<Food> foodBinder = new Binder<>();
    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");
    private Grid<Food> foodGrid;
    private FoodForm foodForm;
    private Food selectedFood = null;
    private Label validationErrorLabel;
    private boolean editingNewFood = false;

    /**
     * The FoodManagementView function is responsible for creating the
     * FoodManagementView.
     * It creates a newFoodButton, foodGridFormLayout and adds them to the view.
     * The updateEditFoodLayoutState function is called at the end of this function.
     *
     * @param foodRepository
     * @see #createNewFoodButton()
     * @see #createFoodGridFormLayout()
     * @see #updateEditFoodLayoutState()
     */
    @Autowired
    public FoodManagementView(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
        setSizeFull();
        Button newFoodButton = createNewFoodButton();
        HorizontalLayout foodGridFormLayout = createFoodGridFormLayout();
        add(newFoodButton, foodGridFormLayout);
        updateEditFoodLayoutState();
    }

    /**
     * The createNewFoodButton function creates a new Button object with the text
     * &quot;Neue Speise hinzufügen&quot; and adds it to the
     * foodGrid.
     *
     * @return A button
     * @see #updateEditFoodLayoutState()
     */
    private Button createNewFoodButton() {
        Button button = new Button("Neue Speise hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            foodGrid.deselectAll();
            selectedFood = new Food();
            foodBinder.readBean(selectedFood);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            editingNewFood = true;
            updateEditFoodLayoutState();
            clearNumberFieldChildren(foodForm.getChildren());
        });
        return button;
    }

    /**
     * The updateEditFoodLayoutState function is used to update the state of the
     * editFoodLayout.
     * It sets the validationErrorLabel text to an empty string and enables or
     * disables all children of foodForm depending on whether a Food object has been
     * selected.
     */
    private void updateEditFoodLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(foodForm.getChildren(), selectedFood != null);
    }

    /**
     * The createFoodGridFormLayout function creates a HorizontalLayout that
     * contains the foodGrid and the createFoodFormLayout.
     *
     * @return A horizontallayout
     * @see #createFoodGrid()
     * @see #createFoodFormLayout()
     */
    private HorizontalLayout createFoodGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        foodGrid = createFoodGrid();
        layout.add(foodGrid, createFoodFormLayout());
        return layout;
    }

    /**
     * The createFoodFormLayout function creates a VerticalLayout that contains the
     * FoodForm,
     * a Label for displaying validation errors and a Button to save the form.
     *
     * @return A verticallayout
     * @see #createValidationLabelLayout()
     * @see #createButton()
     */
    private VerticalLayout createFoodFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("25%");
        foodForm = new FoodForm(foodBinder);
        layout.add(foodForm, createValidationLabelLayout(), createButton());
        return layout;
    }

    /**
     * The createValidationLabelLayout function creates a VerticalLayout that
     * contains the validationErrorLabel.
     * The validationErrorLabel is used to display error messages when the user
     * tries to add an article with invalid data.
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
     * The writeBean function is used to write the values of a FoodForm into a Food
     * object.
     *
     * @return True if the FoodForm contains valid data, false otherwise.
     */
    private boolean writeBean() {
        try {
            foodBinder.writeBean(selectedFood);
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
     * saveButton and cancelButton. The saveButton is used to write the values of
     * the FoodForm into
     * selectedFood, validate them and then either update selectedFood in the
     * database or reset it to its previous state.
     * The cancel button resets all changes made by editing a food item.
     *
     * @return A horizontallayout
     * @see #writeBean()
     * @see #validateFoodSave()
     * @see #saveToDbAndUpdateFood()
     */
    private Component createButton() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        saveButton.addClickListener(clickEvent -> {
            Food uneditedFood = new Food(selectedFood);
            if (writeBean()) {
                if (validateFoodSave()) {
                    saveToDbAndUpdateFood();
                } else {
                    selectedFood.copyValuesOf(uneditedFood);
                }
            }
        });

        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        return buttonLayout;
    }

    /**
     * The saveToDbAndUpdateFood function saves the selectedFood to the database and
     * updates it in the foodGrid.
     * If a new Food is being edited, it will be added to the foodGrid. Otherwise,
     * only its values are updated.
     * After that, resetEditLayout() is called and a notification informing about
     * successful saving of data is shown.
     */
    private void saveToDbAndUpdateFood() {
        foodRepository.save(selectedFood);
        if (editingNewFood) {
            foodGrid.getListDataView().addItem(selectedFood);
        } else {
            foodGrid.getListDataView().refreshItem(selectedFood);
        }
        resetEditLayout();
        Notifications.showInfo("Speise gespeichert");
    }

    /**
     * The resetEditLayout function resets the edit layout to its default state.
     * This means that all fields are cleared, the save and cancel buttons are
     * disabled,
     * and the selected food is set to null. The function also updates the edit
     * layout's state.
     */
    private void resetEditLayout() {
        foodGrid.deselectAll();
        selectedFood = null;
        foodBinder.readBean(new Food());
        editingNewFood = false;
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        updateEditFoodLayoutState();
        clearNumberFieldChildren(foodForm.getChildren());
    }

    /**
     * The validateFoodSave function checks if the selectedFood is already in the
     * database.
     * If it is, then it removes that food from the set of all foods and checks if
     * there are any other foods with that name.
     * If there are no other foods with that name, then we can save this food to our
     * database.
     *
     * @return A boolean
     */
    private boolean validateFoodSave() {
        Optional<Food> dbFood = foodRepository.findById(selectedFood.getId());
        Set<String> foodNameSet = foodRepository.findAllNames();
        dbFood.ifPresent(food -> foodNameSet.remove(food.getName()));
        if (foodNameSet.contains(selectedFood.getName())) {
            validationErrorLabel.setText("Eine Speise mit diesem Namen existiert bereits");
            return false;
        }
        return true;
    }

    /**
     * The createFoodGrid function creates a Grid of Food objects.
     * The grid is populated with all the Food objects in the database.
     * The grid has columns for ID, Name, Stock (amount), Reorder Point (amount),
     * Price and Active status.
     * Each column can be sorted by clicking on its header cell and can be filtered
     * by entering text into its filter field.
     *
     * @return A grid
     * @see #updateEditFoodLayoutState()
     */
    private Grid<Food> createFoodGrid() {
        Grid<Food> grid = new Grid<>(Food.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<Food> idColumn = grid.addColumn(Food::getId).setHeader("ID");
        Grid.Column<Food> nameColumn = grid.addColumn("name").setHeader("Name");
        Grid.Column<Food> stockColumn = grid.addColumn("stock").setHeader("Bestand (Stück)");
        Grid.Column<Food> reorderPointColumn = grid.addColumn("reorderPoint").setHeader("Meldebestand (Stück)");
        Grid.Column<Food> priceColumn = grid.addColumn(food -> formatDouble(food.getPrice()) + "€")
                .setComparator(Comparator.comparingDouble(Food::getPrice)).setHeader("Preis");
        Grid.Column<Food> activeColumn = grid.addColumn(food -> food.isActive() ? "Aktiv" : "Inaktiv")
                .setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");
        grid.setClassNameGenerator(food -> {
            if (food.getStock() < food.getReorderPoint() && food.isActive()) {
                return "highlighted";
            } else {
                return "normal";
            }
        });
        List<Food> foodList = foodRepository.findAll();
        GridListDataView<Food> dataView = grid.setItems(foodList);

        FoodManagementView.FoodFilter foodFilter = new FoodManagementView.FoodFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", foodFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", foodFilter::setName));
        headerRow.getCell(stockColumn).setComponent(createFilterHeaderInteger("Bestand", foodFilter::setStock));
        headerRow.getCell(reorderPointColumn)
                .setComponent(createFilterHeaderInteger("Meldebestand", foodFilter::setReorderPoint));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderString("Preis", foodFilter::setPrice));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", foodFilter::setActive));

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Food> optionalFood = e.getFirstSelectedItem();
                saveButton.setEnabled(true);
                cancelButton.setEnabled(true);
                if (optionalFood.isPresent()) {
                    selectedFood = optionalFood.get();
                    foodBinder.readBean(selectedFood);
                    editingNewFood = false;
                } else {
                    selectedFood = null;
                    foodBinder.readBean(null);
                    saveButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                }
            }
            updateEditFoodLayoutState();
        });
        return grid;
    }

    private static class FoodFilter {
        private final GridListDataView<Food> dataView;
        private String id;
        private String name;
        private String stock;
        private String reorderPoint;
        private String price;
        private Boolean active;

        /**
         * The FoodFilter function is used to filter the GridListDataView of Food
         * objects.
         * It takes a Food object as an argument and returns true if it matches the
         * criteria, false otherwise.
         * The criteria are defined by the user in a TextField and can be changed at any
         * time.
         *
         * @param dataView
         */
        public FoodFilter(GridListDataView<Food> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * The test function checks if the given food matches all the filter
         * criteria.
         *
         * @param food
         * @return A boolean value
         */
        public boolean test(Food food) {
            boolean matchesId = matches(String.valueOf(food.getId()), id);
            boolean matchesName = matches(food.getName(), name);
            boolean matchesStock = matches(String.valueOf(food.getStock()), stock);
            boolean matchesReorderPoint = matches(String.valueOf(food.getReorderPoint()), reorderPoint);
            boolean matchesPrice = matches(formatDouble(food.getPrice()) + "€", price);
            boolean matchesActive = active == null || active == food.isActive();
            return matchesId && matchesName && matchesStock && matchesReorderPoint && matchesPrice && matchesActive;
        }

        /**
         * The matches function checks if the searchTerm is null or empty. If it is,
         * then we return true because
         * that means there's no filter and everything should be shown. Otherwise, we
         * check if the value contains
         * the searchTerm (ignoring case). If it does, then we return true to show this
         * item in our grid.
         *
         * @param value      Store the value of the text field
         * @param searchTerm Search for a specific value in the list
         * @return True if the searchterm is null or empty, otherwise it returns whether
         * value contains the search term
         */
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        /**
         * The setId function is used to set the id of a food item.
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * The setName function sets the name of a food item.
         *
         * @param name
         */
        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        /**
         * The setStock function is used to update the stock of a food item.
         *
         * @param stock
         */
        public void setStock(String stock) {
            this.stock = stock;
            dataView.refreshAll();
        }

        /**
         * The setReorderPoint function is used to set the reorder point of a food item.
         *
         * @param reorderPoint
         */
        public void setReorderPoint(String reorderPoint) {
            this.reorderPoint = reorderPoint;
            dataView.refreshAll();
        }

        /**
         * The setPrice function sets the price of a food item.
         *
         * @param price
         */
        public void setPrice(String price) {
            this.price = price;
            dataView.refreshAll();
        }

        /**
         * The setActive function is used to set the active state of a food item.
         *
         * @param active
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}