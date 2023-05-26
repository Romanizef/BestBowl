package de.softwareprojekt.bestbowl.views.bookingViews;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodBookingRepository;
import de.softwareprojekt.bestbowl.utils.email.MailSenderService;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.extrasElements.DrinkPanel;
import de.softwareprojekt.bestbowl.views.extrasElements.FoodPanel;
import de.softwareprojekt.bestbowl.views.extrasElements.ShoePanel;
import jakarta.annotation.security.PermitAll;

/**
 * Creates a view for all booked elements to be displayed in a invoice and paid
 * 
 * @author Matija Kopschek
 */
@Route(value = "invoice", layout = MainView.class)
@PageTitle("Rechnung")
@PermitAll
@PreserveOnRefresh
public final class InvoiceView extends VerticalLayout {
    private final transient MailSenderService mailSenderController;
    private TabSheet tabs;
    private HorizontalLayout tabLayout;
    private HorizontalLayout buttonLayout;
    private BowlingAlleyBooking bowlingAlleyBooking;
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private final H1 clientHeader;
    private Tab totalinvoice;

    /**
     * Constructor for the InvoiceView class. Creates all the components
     */
    @Autowired
    public InvoiceView(BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
            DrinkBookingRepository drinkBookingRepository, FoodBookingRepository foodBookingRepository,
            BowlingShoeBookingRepository shoeBookingRepository) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.shoeBookingRepository = shoeBookingRepository;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        mailSenderController = new MailSenderService();
        clientHeader = new H1();
        Component totalInvoiceComponent = createTotalInvoice();
        Component footerComponent = createFooter();
        add(clientHeader, totalInvoiceComponent, footerComponent);
    }

    private final Component createTotalInvoice() {
        tabs = new TabSheet();
        tabLayout = new HorizontalLayout();
        totalinvoice = new Tab(VaadinIcon.MONEY.create(), new Span("Gesamtrechnung"));

        tabLayout.setMaxWidth("100%");
        tabs.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabs.setSelectedTab(tabs.add(totalinvoice, createPanels()));
        tabLayout.add(tabs);
        return tabLayout;
    }

    private final Component createPanels() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("100px", 2));
        formLayout.setMaxWidth("1000px");
        if (bowlingAlleyBooking != null) {
            List<DrinkBooking> drinkBookingList = drinkBookingRepository
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());

            for (DrinkBooking drinkBooking : drinkBookingList) {
                formLayout.add(new DrinkPanel(drinkBooking));
            }

            List<FoodBooking> foodList = foodBookingRepository
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
            for (FoodBooking foodBooking : foodList) {
                formLayout.add(new FoodPanel(foodBooking));
            }

            List<BowlingShoeBooking> shoeList = shoeBookingRepository
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
            for (BowlingShoeBooking bowlingShoeBooking : shoeList) {
                formLayout.add(new ShoePanel(bowlingShoeBooking, shoeBookingRepository));
            }
        }
        return formLayout;
    }

    public final BowlingAlleyBooking getBowlingAlleyBooking() {
        return bowlingAlleyBooking;
    }

    public final void setBowlingAlleyBooking(BowlingAlleyBooking bowlingAlleyBooking) {
        this.bowlingAlleyBooking = bowlingAlleyBooking;
        updateInitialComponents();
    }

    private final void updateInitialComponents() {
        clientHeader
                .setText("Statistiken für: " + bowlingAlleyBooking.getClient().getFirstName() + " "
                        + bowlingAlleyBooking.getClient().getLastName());

    }

    private final Component tabButtonPlacement() {
        buttonLayout = new HorizontalLayout();
        buttonLayout.setMaxWidth("100%");
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        buttonLayout.add(addTabAddButton(), addTabSubButton());
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
                tabs.setSelectedTab(tabs.add("Teilrechnung", createPanels()));
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
            if (tabs.getSelectedTab() != totalinvoice) { // .getLabel().equals("Gesamtrechnung")
                tabs.remove(tabs.getSelectedTab());
            } else {
                Notifications.showError("Gesamtrechnung nicht löschbar!");
            }
        });
        return tabSubButton;
    }

    /**
     * All Configurations of the pay button
     */
    private final Component createPayButton() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMaxWidth("100%");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        Button payButton = new Button("BEZAHLEN");
        payButton.setIcon(new Icon(VaadinIcon.CART));
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_LARGE);
        payButton.setDisableOnClick(true);
        payButton.addClickListener(clickEvent -> {
            Notifications.showInfo("Rechnung bezahlt");
            mailSenderController.sendInvoiceMail(bowlingAlleyBooking);
            bowlingAlleyBooking.setCompleted(true);
            UI.getCurrent().navigate(ExtrasView.class);
        });

        Paragraph sumHeader = new Paragraph();
        sumHeader.getStyle().set("border", "3px solid #338CFF").set("background-color", "#338CFF")
                .set("padding", "7px").set("border-radius", "10px").set("font-weight", "bold");
        sumHeader.setText("SUMME: "); // + calculateBookingTotal(bowlingAlleyBooking)
        horizontalLayout.add(payButton, sumHeader);
        return horizontalLayout;
    }

    /**
     * Calculates the total sum of prices for the current booking
     * 
     * @param bowlingAlleyBooking
     * @return {@code double} total
     */
    /*
     * private double calculateBookingTotal(BowlingAlleyBooking bowlingAlleyBooking)
     * {
     * List<DrinkBooking> drinkBookingList = drinkBookingRepository
     * .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
     * bowlingAlleyBooking.getClient(),
     * bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
     * List<FoodBooking> foodBookingList = foodBookingRepository
     * .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
     * bowlingAlleyBooking.getClient(),
     * bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
     * List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository
     * .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
     * bowlingAlleyBooking.getClient(),
     * bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
     * 
     * double total = 0.0;
     * for (DrinkBooking drinkBooking : drinkBookingList) {
     * total += drinkBooking.getPrice() * drinkBooking.getAmount();
     * }
     * for (FoodBooking foodBooking : foodBookingList) {
     * total += foodBooking.getPrice() * foodBooking.getAmount();
     * }
     * for (BowlingShoeBooking shoeBooking : shoeBookingList) {
     * total += shoeBooking.getPrice();
     * }
     * 
     * return total;
     * }
     */

    private final Component createFooter() {
        VerticalLayout verticallayout = new VerticalLayout();
        verticallayout.setWidth("80%");
        verticallayout.setAlignItems(Alignment.CENTER);
        verticallayout.add(tabButtonPlacement(), createPayButton());
        return verticallayout;
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
            Notifications.showInfo("Teilrechnung bezahlt");
            bowlingAlleyBooking.setCompleted(true);
            updateCompleteInvoice();
            // TODO Alle Elemente sperren Children nochmal Angucken bei anderen Verwaltungen
            // setChildrenEnabled(tabs.getChildren(), selectedAssociation != null);
            // TODO ExtraView auf erste Belegte Bahn weiterleiten
        });
        return payButton;
    }

    // TODO wenn teilrechnung bezahlt wird muss die gesamtrechnung aktualisiert
    // werden
    private void updateCompleteInvoice() {

    }
}
