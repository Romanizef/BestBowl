package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.showNotification;

@Route(value = "invoice", layout = MainView.class)
@PageTitle("Rechnung")
@PermitAll
/**
 * @author Matija Kopschek
 */
public final class InvoiceView extends VerticalLayout {
    
    public InvoiceView() {
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        tabConfig();
        payButtonConfig();
        add(layout);
    }

    private final void tabConfig() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        final TabSheet tabs = new TabSheet();
        tabs.setMaxWidth("100%");
        tabs.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabs.add("Gesamtrechnung",
                new Div(new Text("This is the Gesamtrechnung tab content")));

        buttonLayout.add(addTabAddButton(tabs), addTabSubButton(tabs));
        add(tabs, buttonLayout);
    }

    /**
     * @param tabs
     * @return
     */
    private final Button addTabAddButton(TabSheet tabs) {
        Button tabAddButton = new Button("Teilrechung hinzufügen");
        tabAddButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
        tabAddButton.addClickListener(event -> {
            tabs.add("Teilrechnung",
                    new Div(new Text("This is the Teilrechnung tab content")));
        });
        return tabAddButton;
    }

    /**
     * @param tabs
     * @return
     */
    private final Button addTabSubButton(TabSheet tabs) {
        Button tabSubButton = new Button("Teilrechung löschen");
        tabSubButton.setIcon(new Icon(VaadinIcon.MINUS_CIRCLE));
        tabSubButton.addClickListener(event -> {
            // TODO
        });
        return tabSubButton;
    }

    /**
     * All Configurations of the pay button
     */
    private final void payButtonConfig() {
        Button payButton = new Button("Bezahlen");
        payButton.setIcon(new Icon(VaadinIcon.CART));
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_LARGE);
        payButton.setDisableOnClick(true);
        payButton.addClickListener(clickEvent -> {
            showNotification("Rechnung bezahlt");
        });

        add(payButton);
    }
}