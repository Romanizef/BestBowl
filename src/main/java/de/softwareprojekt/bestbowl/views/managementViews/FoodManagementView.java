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

import de.softwareprojekt.bestbowl.jpa.entities.foodEntities.Food;
import de.softwareprojekt.bestbowl.jpa.repositories.foodRepos.FoodRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.articleForms.FoodForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

@Route(value = "foodManagement", layout = MainView.class)
@PageTitle("Speiesenverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
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

    @Autowired
    public FoodManagementView(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
        setSizeFull();
        Button newFoodButton = createNewFoodButton();
        HorizontalLayout foodGridFormLayout = createFoodGridFormLayout();
        add(newFoodButton, foodGridFormLayout);
        updateEditFoodLayoutState();
    }

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
            editingNewFood = false;
            updateEditFoodLayoutState();
            clearNumberFieldChildren(foodForm.getChildren());
        });
        return button;
    }

    private void updateEditFoodLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(foodForm.getChildren(), selectedFood != null);
    }

    private HorizontalLayout createFoodGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        foodGrid = createFoodGrid();
        layout.add(foodGrid, createFoodFormLayout());
        return layout;
    }

    private VerticalLayout createFoodFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("25%");
        foodForm = new FoodForm(foodBinder);
        layout.add(foodForm, createValidationLabelLayout(), createButton());
        return layout;
    }

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

    private boolean validateFoodSave() {
        Optional<Food> dbFood = foodRepository.findById(selectedFood.getId());
        Set<String> foodNameSet = foodRepository.findAllNames();
        dbFood.ifPresent(food -> {
            foodNameSet.remove(food.getName());
        });
        if (foodNameSet.contains(selectedFood.getName())) {
            validationErrorLabel.setText("Eine Speise mit diesem Namen existiert bereits");
            return false;
        }
        return true;
    }


    private Grid<Food> createFoodGrid() {
        Grid<Food> foodGrid = new Grid<>(Food.class);
        foodGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        foodGrid.removeAllColumns();
        Grid.Column<Food> idColumn = foodGrid.addColumn(Food::getId).setHeader("ID");
        Grid.Column<Food> nameColumn = foodGrid.addColumn("name").setHeader("Name");
        Grid.Column<Food> stockColumn = foodGrid.addColumn("stock").setHeader("Bestand (Stück)");
        Grid.Column<Food> reorderPointColumn = foodGrid.addColumn("reorderPoint").setHeader("Meldebestand (Stück)");
        Grid.Column<Food> priceColumn = foodGrid.addColumn(food -> formatDouble(food.getPrice()) + "€").setHeader("Preis");
        Grid.Column<Food> activeColumn = foodGrid.addColumn(food -> food.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        foodGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        foodGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        foodGrid.setWidth("75%");
        foodGrid.setHeight("100%");
        List<Food> foodList = foodRepository.findAll();
        GridListDataView<Food> dataView = foodGrid.setItems(foodList);

        FoodManagementView.FoodFilter foodFilter = new FoodManagementView.FoodFilter(dataView);
        foodGrid.getHeaderRows().clear();
        HeaderRow headerRow = foodGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", foodFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", foodFilter::setName));
        headerRow.getCell(stockColumn).setComponent(createFilterHeaderInteger("Bestand", foodFilter::setStock));
        headerRow.getCell(reorderPointColumn).setComponent(createFilterHeaderInteger("Meldebestand", foodFilter::setReorderPoint));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderString("Preis", foodFilter::setPrice));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", foodFilter::setActive));

        foodGrid.addSelectionListener(e -> {
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
        return foodGrid;
    }

    private static class FoodFilter {
        private final GridListDataView<Food> dataView;
        private String id;
        private String name;
        private String stock;
        private String reorderPoint;
        private String price;
        private Boolean active;

        public FoodFilter(GridListDataView<Food> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(Food food) {
            boolean matchesId = matches(String.valueOf(food.getId()), id);
            boolean matchesName = matches(food.getName(), name);
            boolean matchesStock = matches(String.valueOf(food.getStock()), stock);
            boolean matchesReorderPoint = matches(String.valueOf(food.getReorderPoint()), reorderPoint);
            boolean matchesPrice = matches(formatDouble(food.getPrice()) + "€", price);
            boolean matchesActive = active == null || active == food.isActive();
            return matchesId && matchesName && matchesStock && matchesReorderPoint && matchesPrice && matchesActive;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        public void setStock(String stock) {
            this.stock = stock;
            dataView.refreshAll();
        }

        public void setReorderPoint(String reorderPoint) {
            this.reorderPoint = reorderPoint;
            dataView.refreshAll();
        }

        public void setPrice(String price) {
            this.price = price;
            dataView.refreshAll();
        }

        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }

}
