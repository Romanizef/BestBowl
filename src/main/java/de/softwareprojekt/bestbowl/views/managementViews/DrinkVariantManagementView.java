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
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkVariantRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.form.DrinkVariantForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

@Route(value = "drinkVariantManagement", layout = MainView.class)
@PageTitle("Getränkevariantenverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class DrinkVariantManagementView extends VerticalLayout {
    private final DrinkVariantRepository drinkVariantRepository;
    private final Binder<DrinkVariant> drinkVariantBinder = new Binder<>();
    private final Binder<Drink> drinkBinder = new Binder<>();
    private Grid<DrinkVariant> drinkVariantGrid;
    private DrinkVariantForm drinkVariantForm;
    private DrinkVariant selectedDrinkVariant = null;
    private Label validationErrorLabel;
    private boolean editingNewDrinkVariant = false;
    private final Button saveButton = new Button("Sichern");
    private final Button cancelButton = new Button("Abbrechen");

    @Autowired
    public DrinkVariantManagementView(DrinkVariantRepository drinkVariantRepository) {
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
            drinkVariantBinder.readBean(null);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            editingNewDrinkVariant = true;
            updateEditDrinkVariantLayoutState();
        });
        return button;
    }

    private void updateEditDrinkVariantLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(drinkVariantForm.getChildren(), selectedDrinkVariant != null);
    }

    private HorizontalLayout createDrinkVariantGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        drinkVariantGrid = createDrinkVariantGrid();
        layout.add(drinkVariantGrid, createDrinkVariantFormLayout());
        return layout;
    }

    private VerticalLayout createDrinkVariantFormLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("25%");
        drinkVariantForm = new DrinkVariantForm(drinkVariantBinder, drinkBinder);
        layout.add(drinkVariantForm, createValidationLabelLayout(), createButton());
        loadNewDrinkVariant();
        return layout;
    }
    private VerticalLayout createValidationLabelLayout(){
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

    private boolean writeBean(){
        try{
            drinkVariantBinder.writeBean(selectedDrinkVariant);
            return true;
        } catch (ValidationException e){
            if(!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    public Component createButton(){
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        saveButton.addClickListener(clickEvent ->{
            DrinkVariant undeditedDrinkVariant = new DrinkVariant(selectedDrinkVariant);
            if(writeBean()){
                if(validateDrinkVariantSave()){
                    saveToDbAndUpdateDrinkVariant();
                } else {
                    selectedDrinkVariant.copyValueOf(undeditedDrinkVariant);
                }
            }
        });
        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        return buttonLayout;
    }

    private void saveToDbAndUpdateDrinkVariant(){
        drinkVariantRepository.save(selectedDrinkVariant);
        if(editingNewDrinkVariant){
            drinkVariantGrid.getListDataView().addItem(selectedDrinkVariant);
        } else {
            drinkVariantGrid.getListDataView().refreshItem(selectedDrinkVariant);
        }
        resetEditLayout();
        showNotification("Getränkvariante gespeichert");
    }

    private void resetEditLayout(){
        drinkVariantGrid.deselectAll();
        selectedDrinkVariant = null;
        editingNewDrinkVariant = false;
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);

        Drink drink = new Drink();
        drink.addDrinkVariant(new DrinkVariant());
        drinkBinder.readBean(drink);

        updateEditDrinkVariantLayoutState();
        setValueForIntegerFieldChildren(drinkVariantForm.getChildren(), null);
    }

    private boolean validateDrinkVariantSave(){
        Optional<DrinkVariant> dbDrinkVariant = drinkVariantRepository.findById(selectedDrinkVariant.getId());
        //Variante(ml) und name duplicate check
        // name check noch nicht geschrieben FEHLT noch !!
        Set<Integer> drinkVariantMlSet = drinkVariantRepository.findAllMlForDrink(selectedDrinkVariant.getDrink());
        dbDrinkVariant.ifPresent(drinkVariant -> {
            drinkVariantMlSet.remove(drinkVariant.getMl());
        });
        if(drinkVariantMlSet.contains(selectedDrinkVariant.getMl())) {
            validationErrorLabel.setText("Ein Getränkvariante mit dieser größe existiert bereits");
            return false;
        }
        return true;
    }

    public void loadNewDrinkVariant(){
        int counter = drinkVariantGrid.getListDataView().getItemCount();
        int drinkVariantCounter = counter -1;
        selectedDrinkVariant = drinkVariantGrid.getListDataView().getItem(drinkVariantCounter);
        drinkVariantBinder.readBean(selectedDrinkVariant);
    }

    private Grid<DrinkVariant> createDrinkVariantGrid() {
        Grid<DrinkVariant> drinkVariantGrid = new Grid<>(DrinkVariant.class);
        drinkVariantGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        drinkVariantGrid.removeAllColumns();
        Grid.Column<DrinkVariant> idColumn = drinkVariantGrid.addColumn(DrinkVariant::getId).setHeader("ID");
        Grid.Column<DrinkVariant> nameColum = drinkVariantGrid.addColumn(DrinkVariant ->
                DrinkVariant.getDrink() == null ? "" : DrinkVariant.getDrink().getName()).setHeader("Getränk");
        Grid.Column<DrinkVariant> mlColumn = drinkVariantGrid.addColumn(DrinkVariant::getMl).setHeader("Variante");
        Grid.Column<DrinkVariant> priceColumn = drinkVariantGrid.addColumn(DrinkVariant::getPrice).setHeader("Preis");
        Grid.Column<DrinkVariant> activeColumn =
                drinkVariantGrid.addColumn(DrinkVariant ->
                        DrinkVariant.getDrink() == null ? "" : DrinkVariant.getDrink().isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        drinkVariantGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        drinkVariantGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        drinkVariantGrid.setWidth("75%");
        drinkVariantGrid.setHeight("100%");

        List<DrinkVariant> drinkVariantList = drinkVariantRepository.findAll();
        GridListDataView<DrinkVariant> dataView = drinkVariantGrid.setItems(drinkVariantList);

        DrinkVariantManagementView.DrinkVariantFilter drinkVariantFilter = new DrinkVariantManagementView.DrinkVariantFilter(dataView);
        drinkVariantGrid.getHeaderRows().clear();
        HeaderRow headerRow = drinkVariantGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", drinkVariantFilter::setId));
        headerRow.getCell(nameColum).setComponent(createFilterHeaderString("Getränk", drinkVariantFilter::setName));
        headerRow.getCell(mlColumn).setComponent(createFilterHeaderInteger("Variante", drinkVariantFilter::setMl));
        headerRow.getCell(priceColumn).setComponent(createFilterHeaderInteger("Preis", drinkVariantFilter::setPrice));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktive", "Inaktiv", drinkVariantFilter::setActive));

        drinkVariantGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<DrinkVariant> optionalDrinkVariant = e.getFirstSelectedItem();
                saveButton.setEnabled(true);
                cancelButton.setEnabled(true);
                if (optionalDrinkVariant.isPresent()) {
                    selectedDrinkVariant = optionalDrinkVariant.get();
                    drinkVariantBinder.readBean(selectedDrinkVariant);
                    editingNewDrinkVariant = false;
                } else {
                    selectedDrinkVariant = null;
                    drinkVariantBinder.readBean(null);
                    saveButton.setEnabled(false);
                    cancelButton.setEnabled(false);
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
        private Boolean active;

        public DrinkVariantFilter(GridListDataView<DrinkVariant> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(DrinkVariant drinkVariant) {
            boolean matchesId = matches(String.valueOf(drinkVariant.getId()), id);
            //boolean matchesDrink = matches((drinkVariant.getDrink().getName()), name);
            boolean matchesMilliliter = matches(String.valueOf(drinkVariant.getMl()), milliliter);
            boolean matchesPrice = matches(String.valueOf(drinkVariant.getPrice()), price);
         //   boolean matchesActive = active == null || active == drinkVariant.getDrink().isActive();
            return matchesId  && matchesMilliliter && matchesPrice; // && matchesActive && matchesDrink;
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

        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
