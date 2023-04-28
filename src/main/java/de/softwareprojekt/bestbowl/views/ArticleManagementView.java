package de.softwareprojekt.bestbowl.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkVariantRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.form.DrinkForm;
import de.softwareprojekt.bestbowl.views.form.FoodForm;
import de.softwareprojekt.bestbowl.views.form.ShoeForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Max Ziller
 */

@Route(value = "articleManagement", layout = MainView.class)
@PageTitle("Artikelverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class ArticleManagementView extends VerticalLayout {
    private final FoodRepository foodRepository;
    private final DrinkRepository drinkRepository;
    private final DrinkVariantRepository drinkVariantRepository;
    private final BowlingShoeRepository bowlingShoeRepository;
    private final Binder<Food> foodBinder = new Binder<>();
    private final Binder<Drink> drinkBinder = new Binder<>();
    private final Binder<DrinkVariant> drinkVariantBinder = new Binder<>();
    private final Binder<BowlingShoe> shoeBinder = new Binder<>();
    private final VerticalLayout foodTabSheet = new VerticalLayout();
    private final VerticalLayout drinkTabSheet = new VerticalLayout();
    private final VerticalLayout shoeTabSheet = new VerticalLayout();
    private Grid<Food> foodGrid;
    private Grid<Drink> drinkGrid;
    private Grid<DrinkVariant> drinkVariantGrid;
    private Grid<BowlingShoe> shoeGrid;
    private FoodForm foodForm;
    private DrinkForm drinkForm;
    private ShoeForm shoeForm;

    private FormLayout editFoodForm;
    private FormLayout editDrinkForm;
    private FormLayout editShoeForm;
    private Food selectedFood = null;
    private Drink selectedDrink = null;
    private BowlingShoe selectedShoe = null;


    @Autowired
    public ArticleManagementView(FoodRepository foodRepository, DrinkRepository drinkRepository,
                                 DrinkVariantRepository drinkVariantRepository,
                                 BowlingShoeRepository bowlingShoeRepository) {
        this.foodRepository = foodRepository;
        this.drinkRepository = drinkRepository;
        this.drinkVariantRepository = drinkVariantRepository;
        this.bowlingShoeRepository = bowlingShoeRepository;
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
        updateEditFoodLayoutState();

        Button newDrinkButton = createNewDrinkButton();
        HorizontalLayout drinkGridFormLayout = createDrinkGridFormLayout();
        drinkVariantGrid = createDrinkVariantGrid();
        drinkTabSheet.setSizeFull();
        drinkTabSheet.add(newDrinkButton, drinkGridFormLayout, drinkVariantGrid);
        updateEditDrinkLayoutState();

        Button newShoeButton = createNewShoeButton();
        HorizontalLayout shoeGridFormLayout = createShoeGridFormLayout();
        shoeTabSheet.setSizeFull();
        shoeTabSheet.add(newShoeButton, shoeGridFormLayout);
        updateEditShoeLayoutState();
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

    private Button createNewDrinkButton() {
        Button button = new Button("Neues Getränk hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            drinkGrid.deselectAll();
            selectedDrink = new Drink();
            drinkBinder.readBean(selectedDrink);
            updateEditDrinkLayoutState();
        });
        return button;
    }

    private Button createNewShoeButton() {
        Button button = new Button("Neue Schuhe hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            shoeGrid.deselectAll();
            selectedShoe = new BowlingShoe();
            shoeBinder.readBean(selectedShoe);
            updateEditShoeLayoutState();
        });
        return button;
    }

    private void updateEditFoodLayoutState() {
        setChildrenEnabled(foodForm.getChildren(), selectedFood != null);
        if (selectedFood == null) {
            setValueForIntegerFieldChildren(foodForm.getChildren(), null);
        }
    }

    private void updateEditDrinkLayoutState() {
        setChildrenEnabled(drinkForm.getChildren(), selectedDrink != null);
        if (selectedDrink == null) {
            setValueForIntegerFieldChildren(drinkForm.getChildren(), null);
        }
    }

    private void updateEditShoeLayoutState() {
        setChildrenEnabled(editShoeForm.getChildren(), selectedShoe != null);
        if (selectedShoe == null) {
            setValueForIntegerFieldChildren(shoeForm.getChildren(), null);
        }
    }

    private HorizontalLayout createFoodGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        foodForm = new FoodForm(foodBinder);
        layout.setSizeFull();
        foodGrid = createFoodGrid();
        editFoodForm = foodForm;
        layout.add(foodGrid, foodForm);
        return layout;
    }

    private HorizontalLayout createDrinkGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        drinkForm = new DrinkForm(drinkVariantBinder, drinkBinder);
        layout.setSizeFull();
        drinkGrid = createDrinkGrid();
        editDrinkForm = drinkForm;
        layout.add(drinkGrid, drinkForm);
        return layout;
    }

    private HorizontalLayout createShoeGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        shoeForm = new ShoeForm(shoeBinder);
        layout.setSizeFull();
        shoeGrid = createShoeGrid();
        editShoeForm = shoeForm;
        layout.add(shoeGrid, shoeForm);
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
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", foodFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", foodFilter::setName));
        headerRow.getCell(stockColumn).setComponent(createFilterHeaderInteger("Bestand", foodFilter::setStock));
        headerRow.getCell(reorderPointColumn).setComponent(createFilterHeaderInteger("Meldebestand", foodFilter::setReorderPoint));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderString("Preis", foodFilter::setPrice));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", foodFilter::setActive));
        foodFilter.setActive(true);
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

    private Grid<DrinkVariant> createDrinkVariantGrid() {
        Grid<DrinkVariant> drinkVariantGrid = new Grid<>(DrinkVariant.class);
        drinkVariantGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        drinkVariantGrid.removeAllColumns();
        Grid.Column<DrinkVariant> idColumn = drinkVariantGrid.addColumn(DrinkVariant::getId).setHeader("ID");
        drinkVariantGrid.addColumn(DrinkVariant -> DrinkVariant.getDrink() == null ? "" :
                DrinkVariant.getDrink().getName()).setHeader("Getränk");
        Grid.Column<DrinkVariant> mlColumn = drinkVariantGrid.addColumn(DrinkVariant::getMl).setHeader("Milliliter");
        Grid.Column<DrinkVariant> priceColumn = drinkVariantGrid.addColumn(DrinkVariant::getPrice).setHeader("Preis");
        drinkVariantGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        drinkVariantGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        drinkVariantGrid.setWidth("75%");
        drinkVariantGrid.setHeight("100%");

        List<DrinkVariant> drinkVariantList = drinkVariantRepository.findAll();
        GridListDataView<DrinkVariant> dataView = drinkVariantGrid.setItems(drinkVariantList);

        DrinkVariantFilter drinkVariantFilter = new DrinkVariantFilter(dataView);
        drinkVariantGrid.getHeaderRows().clear();
        HeaderRow headerRow = drinkVariantGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", drinkVariantFilter::setId));
        //headerRow.getCell(drinkVariantGrid).setComponent(createFilterHeaderString("Getränk", drinkVariantFilter::setName));
        //Filter Feld für GetränkeVariante funktioniert so noch nicht
        headerRow.getCell(mlColumn).setComponent(createFilterHeaderInteger("Variante", drinkVariantFilter::setMl));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderInteger("Preis", drinkVariantFilter::setPrice));

        return drinkVariantGrid;
    }

    private Grid<Drink> createDrinkGrid() {
        Grid<Drink> drinkGrid = new Grid<>(Drink.class);
        drinkGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        drinkGrid.removeAllColumns();
        Grid.Column<Drink> idColumn = drinkGrid.addColumn(Drink::getId).setHeader("ID");
        Grid.Column<Drink> nameColumn = drinkGrid.addColumn("name").setHeader("Name");
        Grid.Column<Drink> stockColumn = drinkGrid.addColumn("stockInMilliliters").setHeader("Bestand");
        Grid.Column<Drink> reorderPointColumn = drinkGrid.addColumn("reorderPoint").setHeader("Meldebestand");
        Grid.Column<Drink> activeColumn = drinkGrid.addColumn("active").setHeader("Aktiv");
        drinkGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        drinkGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        drinkGrid.setWidth("75%");
        drinkGrid.setHeight("100%");
        List<Drink> drinkList = drinkRepository.findAll();
        GridListDataView<Drink> dataView = drinkGrid.setItems(drinkList);
        DrinkFilter drinkFilter = new DrinkFilter(dataView);
        drinkGrid.getHeaderRows().clear();
        HeaderRow headerRow = drinkGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", drinkFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", drinkFilter::setName));
        headerRow.getCell(stockColumn).setComponent(createFilterHeaderInteger("Bestand", drinkFilter::setStock));
        headerRow.getCell(reorderPointColumn)
                .setComponent(createFilterHeaderInteger("Meldebestand", drinkFilter::setReorderPoint));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktive", "Inaktiv", drinkFilter::setActive));
        drinkFilter.setActive(true);
        drinkGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Drink> optionalDrink = e.getFirstSelectedItem();
                if (optionalDrink.isPresent()) {
                    selectedDrink = optionalDrink.get();
                    drinkBinder.readBean(selectedDrink);
                } else {
                    selectedDrink = null;
                    drinkBinder.readBean(new Drink());
                }
            }
            updateEditDrinkLayoutState();
        });
        return drinkGrid;
    }

    private Grid<BowlingShoe> createShoeGrid() {
        Grid<BowlingShoe> shoeGrid = new Grid<>(BowlingShoe.class);
        shoeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        shoeGrid.removeAllColumns();
        Grid.Column<BowlingShoe> idColumn = shoeGrid.addColumn(BowlingShoe::getId).setHeader("ID");
        Grid.Column<BowlingShoe> boughtColumn = shoeGrid.addColumn("boughtAt").setHeader("Kaufdatum");
        Grid.Column<BowlingShoe> sizeColumn = shoeGrid.addColumn("size").setHeader("Größe");
        Grid.Column<BowlingShoe> activeColumn = shoeGrid.addColumn("active").setHeader("Aktiv");
        shoeGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        shoeGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        shoeGrid.setWidth("75%");
        shoeGrid.setHeight("100%");
        List<BowlingShoe> shoeList = bowlingShoeRepository.findAll();
        GridListDataView<BowlingShoe> dataView = shoeGrid.setItems(shoeList);
        ShoeFilter shoeFilter = new ShoeFilter(dataView);
        shoeGrid.getHeaderRows().clear();
        HeaderRow headerRow = shoeGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", shoeFilter::setId));
        //headerRow.getCell(boughtColumn).setComponent(createFilterHeaderString("Kaufdatum", shoeFilter::boughtAt));
        headerRow.getCell(sizeColumn).setComponent(createFilterHeaderInteger("Größe", shoeFilter::setSize));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", shoeFilter::setActive));
        shoeFilter.setActive(true);
        shoeGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<BowlingShoe> optionalBowlingShoe = e.getFirstSelectedItem();
                if (optionalBowlingShoe.isPresent()) {
                    selectedShoe = optionalBowlingShoe.get();
                    shoeBinder.readBean(selectedShoe);
                } else {
                    selectedShoe = null;
                    shoeBinder.readBean(new BowlingShoe());
                }
            }
            updateEditShoeLayoutState();
        });
        return shoeGrid;
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

    private static class DrinkFilter {
        private final GridListDataView<Drink> dataView;
        private String id;
        private String name;
        private String stock;
        private String reorderPoint;

        private boolean active;

        public DrinkFilter(GridListDataView<Drink> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(Drink drink) {
            boolean matchesId = matches(String.valueOf(drink.getId()), id);
            boolean matchesName = matches(drink.getName(), name);
            boolean matchesStock = matches(String.valueOf(drink.getStockInMilliliters()), stock);
            boolean matchesReorderPoint = matches(String.valueOf(drink.getReorderPoint()), reorderPoint);
            boolean matchesActive = drink.isActive() == active;
            return matchesId && matchesName && matchesStock && matchesReorderPoint && matchesActive;
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

        public void setActive(boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }

    private static class DrinkVariantFilter {
        private final GridListDataView<DrinkVariant> dataView;
        private String id;
        private String name; //siehe Methode Zeile 439
        private String milliliter;
        private String price;


        public DrinkVariantFilter(GridListDataView<DrinkVariant> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(DrinkVariant drinkVariant) {
            boolean matchesId = matches(String.valueOf(drinkVariant.getId()), id);
            boolean matchesDrink = matches(String.valueOf(drinkVariant.getDrink()), name); //Filter funktioniert noch nicht
            boolean matchesMilliliter = matches(String.valueOf(drinkVariant.getMl()), milliliter);
            boolean matchesPrice = matches(String.valueOf(drinkVariant.getPrice()), price);
            return matchesId && matchesDrink && matchesMilliliter && matchesPrice;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /*public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }
        //Da das Filter Feld für GetränkeVariante noch nicht funktioniert ist diese Methode auskommentiert
         */

        public void setMl(String milliliter) {
            this.milliliter = milliliter;
            dataView.refreshAll();
        }

        public void setPrice(String price) {
            this.price = price;
            dataView.refreshAll();
        }

    }

    private static class ShoeFilter {
        private final GridListDataView<BowlingShoe> dataView;
        private String id;
        private DatePicker boughtAt;
        //private String boughtAt;
        private String size;

        private boolean active;

        public ShoeFilter(GridListDataView<BowlingShoe> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(BowlingShoe shoe) {
            boolean matchesId = matches(String.valueOf(shoe.getId()), id);
            // boolean matchesBoughtAt = matches(boughtAt);
            boolean matchesSize = matches(String.valueOf(shoe.getSize()), size);
            boolean matchesActive = shoe.isActive() == active;
            return matchesId && matchesSize && matchesActive;
        }

        /*
        Filterregel noch nicht geschrieben
        */

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setBoughtAt(DatePicker boughtAt) {
            this.boughtAt = boughtAt;
            dataView.refreshAll();
        }

        public void setSize(String size) {
            this.size = size;
            dataView.refreshAll();
        }

        public void setActive(boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
