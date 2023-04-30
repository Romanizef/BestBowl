package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;

public class DrinkPanel implements Panel{
    private FormLayout kachelLayout;
    private Grid<DrinkBooking> shoeGrid;

    public DrinkPanel() {
        
    }

    public FormLayout addPanelComponent() {

        kachelLayout = new FormLayout();
        kachelLayout.setResponsiveSteps(new ResponsiveStep("0", 2));

        IntegerField shoeSizeField = new IntegerField();
        shoeSizeField.setValue(2);
        shoeSizeField.setStepButtonsVisible(true);
        shoeSizeField.setMin(0);
        shoeSizeField.setMax(9);
        shoeSizeField.addValueChangeListener(e -> {

        });
        kachelLayout.addFormItem(shoeSizeField, "Größe: ");

        IntegerField shoeAmountField = new IntegerField();
        shoeAmountField.setValue(2);
        shoeAmountField.setStepButtonsVisible(true);
        shoeAmountField.setMin(0);
        shoeAmountField.setMax(9);  //TODO Lagerbestand muss aus der DB ausgelesen werden und als max gesetzt

        kachelLayout.addFormItem(shoeAmountField, "Menge: ");

        return kachelLayout;
    }

}
