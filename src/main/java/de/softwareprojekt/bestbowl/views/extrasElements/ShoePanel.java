package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.PANEL_COLOR_SHOE;

/**
 * Class for the Shoe Panels in the ExtrasView.
 *
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class ShoePanel extends VerticalLayout {
    private FormLayout kachelLayout;
    private IntegerField shoeSizeField;
    private IntegerField shoeAmountField;
    private BowlingShoeRepository bowlingShoeRepository;
    private Map<Integer, Integer> shoeSizeAmountMap;

    /**
     * Constructor for the ShoePanel.
     *
     * @param bowlingShoeRepository
     * @param client
     * @see #addCSS()
     * @see #createShoeGrid(Client)
     */
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

    /**
     * Adds CSS style to the ShoePanel.
     */
    private void addCSS() {
        getStyle()
                .set("border", "2px solid " + PANEL_COLOR_SHOE)
                .set("background-color", PANEL_COLOR_SHOE + "10")
                .set("padding", "10px")
                .set("margin-bottom", "10px")
                .set("border-radius", "20px");
    }

    /**
     * Updates the amount of shoes of a certain size.
     */
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

    /**
     * Resets the shoeAmountField to the value 0.
     */
    public void resetIntergerfield() {
        shoeAmountField.setValue(0);
    }

    /**
     * Creates a {@code Grid} with the {@code BowlingShoe}s of the client.
     *
     * @param client
     * @return {@code Grid<BowlingShoe>}
     */
    private Grid<BowlingShoe> createShoeGrid(Client client) {
        Grid<BowlingShoe> grid = new Grid<>(BowlingShoe.class, false);
        grid.addColumn(BowlingShoe::getId).setHeader("Schuh ID");
        grid.addColumn(BowlingShoe::getSize).setHeader("Schuhgröße");
        List<BowlingShoe> bowlingShoeList = bowlingShoeRepository.findAllByClientEqualsAndActiveIsTrue(client);
        bowlingShoeList.sort(Comparator.comparingInt(BowlingShoe::getSize));
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES);
        grid.setItems(bowlingShoeList);
        return grid;
    }

    /**
     * Getter for the {@code IntegerField} shoeSizeField.
     *
     * @return {@code IntegerField}
     */
    public IntegerField getShoeSizeField() {
        return shoeSizeField;
    }

    /**
     * Getter for the {@code IntegerField} shoeAmountField.
     *
     * @return {@code IntegerField}
     */
    public IntegerField getShoeAmountField() {
        return shoeAmountField;
    }

    /**
     * Getter for the {@code Map<Integer, Integer>} shoeSizeAmountMap.
     *
     * @return {@code Map<Integer, Integer>}
     */
    public Map<Integer, Integer> getShoeSizeAmountMap() {
        return shoeSizeAmountMap;
    }
}