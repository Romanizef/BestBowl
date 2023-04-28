package de.softwareprojekt.bestbowl.views.extrasElements;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ButtonLayout extends VerticalLayout {

    public ButtonLayout() {
        // Create the first row of buttons
        HorizontalLayout row1 = new HorizontalLayout();
        for (int i = 1; i <= 5; i++) {
            Button button = new Button("Button " + i);
            row1.add(button);
        }

        // Create the second row of buttons
        HorizontalLayout row2 = new HorizontalLayout();
        for (int i = 6; i <= 10; i++) {
            Button button = new Button("Button " + i);
            row2.add(button);
        }

        // Add both rows to the vertical layout
        add(row1, row2);
    }
}

