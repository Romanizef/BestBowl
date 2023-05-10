package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.extrasElements.DrinkPanel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.showNotification;

import java.util.List;

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
    private Notification errorNotification;
    private BowlingAlleyBooking bowlingAlleyBooking;

    public InvoiceView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        Component tabComponent = tabConfig();
        Component addButton = addTabAddButton();
        Component subButton = addTabSubButton();
        Component tabButtonComponent = tabButtonPlacement(addButton, subButton);
        Component footerComponent = createPayButton();
        add(tabComponent, tabButtonComponent, footerComponent);
    }

    private final Component tabConfig() {
        tabs = new TabSheet();
        tabLayout = new HorizontalLayout();
        tabLayout.setMaxWidth("100%");
        tabs.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabs.add("Gesamtrechnung", createCompleteInvoice());
        tabLayout.add(tabs);
        return tabLayout;
    }

    //TODO food und shoe
    public Component createCompleteInvoice() {
        VerticalLayout verticalLayout = new VerticalLayout();
        if (bowlingAlleyBooking != null) {
            List<DrinkBooking> drinkBookingList = Repos.getDrinkBookingRepository()
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());

            verticalLayout.setAlignItems(Alignment.CENTER);
            verticalLayout.setWidthFull();

            for (DrinkBooking drinkBooking : drinkBookingList) {
                verticalLayout.add(new DrinkPanel(drinkBooking));
            }
        }
        return verticalLayout;
    }

    //TODO food und shoe
    public Component createPartialInvoice() {
        VerticalLayout verticalLayout = new VerticalLayout();
        if (bowlingAlleyBooking != null) {
            List<DrinkBooking> drinkBookingList = Repos.getDrinkBookingRepository()
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());

            verticalLayout.setAlignItems(Alignment.CENTER);
            verticalLayout.setWidthFull();

            for (DrinkBooking drinkBooking : drinkBookingList) {
                verticalLayout.add(new DrinkPanel(drinkBooking));
            }
        }
        verticalLayout.add(createPartialPayButton());
        return verticalLayout;
    }

    public void setBowlingAlleyBooking(BowlingAlleyBooking bowlingAlleyBooking) {
        this.bowlingAlleyBooking = bowlingAlleyBooking;
    }

    public BowlingAlleyBooking getBowlingAlleyBooking(){
        return bowlingAlleyBooking;
    }

    private final Component tabButtonPlacement(Component addButton, Component subButton) {
        buttonLayout = new HorizontalLayout();
        buttonLayout.setMaxWidth("100%");
        buttonLayout.add(addButton, subButton);
        return buttonLayout;
    }

    /**
     * @param partialBill
     * @param tabs
     * @return
     */
    private final Button addTabAddButton() {
        Button tabAddButton = new Button("Teilrechung hinzufügen");
        tabAddButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
        tabAddButton.addClickListener(event -> {
            if (bowlingAlleyBooking != null) {
                tabs.setSelectedTab(tabs.add("Teilrechnung", createPartialInvoice()));
            }
        });
        return tabAddButton;
    }

    /**
     * @param tabs
     * @return
     */
    private final Button addTabSubButton() {
        Button tabSubButton = new Button("Teilrechung löschen");
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

    private final Notification showTabDeletionErrorNotification() {
        errorNotification = new Notification();
        errorNotification.setPosition(Position.BOTTOM_CENTER);
        errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text("Gesamtrechnung nicht löschbar!"));
        Icon icon = VaadinIcon.WARNING.create();

        HorizontalLayout layout = new HorizontalLayout(icon, text);
        layout.setAlignItems(Alignment.CENTER);

        errorNotification.add(layout);
        errorNotification.setDuration(3000);
        errorNotification.open();
        return errorNotification;
    }

    /**
     * All Configurations of the pay button
     */
    private final Component createPayButton() {
        Button payButton = new Button("Bezahlen");
        payButton.setIcon(new Icon(VaadinIcon.CART));
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_LARGE);
        payButton.setDisableOnClick(true);
        payButton.addClickListener(clickEvent -> {
            showNotification("Rechnung bezahlt");
            // TODO pdf erstellen und per EMail versenden
            // TODO Rechnung speichern in Datenbank
        });
        return payButton;
    }

    /**
     * All Configurations of the pay button
     */
    private final Component createPartialPayButton() {
        Button payButton = new Button("Bezahlen");
        payButton.setIcon(new Icon(VaadinIcon.CART));
        payButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY,
                ButtonVariant.LUMO_SMALL);
        payButton.setDisableOnClick(true);
        payButton.addClickListener(clickEvent -> {
            showNotification("Rechnung bezahlt");
            // TODO Summe wird geupdated
            // TODO Alle Elemente sperren Children nochmal Angucken bei anderen Verwaltungen
            // setChildrenEnabled();
            // TODO ExtraView auf erste Belegte Bahn weiterleiten
            // Zahlung und Rechnung in DB speichern
        });
        return payButton;
    }
}