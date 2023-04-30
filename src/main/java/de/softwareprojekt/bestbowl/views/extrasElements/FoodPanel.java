package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
public class FoodPanel implements PanelInterface {
    private FormLayout kachelLayout;
    private Grid<FoodBooking> shoeGrid;

    public FoodPanel() {

    }

    public FormLayout addPanelComponent() {
        kachelLayout = new FormLayout();
        kachelLayout.setResponsiveSteps(new ResponsiveStep("0", 2));

        IntegerField foodField = new IntegerField();
        foodField.setValue(2);
        foodField.setStepButtonsVisible(true);
        foodField.setMin(0);
        foodField.setMax(9);  //TODO Lagerbestand muss aus der DB ausgelesen werden und als max gesetzt
        foodField.setLabel("Speisename");

        kachelLayout.add(foodField);
        addCSS();
        return kachelLayout;
    }

     private void addCSS() {
        kachelLayout.getStyle().set("border", "1px solid #1b7513");
        kachelLayout.getStyle().set("background-color", "#2dbf21");
    }
}
