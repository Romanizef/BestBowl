package de.softwareprojekt.bestbowl.views.managementViews;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkVariantRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.form.DrinkForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;
import static de.softwareprojekt.bestbowl.utils.messages.NotificationSender.showNotification;

@Route(value = "drinkManagement", layout = MainView.class)
@PageTitle("Getränkeverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class DrinkManagementView extends VerticalLayout {
    private final DrinkRepository drinkRepository;
    private final DrinkVariantRepository drinkVariantRepository;
    private final Binder<Drink> drinkBinder = new Binder<>();
    private final Button saveButton = new Button("Sichern");
    private final Button cancelButton = new Button("Abbrechen");
    private final Button saveAndOpenDrinkVariantButton = new Button("Sicher & neue Variante anlegen");
    private Grid<Drink> drinkGrid;
    private DrinkForm drinkForm;
    private Drink selectedDrink = null;
    private Label validationErrorLabel;
    private boolean editingNewDrink = false;

    @Autowired
    public DrinkManagementView(DrinkRepository drinkRepository, DrinkVariantRepository drinkVariantRepository) {
        this.drinkRepository = drinkRepository;
        this.drinkVariantRepository = drinkVariantRepository;
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
            saveAndOpenDrinkVariantButton.setEnabled(true);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            editingNewDrink = true;
            updateEditDrinkLayoutState();
            clearNumberFieldChildren(drinkForm.getChildren());
        });
        return button;
    }

    private void updateEditDrinkLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(drinkForm.getChildren(), selectedDrink != null);
    }

    private HorizontalLayout createDrinkGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        drinkGrid = createDrinkGrid();
        layout.add(drinkGrid, createDrinkFormLayout());
        return layout;
    }

    private VerticalLayout createDrinkFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("25%");
        drinkForm = new DrinkForm(drinkBinder);
        layout.add(drinkForm, createValiationLabelLayout(), createButton());
        return layout;
    }

    private VerticalLayout createValiationLabelLayout() {
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
            drinkBinder.writeBean(selectedDrink);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

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

    private void saveToDbAndUpdateDrink() {
        drinkRepository.save(selectedDrink);
        if (editingNewDrink) {
            drinkGrid.getListDataView().addItem(selectedDrink);
        } else {
            drinkGrid.getListDataView().refreshItem(selectedDrink);
        }
        resetEditLayout();
        showNotification("Getränk gespeichert");
    }

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

    private boolean validateDrinkSave() {
        Optional<Drink> dbDrink = drinkRepository.findById(selectedDrink.getId());
        //name duplicate check
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


    private Grid<Drink> createDrinkGrid() {
        Grid<Drink> drinkGrid = new Grid<>(Drink.class);
        drinkGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        drinkGrid.removeAllColumns();
        Grid.Column<Drink> idColumn = drinkGrid.addColumn(Drink::getId).setHeader("ID");
        Grid.Column<Drink> nameColumn = drinkGrid.addColumn("name").setHeader("Name");
        Grid.Column<Drink> stockColumn = drinkGrid.addColumn("stockInMilliliters").setHeader("Bestand");
        Grid.Column<Drink> reorderPointColumn = drinkGrid.addColumn("reorderPoint").setHeader("Meldebestand");
        Grid.Column<Drink> activeColumn = drinkGrid.addColumn(drink -> drink.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        drinkGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        drinkGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        drinkGrid.setWidth("75%");
        drinkGrid.setHeight("100%");
        List<Drink> drinkList = drinkRepository.findAll();
        GridListDataView<Drink> dataView = drinkGrid.setItems(drinkList);

        DrinkManagementView.DrinkFilter drinkFilter = new DrinkManagementView.DrinkFilter(dataView);
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
