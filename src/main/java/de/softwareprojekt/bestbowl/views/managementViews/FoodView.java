package de.softwareprojekt.bestbowl.views.managementViews;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.form.FoodForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createFilterHeaderBoolean;

@Route(value = "FoodManagement", layout = MainView.class)
@PageTitle("Speiesenverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class FoodView extends VerticalLayout {
    private final FoodRepository foodRepository;
    private final Binder<Food> foodBinder = new Binder<>();
    private Grid<Food> foodGrid;
    private FoodForm foodForm;
    private Food selectedFood = null;

    @Autowired
    public FoodView(FoodRepository foodRepository){
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
            updateEditFoodLayoutState();
        });
        return button;
    }

    private void updateEditFoodLayoutState() {
        setChildrenEnabled(foodForm.getChildren(), selectedFood != null);
        if (selectedFood == null) {
            setValueForIntegerFieldChildren(foodForm.getChildren(), null);
        }
    }

    private HorizontalLayout createFoodGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        foodForm = new FoodForm(foodBinder);
        layout.setSizeFull();
        foodGrid = createFoodGrid();
        layout.add(foodGrid, foodForm);
        return layout;
    }

    private Grid<Food> createFoodGrid() {
        Grid<Food> foodGrid = new Grid<>(Food.class);
        foodGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        foodGrid.removeAllColumns();
        Grid.Column<Food> idColumn = foodGrid.addColumn(Food::getId).setHeader("ID");
        Grid.Column<Food> nameColumn = foodGrid.addColumn("name").setHeader("Name");
        Grid.Column<Food> stockColumn = foodGrid.addColumn("stock").setHeader("Bestand");
        Grid.Column<Food> reorderPointColumn = foodGrid.addColumn("reorderPoint").setHeader("Meldebestand");
        Grid.Column<Food> priceColumn = foodGrid.addColumn("price").setHeader("Preis");
        Grid.Column<Food> activeColumn = foodGrid.addColumn("active").setHeader("Aktiv");
        foodGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        foodGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        foodGrid.setWidth("75%");
        foodGrid.setHeight("100%");
        List<Food> foodList = foodRepository.findAll();
        GridListDataView<Food> dataView = foodGrid.setItems(foodList);

        FoodView.FoodFilter foodFilter = new FoodView.FoodFilter(dataView);
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
                if (optionalFood.isPresent()) {
                    selectedFood = optionalFood.get();
                    foodBinder.readBean(selectedFood);
                } else {
                    selectedFood = null;
                    foodBinder.readBean(new Food());
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
            boolean matchesPrice = matches(String.valueOf(food.getPrice()), price);
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
