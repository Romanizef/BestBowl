package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;

public class ShoePanel {
    private FormLayout kachelLayout;
    private Grid<BowlingShoeBooking> shoeGrid;

    public ShoePanel() {

    }

    public FormLayout addShoePanelComponent() {

        kachelLayout = new FormLayout();
        kachelLayout.setResponsiveSteps(new ResponsiveStep("0", 2));

        IntegerField shoeSizeField = new IntegerField();
        shoeSizeField.setValue(0);
        shoeSizeField.setStepButtonsVisible(true);
        shoeSizeField.setMin(35);
        shoeSizeField.setMax(45);
        shoeSizeField.addValueChangeListener(e -> {

        });
        kachelLayout.addFormItem(shoeSizeField, "Größe: ");

        IntegerField shoeAmountField = new IntegerField();
        shoeAmountField.setValue(0);
        shoeAmountField.setStepButtonsVisible(true);
        shoeAmountField.setMin(0);
        shoeAmountField.setMax(10); //TODO Lagerbestand muss aus der DB ausgelesen werden und als max gesetzt

        kachelLayout.addFormItem(shoeAmountField, "Menge: ");

        return kachelLayout;
    }

    private HorizontalLayout createShoeGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        shoeGrid = createGrid();
        layout.add(shoeGrid);
        return layout;
    }

    private Grid<BowlingShoeBooking> createGrid() {
        Grid<BowlingShoeBooking> grid = new Grid<>(BowlingShoeBooking.class);
        /*
         * grid.removeAllColumns();
         * Grid.Column<BowlingShoeBooking> idColumn =
         * grid.addColumn("id").setHeader("ID");
         * Grid.Column<BowlingShoeBooking> sizeColumn =
         * grid.addColumn("id").setHeader("ID");
         * grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
         * grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS,
         * GridVariant.LUMO_ROW_STRIPES);
         * grid.setWidth("75%");
         * grid.setHeight("100%");
         */
        return grid;
    }

}