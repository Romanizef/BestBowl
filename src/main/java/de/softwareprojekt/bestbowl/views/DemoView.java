package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.showNotification;

/**
 * @author Marten Voß
 */
@Route(value = "demo", layout = MainView.class)
@PageTitle("Demo Page")
@RouteAlias(value = "", layout = MainView.class)
@PermitAll
public class DemoView extends VerticalLayout {
    private final Button testButton = new Button("Click me");

    public DemoView() {
        setSizeFull();
        HorizontalLayout layout1 = new HorizontalLayout();
        layout1.setAlignItems(Alignment.BASELINE);
        layout1.add(testButton);
        add(layout1);
        testButton.addClickListener(e -> showNotification("Hello there"));
    }
}
