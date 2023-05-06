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
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.form.DrinkForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

@Route(value = "DrinkManagement", layout = MainView.class)
@PageTitle("Getraenkeverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class DrinkView extends VerticalLayout {
    private final DrinkRepository drinkRepository;
    private final Binder<Drink> drinkBinder = new Binder<>();
    private Grid<Drink> drinkGrid;
    private DrinkForm drinkForm;
    private Drink selectedDrink = null;

    @Autowired
    public DrinkView (DrinkRepository drinkRepository){
        this.drinkRepository = drinkRepository;
        setSizeFull();
        Button newDrinkButton = createNewDrinkButton();
        HorizontalLayout drinkGridFormLayout = createDrinkGridFormLayout();
        add(newDrinkButton, drinkGridFormLayout);
        updateEditDrinkLayoutState();
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

    private void updateEditDrinkLayoutState() {
        setChildrenEnabled(drinkForm.getChildren(), selectedDrink != null);
        if (selectedDrink == null) {
            setValueForIntegerFieldChildren(drinkForm.getChildren(), null);
        }
    }

    private HorizontalLayout createDrinkGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        drinkForm = new DrinkForm(drinkBinder);
        layout.setSizeFull();
        drinkGrid = createDrinkGrid();
        layout.add(drinkGrid, drinkForm);
        return layout;
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
        drinkGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        drinkGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        drinkGrid.setWidth("75%");
        drinkGrid.setHeight("100%");
        List<Drink> drinkList = drinkRepository.findAll();
        GridListDataView<Drink> dataView = drinkGrid.setItems(drinkList);

        DrinkView.DrinkFilter drinkFilter = new DrinkView.DrinkFilter(dataView);
        drinkGrid.getHeaderRows().clear();
        HeaderRow headerRow = drinkGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", drinkFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", drinkFilter::setName));
        headerRow.getCell(stockColumn).setComponent(createFilterHeaderInteger("Bestand", drinkFilter::setStock));
        headerRow.getCell(reorderPointColumn)
                .setComponent(createFilterHeaderInteger("Meldebestand", drinkFilter::setReorderPoint));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktive", "Inaktiv", drinkFilter::setActive));
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

    private static class DrinkFilter {
        private final GridListDataView<Drink> dataView;
        private String id;
        private String name;
        private String stock;
        private String reorderPoint;
        private Boolean active;

        public DrinkFilter(GridListDataView<Drink> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(Drink drink) {
            boolean matchesId = matches(String.valueOf(drink.getId()), id);
            boolean matchesName = matches(drink.getName(), name);
            boolean matchesStock = matches(String.valueOf(drink.getStockInMilliliters()), stock);
            boolean matchesReorderPoint = matches(String.valueOf(drink.getReorderPoint()), reorderPoint);
            boolean matchesActive = active == null || active == drink.isActive();
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

        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
