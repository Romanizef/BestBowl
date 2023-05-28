package de.softwareprojekt.bestbowl.views.extrasElements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeRepository;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class ShoePanel extends HorizontalLayout {
    private FormLayout kachelLayout;
    private Grid<BowlingShoeBooking> shoeGrid;


    private IntegerField shoeSizeField;
    private IntegerField shoeAmountField;

    private HorizontalLayout amountLayout;

    private HorizontalLayout sizeLayout;
    private BowlingShoeRepository bowlingShoeRepository;

    private Map<Integer,Integer> shoeSizeAmountMap;

    public ShoePanel(BowlingShoeRepository bowlingShoeRepository) {
        this.bowlingShoeRepository = bowlingShoeRepository;
        List<BowlingShoe> shoeList = bowlingShoeRepository.findAllByClientIsNullAndActiveIsTrue();
        int value;
        shoeSizeAmountMap = new HashMap<>();
        for (int i = 30; i < 51; i++) {
            shoeSizeAmountMap.put(i,0);
        }
        for (BowlingShoe shoe: shoeList) {
            if(shoe.isActive()){
                if(shoeSizeAmountMap.containsKey(shoe.getSize())){
                    value = shoeSizeAmountMap.get(shoe.getSize());
                    value++;
                    shoeSizeAmountMap.put(shoe.getSize(), value);
                }
            }
        }
        VerticalLayout panelLayout =  new VerticalLayout();
        Label sizeLabel = new Label("Schuhgröße");
        HorizontalLayout sizeLayout = new HorizontalLayout();
        sizeLabel.setMinWidth("250px");
        sizeLabel.setMaxWidth("250px");
        shoeSizeField = new IntegerField();
        shoeSizeField.setMin(30);
        shoeSizeField.setMax(50);
        shoeSizeField.setValue(40);
        shoeSizeField.setStepButtonsVisible(true);
        sizeLayout.add(sizeLabel,shoeSizeField);
        HorizontalLayout amountLayout = new HorizontalLayout();
        Label amountLabel = new Label("Menge");
        amountLabel.setMinWidth("250px");
        amountLabel.setMaxWidth("250px");
        shoeAmountField = new IntegerField();
        shoeAmountField.setValue(0);
        shoeAmountField.setMin(0);
        shoeAmountField.setMax(shoeSizeAmountMap.get(shoeSizeField.getValue()));
        shoeAmountField.setStepButtonsVisible(true);
        amountLayout.add(amountLabel,shoeAmountField);
        setWidthFull();
        addCSS();
        setAlignItems(Alignment.CENTER);
        panelLayout.add(sizeLayout, amountLayout);
        add(panelLayout);
    }

    private void addCSS() {
        getStyle().set("border", "2px solid #db03fc").set("background-color", "#db03fc10").set("padding", "10px")
                .set("margin-bottom", "10px").set("border-radius", "50px");
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

    public void updateShoeSizeAmountMap(){
        List<BowlingShoe> shoeList = bowlingShoeRepository.findAllByClientIsNullAndActiveIsTrue();
        int value;
        shoeSizeAmountMap = new HashMap<>();
        for (int i = 30; i < 51; i++) {
            shoeSizeAmountMap.put(i,0);
        }
        for (BowlingShoe shoe: shoeList) {
            if(shoe.isActive()){
                if(shoeSizeAmountMap.containsKey(shoe.getSize())){
                    value = shoeSizeAmountMap.get(shoe.getSize());
                    value++;
                    shoeSizeAmountMap.put(shoe.getSize(), value);
                }
            }
        }
    }
    public void resetIntergerfield(){
        shoeAmountField.setValue(0);
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

    public FormLayout getKachelLayout() {
        return kachelLayout;
    }

    public Grid<BowlingShoeBooking> getShoeGrid() {
        return shoeGrid;
    }

    public IntegerField getShoeSizeField() {
        return shoeSizeField;
    }

    public IntegerField getShoeAmountField() {
        return shoeAmountField;
    }

    public HorizontalLayout getAmountLayout() {
        return amountLayout;
    }

    public HorizontalLayout getSizeLayout() {
        return sizeLayout;
    }

    public BowlingShoeRepository getBowlingShoeRepository() {
        return bowlingShoeRepository;
    }

    public Map<Integer, Integer> getShoeSizeAmountMap() {
        return shoeSizeAmountMap;
    }

    public void setKachelLayout(FormLayout kachelLayout) {
        this.kachelLayout = kachelLayout;
    }

    public void setShoeGrid(Grid<BowlingShoeBooking> shoeGrid) {
        this.shoeGrid = shoeGrid;
    }

    public void setShoeSizeField(IntegerField shoeSizeField) {
        this.shoeSizeField = shoeSizeField;
    }

    public void setShoeAmountField(IntegerField shoeAmountField) {
        this.shoeAmountField = shoeAmountField;
    }

    public void setAmountLayout(HorizontalLayout amountLayout) {
        this.amountLayout = amountLayout;
    }

    public void setSizeLayout(HorizontalLayout sizeLayout) {
        this.sizeLayout = sizeLayout;
    }

    public void setBowlingShoeRepository(BowlingShoeRepository bowlingShoeRepository) {
        this.bowlingShoeRepository = bowlingShoeRepository;
    }

    public void setShoeSizeAmountMap(Map<Integer, Integer> shoeSizeAmountMap) {
        this.shoeSizeAmountMap = shoeSizeAmountMap;
    }
}