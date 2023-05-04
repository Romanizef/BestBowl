package de.softwareprojekt.bestbowl.views.managementViews;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyBookingRepository;

import java.util.List;

import static de.softwareprojekt.bestbowl.utils.Utils.matches;

/**
 * @author Ali
 */
public class AlleyBookingView extends VerticalLayout {
/*
    private final Binder<BowlingAlleyBooking> binder = new Binder<>();

    private final BowlingAlleyBookingRepository bowlingAlleyRepository;

    private Grid<BowlingAlley> bowlingAlleyGrid;

    private FormLayout editLayout;

    private BowlingAlley selectedBowlingAlley = null;

    private Label validationErrorLabel;

    private boolean editingNewBowlingAlley = false;

    public AlleyBookingView(BowlingAlleyBookingRepository bowlingAlleyRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        setSizeFull();
        Button newBowlingAlleyBookingButton = createNewBowlingAlleyButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newBowlingAlleyBookingButton, gridLayout);
        updateEditLayoutState();
    }



    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        bowlingAlleyGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(bowlingAlleyGrid, editLayout);
        return layout;
    }

    private Grid<BowlingAlleyBooking> createGrid() {
        Grid<BowlingAlleyBooking> grid = new Grid<>(BowlingAlleyBooking.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<BowlingAlleyBooking> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<BowlingAlleyBooking> activeColumn = grid
        .addColumn(association -> association.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        Grid.Column<BowlingAlleyBooking> startTime = grid.addColumn("startTime").setHeader("Start Zeit");
        Grid.Column<BowlingAlleyBooking> endTime = grid.addColumn("endTime").setHeader("End Zeit");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyRepository.findAll();
        GridListDataView<BowlingAlleyBooking> dataView = grid.setItems(bowlingAlleyBookingList);
        BowlingAlleyBookingFilter associationFilter = new BowlingAlleyBookingFilter(dataView);
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
*/
}



