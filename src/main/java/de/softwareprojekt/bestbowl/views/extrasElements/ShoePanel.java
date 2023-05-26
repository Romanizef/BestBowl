package de.softwareprojekt.bestbowl.views.extrasElements;

import java.util.List;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeBookingRepository;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class ShoePanel extends HorizontalLayout {
    private FormLayout kachelLayout;
    private Grid<BowlingShoeBooking> shoeGrid;

    public ShoePanel() {
        Label label = new Label("Schuhgröße");
        label.setMinWidth("250px");
        label.setMaxWidth("250px");

        IntegerField shoeAmountField = new IntegerField();
        // shoeAmountField.setValue(bowlingShoeBooking.getAmount());
        shoeAmountField.setStepButtonsVisible(true);
        shoeAmountField.setMin(0);
        // shoeAmountField.setMax(bowlingShoeBooking.getAmount());

        setWidthFull();
        // addCSS();
        setAlignItems(Alignment.CENTER);
        add(label, shoeAmountField);
    }

    public ShoePanel(BowlingShoeBooking bowlingShoeBooking, BowlingShoeBookingRepository shoeBookingRepository) {
        Label label = new Label("" + bowlingShoeBooking.getBowlingShoe().getSize());
        label.setMinWidth("250px");
        label.setMaxWidth("250px");
        // list mit Schuhen wo der spezifische Client drin steht und alle Elemente in
        // der Liste sind der amount
        List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository
                .findAllByClientEquals(bowlingShoeBooking.getClient());
        IntegerField shoeAmountField = new IntegerField();
        shoeAmountField.setValue(shoeBookingList.size());
        shoeAmountField.setStepButtonsVisible(true);
        shoeAmountField.setMin(0);
        shoeAmountField.setMax(shoeBookingList.size());

        setWidthFull();
        // addCSS();
        setAlignItems(Alignment.CENTER);
        add(label, shoeAmountField);
    }

    public IntegerField createIntegerField(BowlingShoe bowlingShoe, BowlingShoeBooking bowlingShoeBooking) {
        IntegerField sizeField = new IntegerField();
        sizeField.setValue(0);
        sizeField.setStepButtonsVisible(true);
        sizeField.setMin(0);
        // sizeField.setMax(bowlingShoe.getStock() / bowlingShoeBooking.getAmount());

        sizeField.addValueChangeListener(e -> {
            // TODO
        });
        return sizeField;
    }

    public FormLayout addPanelComponent() {
        kachelLayout = new FormLayout();
        kachelLayout.setResponsiveSteps(new ResponsiveStep("0", 2));

        IntegerField shoeSizeField = new IntegerField();
        shoeSizeField.setValue(35);
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
        shoeAmountField.setMax(10); // TODO Lagerbestand muss aus der DB ausgelesen werden und als max gesetzt

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