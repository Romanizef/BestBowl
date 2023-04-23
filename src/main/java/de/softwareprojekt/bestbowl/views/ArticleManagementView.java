package de.softwareprojekt.bestbowl.views;


import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.form.FoodForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createFilterHeaderBoolean;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createFilterHeaderString;

/**
 * @author Max Ziller
 */

@Route(value = "Artikel-Management", layout = MainView.class)
@PageTitle("Artikel Management")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class ArticleManagementView extends VerticalLayout {
    private final FoodRepository foodRepository;
    private final Binder<Food> binder = new Binder<>();
    private Grid<Food> foodGrid;
    VerticalLayout foodTabSheet = new VerticalLayout();
    FoodForm foodForm;
    private FormLayout editFoodForm;
    private Food selectedFood = null;

    VerticalLayout drinkTabSheet = new VerticalLayout();
    VerticalLayout shoeTabSheet = new VerticalLayout();


    @Autowired
    public ArticleManagementView(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
        setSizeFull();
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.add("Speisen", foodTabSheet);
        tabSheet.add("Getränke", drinkTabSheet);
        tabSheet.add("Schuhe", shoeTabSheet);
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_BORDERED);
        add(tabSheet);


        Button newFoodButton = createNewFoodButton();
        HorizontalLayout foodGridFormLayout = createFoodGridFormLayout();
        foodTabSheet.setSizeFull();
        foodTabSheet.add(newFoodButton, foodGridFormLayout);


        drinkTabSheet.setSizeFull();
        drinkTabSheet.add(new H1("Test"));

        shoeTabSheet.setSizeFull();
        shoeTabSheet.add(new H1("Test2"));
    }


    private Button createNewFoodButton() {
        Button button = new Button("Neue Speise hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            foodGrid.deselectAll();
            selectedFood = new Food();
            binder.readBean(selectedFood);
            updateEditLayoutVisibility();
        });
        return button;
    }

    private void updateEditLayoutVisibility() {
        if (selectedFood == null) {
            editFoodForm.getChildren().forEach(component -> {
                if (component instanceof HasEnabled c) {
                    c.setEnabled(false);
                }
            });
        } else {
            editFoodForm.getChildren().forEach(component -> {
                if (component instanceof HasEnabled c) {
                    c.setEnabled(true);
                }
            });
        }
    }
    private HorizontalLayout createFoodGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        foodForm = new FoodForm();
        layout.setSizeFull();
        foodGrid = createFoodGrid();
        editFoodForm = foodForm;
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
        foodGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        foodGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        foodGrid.setWidth("75%");
        foodGrid.setHeight("100%");
        List<Food> foodList = foodRepository.findAll();
        GridListDataView<Food> dataView = foodGrid.setItems(foodList);
        FoodFilter foodFilter = new FoodFilter(dataView);
        foodGrid.getHeaderRows().clear();
        HeaderRow headerRow = foodGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderString("ID", foodFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", foodFilter::setName));
        headerRow.getCell(stockColumn).setComponent(createFilterHeaderString("Bestand", foodFilter::setStock));
        headerRow.getCell(reorderPointColumn).setComponent(createFilterHeaderString("Meldebestand", foodFilter::setReorderPoint));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderString("Preis", foodFilter::setPrice));
        headerRow.getCell(activeColumn).setComponent(createFilterHeaderBoolean(foodFilter::setActive, true));
        foodFilter.setActive(true);
        foodGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Food> optionalFood = e.getFirstSelectedItem();
                if (optionalFood.isPresent()) {
                    selectedFood = optionalFood.get();
                    binder.readBean(selectedFood);
                } else {
                    selectedFood = null;
                    binder.readBean(new Food());
                }
            }
            updateEditLayoutVisibility();
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
        private boolean active;

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
            boolean matchesActive = food.isActive() == active;
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

        public void setActive(boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
