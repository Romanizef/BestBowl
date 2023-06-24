package de.softwareprojekt.bestbowl.views.articlePanels;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeRepository;

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
    private final transient BowlingShoeRepository bowlingShoeRepository;
    private final IntegerField shoeSizeField;
    private final IntegerField shoeAmountField;
    private final int minShoeSize;
    private final int maxShoeSize;
    private Map<Integer, Integer> shoeSizeAmountMap;

    /**
     * The ShoePanel function creates a panel that allows the user to select shoes
     * for their client.
     *
     * @param bowlingShoeRepository
     * @param client
     * @see #addCSS()
     * @see #createShoeGrid(Client)
     */
    public ShoePanel(BowlingShoeRepository bowlingShoeRepository, Client client, BowlingCenter bowlingCenter) {
        this.bowlingShoeRepository = bowlingShoeRepository;
        minShoeSize = bowlingCenter.getMinShoeSize();
        maxShoeSize = bowlingCenter.getMaxShoeSize();
        List<BowlingShoe> shoeList = bowlingShoeRepository.findAllByClientIsNullAndActiveIsTrue();
        int value;
        shoeSizeAmountMap = new HashMap<>();
        for (int i = minShoeSize; i < maxShoeSize; i++) {
            shoeSizeAmountMap.put(i, 0);
        }
        for (BowlingShoe shoe : shoeList) {
            if (shoe.isActive() && (shoeSizeAmountMap.containsKey(shoe.getSize()))) {
                value = shoeSizeAmountMap.get(shoe.getSize());
                value++;
                shoeSizeAmountMap.put(shoe.getSize(), value);
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

        shoeSizeField.setMin(minShoeSize);
        shoeSizeField.setMax(maxShoeSize);
        shoeSizeField.setValue((minShoeSize + maxShoeSize) / 2);
        shoeSizeField.addValueChangeListener(integerFieldIntegerComponentValueChangeEvent ->
                shoeAmountField.setMax(shoeSizeAmountMap.get(shoeSizeField.getValue())));
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
     * The addCSS function adds CSS styling to the panel.
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
     * The updateShoeSizeAmountMap function updates the shoeSizeAmountMap HashMap
     * with the current amount of available shoes for each size.
     * The function is called whenever a new client is added or removed from the
     * database, so that it can be displayed in real time on the frontend.
     */
    public void updateShoeSizeAmountMap() {
        List<BowlingShoe> shoeList = bowlingShoeRepository.findAllByClientIsNullAndActiveIsTrue();
        int value;
        shoeSizeAmountMap = new HashMap<>();
        for (int i = minShoeSize; i < maxShoeSize; i++) {
            shoeSizeAmountMap.put(i, 0);
        }
        for (BowlingShoe shoe : shoeList) {
            if (shoe.isActive() && (shoeSizeAmountMap.containsKey(shoe.getSize()))) {
                value = shoeSizeAmountMap.get(shoe.getSize());
                value++;
                shoeSizeAmountMap.put(shoe.getSize(), value);
            }
        }
    }

    /**
     * The resetIntegerField function resets the value of the shoeAmountField to 0.
     */
    public void resetIntegerField() {
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