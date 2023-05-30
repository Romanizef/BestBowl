package de.softwareprojekt.bestbowl.views.bookingViews;


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

    private void updateEditLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(editLayout.getChildren(), selectedBowlingAlley != null);
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
*/
}



