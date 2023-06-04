package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

@Route(value = "drinkVariantManagement", layout = MainView.class)
@PageTitle("Getränkevariantenverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class DrinkVariantManagementView extends VerticalLayout {
    private final transient DrinkVariantRepository drinkVariantRepository;
    private final Binder<DrinkVariant> drinkVariantBinder = new Binder<>();
    private final Binder<Drink> drinkBinder = new Binder<>();
    private Grid<DrinkVariant> drinkVariantGrid;
    private FormLayout drinkVariantForm;
    private ComboBox<Drink> drinkCB;
    private DrinkVariant selectedDrinkVariant = null;
    private Label validationErrorLabel;
    private boolean editingNewDrinkVariant = false;

    @Autowired
    public DrinkVariantManagementView(DrinkVariantRepository drinkVariantRepository) {
        this.drinkVariantRepository = drinkVariantRepository;
        setSizeFull();
        Button newDrinkVariantButton = createNewDrinkVariantButton();
        add(newDrinkVariantButton, createGridFormLayout());
        updateEditDrinkVariantLayoutState();
    }

    public void setSelectedDrinkVariant(DrinkVariant selectedDrinkVariant) {
        this.selectedDrinkVariant = selectedDrinkVariant;
        updateEditDrinkVariantLayoutState();
        drinkVariantBinder.readBean(selectedDrinkVariant);
    }

    private Button createNewDrinkVariantButton() {
        Button button = new Button("Neue Getränkevariante hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            drinkVariantGrid.deselectAll();
            selectedDrinkVariant = new DrinkVariant();
            drinkVariantBinder.readBean(selectedDrinkVariant);
            editingNewDrinkVariant = true;
            drinkCB.setValue(Drink.NO_DRINK);
            updateEditDrinkVariantLayoutState();
            clearNumberFieldChildren(drinkVariantForm.getChildren());
        });
        return button;
    }

    private void updateEditDrinkVariantLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(drinkVariantForm.getChildren(), selectedDrinkVariant != null);
    }

    private HorizontalLayout createGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        drinkVariantGrid = createDrinkVariantGrid();
        drinkVariantForm = drinkVariantForm();
        layout.add(drinkVariantGrid, drinkVariantForm);
        return layout;
    }

    private FormLayout drinkVariantForm() {
        FormLayout drinkVariantlayout = new FormLayout();
        drinkVariantlayout.setWidth("25%");

        drinkCB = createDrinkCB();

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

        drinkVariantlayout.add(drinkCB, variantField, priceField, checkboxLayout, createValidationLabelLayout(),
                buttonLayout);

        drinkVariantBinder.withValidator(new DrinkVariantValidator());
        drinkVariantBinder.bind(drinkCB,
                drinkVariant -> drinkVariant.getDrink() == null ? Drink.NO_DRINK : drinkVariant.getDrink(),
                ((drinkVariant, drink) -> {
                    if (drink.equals(Drink.NO_DRINK)) {
                        drinkVariant.setDrink(null);
                    } else {
                        drinkVariant.setDrink(drink);
                    }
                }));
        drinkVariantBinder.bind(priceField, DrinkVariant::getPrice, DrinkVariant::setPrice);
        drinkVariantBinder.bind(variantField, DrinkVariant::getMl, DrinkVariant::setMl);
        drinkVariantBinder.bind(activeCheckbox, DrinkVariant::isActive, DrinkVariant::setActive);
        return drinkVariantlayout;
    }

    private ComboBox<Drink> createDrinkCB() {
        ComboBox<Drink> drinkCB = new ComboBox<>("Getränk");

        List<Drink> drinkList = Repos.getDrinkRepository().findAll();
        Set<Drink> drinkSet = new HashSet<>(drinkList.size() + 1);
        drinkSet.add(Drink.NO_DRINK);
        drinkSet.addAll(drinkList);
        ListDataProvider<Drink> dataProvider = new ListDataProvider<>(drinkSet);
        dataProvider.setSortComparator((SerializableComparator<Drink>) (o1, o2) -> o1.getName().compareTo(o2.getName()));

        drinkCB.setWidthFull();
        drinkCB.setItems(dataProvider);
        drinkCB.setValue(Drink.NO_DRINK);
        drinkCB.setItemLabelGenerator(Drink::getName);
        drinkCB.setAllowCustomValue(false);
        drinkCB.setRequiredIndicatorVisible(true);
        drinkCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        return drinkCB;
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
            drinkVariantBinder.writeBean(selectedDrinkVariant);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }


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

    private boolean validateDrinkVariantSave() {
        //checke ob Getränk ausgewählt
        if (selectedDrinkVariant.getDrink() == null) {
            validationErrorLabel.setText("Ein Getränk muss ausgewählt sein!");
            return false;
        }

        Optional<DrinkVariant> dbDrinkVariant = drinkVariantRepository.findById(selectedDrinkVariant.getId());
        //Variante(ml) duplicate check

        Set<Integer> drinkVariantMlSet = drinkVariantRepository.findAllMlForDrink(selectedDrinkVariant.getDrink());
        dbDrinkVariant.ifPresent(drinkVariant -> {
            drinkVariantMlSet.remove(drinkVariant.getMl());
        });
        if (drinkVariantMlSet.contains(selectedDrinkVariant.getMl())) {
            validationErrorLabel.setText("Ein Getränkevariante mit dieser größe existiert bereits");
            return false;
        }
        return true;
    }

    private Grid<DrinkVariant> createDrinkVariantGrid() {
        Grid<DrinkVariant> grid = new Grid<>(DrinkVariant.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<DrinkVariant> idColumn = grid.addColumn(DrinkVariant::getId).setHeader("ID");
        Grid.Column<DrinkVariant> nameColum = grid.addColumn(drinkVariant ->
                drinkVariant.getDrink().getName()).setHeader("Getränk");
        Grid.Column<DrinkVariant> mlColumn = grid.addColumn(DrinkVariant::getMl).setHeader("Variante (ml)");
        Grid.Column<DrinkVariant> priceColumn =
                grid.addColumn(drinkVariant -> formatDouble(drinkVariant.getPrice()) + "€").setHeader("Preis");
        Grid.Column<DrinkVariant> activeColumn =
                grid.addColumn(drinkVariant -> drinkVariant.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<DrinkVariant> drinkVariantList = drinkVariantRepository.findAll();
        GridListDataView<DrinkVariant> dataView = grid.setItems(drinkVariantList);
        dataView.setSortOrder(drinkVariant -> drinkVariant.getDrink().getName() + drinkVariant.getMl(), SortDirection.ASCENDING);

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

        public DrinkVariantFilter(GridListDataView<DrinkVariant> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(DrinkVariant drinkVariant) {
            boolean matchesId = matches(String.valueOf(drinkVariant.getId()), id);
            boolean matchesDrink = matches((drinkVariant.getDrink().getName()), name);
            boolean matchesMilliliter = matches(String.valueOf(drinkVariant.getMl()), milliliter);
            boolean matchesPrice = matches(formatDouble(drinkVariant.getPrice()) + "€", price);
            boolean matchesActive = active == null || active == drinkVariant.getDrink().isActive();
            return matchesId && matchesDrink && matchesMilliliter && matchesPrice && matchesActive;
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
