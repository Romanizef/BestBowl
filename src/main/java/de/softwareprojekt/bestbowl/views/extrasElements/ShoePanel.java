package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.PANEL_COLOR_SHOE;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class ShoePanel extends VerticalLayout {
    private FormLayout kachelLayout;
    private Grid<BowlingShoeBooking> shoeGrid;

    private IntegerField shoeSizeField;
    private IntegerField shoeAmountField;

    private HorizontalLayout amountLayout;

    private HorizontalLayout sizeLayout;
    private BowlingShoeRepository bowlingShoeRepository;

    private Map<Integer, Integer> shoeSizeAmountMap;

    public ShoePanel(BowlingShoeRepository bowlingShoeRepository, Client client) {
        this.bowlingShoeRepository = bowlingShoeRepository;
        List<BowlingShoe> shoeList = bowlingShoeRepository.findAllByClientIsNullAndActiveIsTrue();
        int value;
        shoeSizeAmountMap = new HashMap<>();
        for (int i = 30; i < 51; i++) {
            shoeSizeAmountMap.put(i, 0);
        }
        for (BowlingShoe shoe : shoeList) {
            if (shoe.isActive()) {
                if (shoeSizeAmountMap.containsKey(shoe.getSize())) {
                    value = shoeSizeAmountMap.get(shoe.getSize());
                    value++;
                    shoeSizeAmountMap.put(shoe.getSize(), value);
                }
            }
        }
        VerticalLayout panelLayout = new VerticalLayout();
        shoeAmountField = new IntegerField();
        HorizontalLayout amountLayout = new HorizontalLayout();
        Label amountLabel = new Label("Menge");
        shoeSizeField = new IntegerField();
        Label sizeLabel = new Label("Schuhgröße");
        HorizontalLayout sizeLayout = new HorizontalLayout();
        sizeLabel.setMinWidth("250px");
        sizeLabel.setMaxWidth("250px");

        shoeSizeField.setMin(30);
        shoeSizeField.setMax(50);
        shoeSizeField.setValue(40);
        shoeSizeField.addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> {
            shoeAmountField.setMax(shoeSizeAmountMap.get(shoeSizeField.getValue()));
        });
        shoeSizeField.setStepButtonsVisible(true);
        sizeLayout.add(sizeLabel, shoeSizeField);

        amountLabel.setMinWidth("250px");
        amountLabel.setMaxWidth("250px");

        shoeAmountField.setValue(0);
        shoeAmountField.setMin(0);
        shoeAmountField.setMax(shoeSizeAmountMap.get(shoeSizeField.getValue()));
        shoeAmountField.setStepButtonsVisible(true);
        amountLayout.add(amountLabel, shoeAmountField);
        setWidthFull();
        addCSS();
        setAlignItems(Alignment.CENTER);
        panelLayout.add(sizeLayout, amountLayout);
        Component shoeGrid = createShoeGrid(client);
        add(panelLayout, shoeGrid);
    }

    private void addCSS() {
        getStyle()
                .set("border", "2px solid " + PANEL_COLOR_SHOE)
                .set("background-color", PANEL_COLOR_SHOE + "10")
                .set("padding", "10px")
                .set("margin-bottom", "10px")
                .set("border-radius", "20px");
    }

    public void updateShoeSizeAmountMap() {
        List<BowlingShoe> shoeList = bowlingShoeRepository.findAllByClientIsNullAndActiveIsTrue();
        int value;
        shoeSizeAmountMap = new HashMap<>();
        for (int i = 30; i < 51; i++) {
            shoeSizeAmountMap.put(i, 0);
        }
        for (BowlingShoe shoe : shoeList) {
            if (shoe.isActive()) {
                if (shoeSizeAmountMap.containsKey(shoe.getSize())) {
                    value = shoeSizeAmountMap.get(shoe.getSize());
                    value++;
                    shoeSizeAmountMap.put(shoe.getSize(), value);
                }
            }
        }
    }

    public void resetIntergerfield() {
        shoeAmountField.setValue(0);
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
        grid.removeAllColumns();
        Grid.Column<BowlingShoeBooking> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<BowlingShoeBooking> sizeColumn = grid.addColumn("size").setHeader("ID");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");
        return grid;
    }

    private Grid<BowlingShoe> createShoeGrid(Client client) {
        Grid<BowlingShoe> grid = new Grid<>(BowlingShoe.class, false);
        grid.addColumn(BowlingShoe::getId).setHeader("Schuh ID");
        grid.addColumn(BowlingShoe::getSize).setHeader("Schuh Groesse");
        List<BowlingShoe> bowlingShoeList = bowlingShoeRepository.findAllByClientEqualsAndActiveIsTrue(client);
        bowlingShoeList.sort(Comparator.comparingInt(BowlingShoe::getSize));
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES);
        grid.setItems(bowlingShoeList);
        return grid;
    }

    public FormLayout getKachelLayout() {
        return kachelLayout;
    }

    public void setKachelLayout(FormLayout kachelLayout) {
        this.kachelLayout = kachelLayout;
    }

    public Grid<BowlingShoeBooking> getShoeGrid() {
        return shoeGrid;
    }

    public void setShoeGrid(Grid<BowlingShoeBooking> shoeGrid) {
        this.shoeGrid = shoeGrid;
    }

    public IntegerField getShoeSizeField() {
        return shoeSizeField;
    }

    public void setShoeSizeField(IntegerField shoeSizeField) {
        this.shoeSizeField = shoeSizeField;
    }

    public IntegerField getShoeAmountField() {
        return shoeAmountField;
    }

    public void setShoeAmountField(IntegerField shoeAmountField) {
        this.shoeAmountField = shoeAmountField;
    }

    public HorizontalLayout getAmountLayout() {
        return amountLayout;
    }

    public void setAmountLayout(HorizontalLayout amountLayout) {
        this.amountLayout = amountLayout;
    }

    public HorizontalLayout getSizeLayout() {
        return sizeLayout;
    }

    public void setSizeLayout(HorizontalLayout sizeLayout) {
        this.sizeLayout = sizeLayout;
    }

    public BowlingShoeRepository getBowlingShoeRepository() {
        return bowlingShoeRepository;
    }

    public void setBowlingShoeRepository(BowlingShoeRepository bowlingShoeRepository) {
        this.bowlingShoeRepository = bowlingShoeRepository;
    }

    public Map<Integer, Integer> getShoeSizeAmountMap() {
        return shoeSizeAmountMap;
    }

    public void setShoeSizeAmountMap(Map<Integer, Integer> shoeSizeAmountMap) {
        this.shoeSizeAmountMap = shoeSizeAmountMap;
    }
}