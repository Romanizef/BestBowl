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
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkVariantRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.form.DrinkVariantForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createFilterHeaderInteger;

@Route(value = "DrinkVariantManagement", layout = MainView.class)
@PageTitle("Getraenkevariantenverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class DrinkVariantView extends VerticalLayout {
    private final DrinkVariantRepository drinkVariantRepository;
    private final Binder<DrinkVariant> drinkVariantBinder = new Binder<>();
    private final Binder<Drink> drinkBinder = new Binder<>();
    private Grid<DrinkVariant> drinkVariantGrid;
    private DrinkVariantForm drinkVariantForm;
    private DrinkVariant selectedDrinkVariant = null;

    @Autowired
    public DrinkVariantView(DrinkVariantRepository drinkVariantRepository){
        this.drinkVariantRepository = drinkVariantRepository;
        setSizeFull();
        Button newDrinkVariantButton = createNewDrinkVariantButton();
        HorizontalLayout drinkVariantGridFormLayout = createDrinkVariantGridFormLayout();
        add(newDrinkVariantButton, drinkVariantGridFormLayout);
        updateEditDrinkVariantLayoutState();
    }

    private Button createNewDrinkVariantButton() {
        Button button = new Button("Neue Getränkevariante hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            drinkVariantGrid.deselectAll();
            selectedDrinkVariant = new DrinkVariant();
            drinkVariantBinder.readBean(selectedDrinkVariant);
            updateEditDrinkVariantLayoutState();
        });
        return button;
    }

    private void updateEditDrinkVariantLayoutState() {
        setChildrenEnabled(drinkVariantForm.getChildren(), selectedDrinkVariant != null);
        if (selectedDrinkVariant == null) {
            setValueForIntegerFieldChildren(drinkVariantForm.getChildren(), null);
        }
    }

    private HorizontalLayout createDrinkVariantGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        drinkVariantForm = new DrinkVariantForm(drinkVariantBinder, drinkBinder);
        layout.setSizeFull();
        drinkVariantGrid = createDrinkVariantGrid();
        layout.add(drinkVariantGrid, drinkVariantForm);
        return layout;
    }

    private Grid<DrinkVariant> createDrinkVariantGrid() {
        Grid<DrinkVariant> drinkVariantGrid = new Grid<>(DrinkVariant.class);
        drinkVariantGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        drinkVariantGrid.removeAllColumns();
        Grid.Column<DrinkVariant> idColumn = drinkVariantGrid.addColumn(DrinkVariant::getId).setHeader("ID");
        Grid.Column<DrinkVariant> nameColum = drinkVariantGrid.addColumn(DrinkVariant -> DrinkVariant.getDrink() == null ? "" : DrinkVariant.getDrink().getName()).setHeader("Getränk");
        Grid.Column<DrinkVariant> mlColumn = drinkVariantGrid.addColumn(DrinkVariant::getMl).setHeader("Variante");
        Grid.Column<DrinkVariant> priceColumn = drinkVariantGrid.addColumn(DrinkVariant::getPrice).setHeader("Preis");
        drinkVariantGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        drinkVariantGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        drinkVariantGrid.setWidth("75%");
        drinkVariantGrid.setHeight("100%");

        List<DrinkVariant> drinkVariantList = drinkVariantRepository.findAll();
        GridListDataView<DrinkVariant> dataView = drinkVariantGrid.setItems(drinkVariantList);

        DrinkVariantView.DrinkVariantFilter drinkVariantFilter = new DrinkVariantView.DrinkVariantFilter(dataView);
        drinkVariantGrid.getHeaderRows().clear();
        HeaderRow headerRow = drinkVariantGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", drinkVariantFilter::setId));
        headerRow.getCell(nameColum).setComponent(createFilterHeaderString("Getränk", drinkVariantFilter::setName));
        headerRow.getCell(mlColumn).setComponent(createFilterHeaderInteger("Variante", drinkVariantFilter::setMl));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderInteger("Preis", drinkVariantFilter::setPrice));

        drinkVariantGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<DrinkVariant> optionalDrinkVariant = e.getFirstSelectedItem();
                if (optionalDrinkVariant.isPresent()) {
                    selectedDrinkVariant = optionalDrinkVariant.get();
                    drinkVariantBinder.readBean(selectedDrinkVariant);
                } else {
                    selectedDrinkVariant = null;
                    drinkVariantBinder.readBean(new DrinkVariant());
                }
            }
            updateEditDrinkVariantLayoutState();
        });
        return drinkVariantGrid;
    }

    private static class DrinkVariantFilter {
        private final GridListDataView<de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant> dataView;
        private String id;
        private String name;
        private String milliliter;
        private String price;

        public DrinkVariantFilter(GridListDataView<de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant drinkVariant) {
            boolean matchesId = matches(String.valueOf(drinkVariant.getId()), id);
            boolean matchesDrink = matches((drinkVariant.getDrink().getName()), name);
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

        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        public void setMl(String milliliter) {
            this.milliliter = milliliter;
            dataView.refreshAll();
        }

        public void setPrice(String price) {
            this.price = price;
            dataView.refreshAll();
        }
    }
}
