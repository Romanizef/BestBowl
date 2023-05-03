package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.utils.validators.BowlingAlleyValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Matija
 */
@Route(value = "alleyManagement", layout = MainView.class)
@PageTitle("Bahnverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class AlleyManagementView extends VerticalLayout {
    private final Binder<BowlingAlley> binder = new Binder<>();
    private final BowlingAlleyRepository bowlingAlleyRepository;
    private Grid<BowlingAlley> bowlingAlleyGrid;
    private FormLayout editLayout;
    private BowlingAlley selectedBowlingAlley = null;
    private Label validationErrorLabel;
    private boolean editingNewBowlingAlley = false;

    @Autowired
    public AlleyManagementView(BowlingAlleyRepository bowlingAlleyRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        setSizeFull();
        Button newBowlingAlleyButton = createNewBowlingAlleyButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newBowlingAlleyButton, gridLayout);
        updateEditLayoutState();
    }

    private Button createNewBowlingAlleyButton() {
        Button button = new Button("Neue Bahn hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            bowlingAlleyGrid.deselectAll();
            selectedBowlingAlley = new BowlingAlley();
            binder.readBean(selectedBowlingAlley);
            editingNewBowlingAlley = true;
            updateEditLayoutState();
        });
        return button;
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        bowlingAlleyGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(bowlingAlleyGrid, editLayout);
        return layout;
    }

    private Grid<BowlingAlley> createGrid() {
        Grid<BowlingAlley> grid = new Grid<>(BowlingAlley.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<BowlingAlley> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<BowlingAlley> activeColumn = grid
                .addColumn(association -> association.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<BowlingAlley> bowlingAlleyList = bowlingAlleyRepository.findAll();
        GridListDataView<BowlingAlley> dataView = grid.setItems(bowlingAlleyList);
        BowlingAlleyFilter associationFilter = new BowlingAlleyFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", associationFilter::setId));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", associationFilter::setActive));
        associationFilter.setActive(true);

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<BowlingAlley> optionalBowlingAlley = e.getFirstSelectedItem();
                if (optionalBowlingAlley.isPresent()) {
                    selectedBowlingAlley = optionalBowlingAlley.get();
                    binder.readBean(selectedBowlingAlley);
                    editingNewBowlingAlley = false;
                    updateEditLayoutState();
                } else {
                    resetEditLayout();
                }
            }
        });
        return grid;
    }

    private FormLayout createEditLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidth("25%");

        IntegerField idField = new IntegerField("ID");
        idField.setWidthFull();
        idField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");

        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);
        layout.add(idField, checkboxLayout, createValidationLabelLayout(), buttonLayoutConfig());

        binder.withValidator(new BowlingAlleyValidator());
        binder.bind(idField, BowlingAlley::getId, (alley, i) -> alley.setId(Objects.requireNonNullElse(i, 0)));
        binder.bind(activeCheckbox, BowlingAlley::isActive, BowlingAlley::setActive);
        return layout;
    }

    private HorizontalLayout buttonLayoutConfig() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        Button saveButton = new Button("Speichern");
        Button cancelButton = new Button("Abbrechen");
        buttonLayout.add(cancelButtonConfig(cancelButton), saveButtonConfig(saveButton));
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        return buttonLayout;
    }

    private Button saveButtonConfig(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        saveButton.addClickListener(clickEvent -> {
            if (writeBean()) {
                saveToDb();
            }
        });
        return saveButton;
    }

    private Button cancelButtonConfig(Button cancelButton) {
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
        cancelButton.addClickListener(clickEvent -> resetEditLayout());
        return cancelButton;
    }

    private void updateEditLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(editLayout.getChildren(), selectedBowlingAlley != null);
    }

    private void resetEditLayout() {
        bowlingAlleyGrid.deselectAll();
        selectedBowlingAlley = null;

        BowlingAlley bowlingAlley = new BowlingAlley();
        binder.readBean(bowlingAlley);

        updateEditLayoutState();
        setValueForIntegerFieldChildren(editLayout.getChildren(), null);
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
            binder.writeBean(selectedBowlingAlley);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    private void saveToDb() {
        bowlingAlleyRepository.save(selectedBowlingAlley);
        if (editingNewBowlingAlley) {
            bowlingAlleyGrid.getListDataView().addItem(selectedBowlingAlley);
        } else {
            bowlingAlleyGrid.getListDataView().refreshItem(selectedBowlingAlley);
        }
        resetEditLayout();
        showNotification("Bahn gespeichert");
    }

    private static class BowlingAlleyFilter {
        private final GridListDataView<BowlingAlley> dataView;
        private String id;
        private Boolean active;

        public BowlingAlleyFilter(GridListDataView<BowlingAlley> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(BowlingAlley bowlingAlley) {
            boolean matchesId = matches(String.valueOf(bowlingAlley.getId()), id);
            boolean matchesActive = active == null || active == bowlingAlley.isActive();
            return matchesId && matchesActive;
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
