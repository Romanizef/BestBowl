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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.showNotification;

/**
 * @author Matija Kopschek
 */
@Route(value = "invoice", layout = MainView.class)
@PageTitle("Rechnung")
@PermitAll
public final class InvoiceView extends VerticalLayout {
    private TabSheet tabs;
    private HorizontalLayout tabLayout;
    private HorizontalLayout buttonLayout;
    private Button tabSubButton;
    private Button tabAddButton;
    private Button payButton;
    private Button closeButton;
    private Notification errorNotification;

    public InvoiceView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        Component tabComponent = tabConfig();
        Component tabButtonComponent = tabButtonPlacement();
        Component footerComponent = createFooterComponent();
        add(tabComponent, tabButtonComponent, footerComponent);
    }

    private final Component tabConfig() {
        tabs = new TabSheet();
        tabLayout = new HorizontalLayout();
        tabLayout.setMaxWidth("100%");
        tabs.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabs.add("Gesamtrechnung",
                new Div(new Text("This is the Gesamtrechnung tab content")));

        tabLayout.add(tabs);
        return tabLayout;
    }

    private final Component tabButtonPlacement() {
        buttonLayout = new HorizontalLayout();
        buttonLayout.setMaxWidth("100%");
        buttonLayout.add(addTabAddButton(), addTabSubButton());
        return buttonLayout;
    }

    /**
     * @param tabs
     * @return
     */
    private final Button addTabAddButton() {
        tabAddButton = new Button("Teilrechung hinzufügen");
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
    private final Button addTabSubButton() {
        tabSubButton = new Button("Teilrechung löschen");
        tabSubButton.setIcon(new Icon(VaadinIcon.MINUS_CIRCLE));
        tabSubButton.addClickListener(e -> {
            if (!tabs.getSelectedTab().getLabel().equals("Gesamtrechnung")) {
                tabs.remove(tabs.getSelectedTab());
            } else {
                showTabDeletionErrorNotification();
            }
        });
        return tabSubButton;
    }

    private Notification showTabDeletionErrorNotification() {
        errorNotification = new Notification();
        errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        Div text = new Div(new Text("Gesamtrechnung nicht löschbar!"));

        closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            errorNotification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        errorNotification.add(layout);
        errorNotification.open();
        return errorNotification;
    }

    /**
     * All Configurations of the pay button
     */
    private final Component createFooterComponent() {
        payButton = new Button("Bezahlen");
        payButton.setIcon(new Icon(VaadinIcon.CART));
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_LARGE);
        payButton.setDisableOnClick(true);
        payButton.addClickListener(clickEvent -> {
            showNotification("Rechnung bezahlt");
        });
        return payButton;
    }
}