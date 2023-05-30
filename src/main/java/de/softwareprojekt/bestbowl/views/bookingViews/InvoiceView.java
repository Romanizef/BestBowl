package de.softwareprojekt.bestbowl.views.bookingViews;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.setChildrenEnabled;

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
import com.vaadin.flow.component.tabs.TabVariant;
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
import de.softwareprojekt.bestbowl.utils.VaadinUtils;
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
    private BowlingAlleyBooking bowlingAlleyBooking;
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private final H1 clientHeader;
    private final H1 alleyHeader;
    private Tab totalInvoice;
    private Tab partialInvoice;
    private TabSheet invoiceTabSheet;

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
        alleyHeader = new H1();
        invoiceTabSheet = new TabSheet();
        add(clientHeader, alleyHeader, invoiceTabSheet);
    }

    private Component createTabs() {
        invoiceTabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                // TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);

        createTotalTab();
        createPartialTab();
        invoiceTabSheet.getStyle().set("border", "3px solid blue").set("border-radius", "10px");
        invoiceTabSheet.setMaxWidth("100%");
        return invoiceTabSheet;
    }

    private Tab createTotalTab() {
        totalInvoice = new Tab(VaadinIcon.MONEY.create(), new Span("Gesamtrechnung"));
        invoiceTabSheet.setSelectedTab(invoiceTabSheet.add(totalInvoice, createTotalInvoicePanels()));
        totalInvoice.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        totalInvoice.getElement().addEventListener("click", e -> {
            updateTotalInvoice();
        });
        return totalInvoice;
    }

    private Tab createPartialTab() {
        partialInvoice = new Tab(VaadinIcon.MONEY.create(), new Span("Teilrechnung"));
        invoiceTabSheet.add(partialInvoice, createPartialInvoicePanels());
        partialInvoice.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        return partialInvoice;
    }

    private final Component createTotalInvoicePanels() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("200px", 2));
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
        formLayout.add(createTotalInvoiceFooter());
        return formLayout;
    }

    private final Component createPartialInvoicePanels() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("200px", 2));
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
        formLayout.add(createPartialInvoiceFooter());
        return formLayout;
    }

    public final void setBowlingAlleyBooking(BowlingAlleyBooking bowlingAlleyBooking) {
        this.bowlingAlleyBooking = bowlingAlleyBooking;
        updateInitialComponents();
        createTabs();
    }

    private final void updateInitialComponents() {
        clientHeader
                .setText("Rechnung für: " + bowlingAlleyBooking.getClient().getFirstName() + " "
                        + bowlingAlleyBooking.getClient().getLastName());
        alleyHeader
                .setText("Bahn " + bowlingAlleyBooking.getBowlingAlley().getId() + ": "
                        + bowlingAlleyBooking.getPrice() + " €");
    }

    /**
     * Calculates the total sum of prices for the current booking
     * 
     * @param bowlingAlleyBooking
     * @return {@code double} total
     */

    private double calculateBookingTotal() {
        double total = 0.0;
        if (bowlingAlleyBooking != null) {
            List<DrinkBooking> drinkBookingList = drinkBookingRepository
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
                            bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
            List<FoodBooking> foodBookingList = foodBookingRepository
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
                            bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
            List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository
                    .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
                            bowlingAlleyBooking.getClient(),
                            bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());

            for (DrinkBooking drinkBooking : drinkBookingList) {
                total += drinkBooking.getPrice() * drinkBooking.getAmount();
            }
            for (FoodBooking foodBooking : foodBookingList) {
                total += foodBooking.getPrice() * foodBooking.getAmount();
            }
            for (BowlingShoeBooking shoeBooking : shoeBookingList) {
                total += shoeBooking.getPrice();
            }
            total += bowlingAlleyBooking.getPrice();
        }
        return total;
    }

    private final Component createTotalInvoiceFooter() {
        VerticalLayout verticallayout = new VerticalLayout();
        verticallayout.setWidth("100%");
        verticallayout.setAlignItems(Alignment.CENTER);
        verticallayout.add(createPayButton());
        return verticallayout;
    }

    private final Component createPartialInvoiceFooter() {
        VerticalLayout verticallayout = new VerticalLayout();
        verticallayout.setWidth("100%");
        verticallayout.setAlignItems(Alignment.CENTER);
        verticallayout.add(createPartialPayButton());
        return verticallayout;
    }

    /**
     * All Configurations of the pay button
     */
    private final HorizontalLayout createPartialPayButton() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMaxWidth("100%");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        Button partialpayButton = new Button("BEZAHLEN");
        partialpayButton.setIcon(new Icon(VaadinIcon.CART));
        partialpayButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_LARGE);
        partialpayButton.setDisableOnClick(true);
        partialpayButton.addClickListener(clickEvent -> {
            VaadinUtils.showConfirmationDialog("Teilrechnung bezahlen?", "Ja", "Abbrechen", () -> {
                Notifications.showInfo("Teilrechnung bezahlt");
            });
            setChildrenEnabled(partialInvoice.getChildren(), false);
            createPartialTab();
        });
        horizontalLayout.add(partialpayButton, createSumHeader());
        return horizontalLayout;
    }

    /**
     * All Configurations of the pay button
     */
    private final HorizontalLayout createPayButton() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMaxWidth("100%");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        Button payButton = new Button("BEZAHLEN");
        payButton.setIcon(new Icon(VaadinIcon.CART));
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_LARGE);
        payButton.setDisableOnClick(true);
        payButton.addClickListener(clickEvent -> {
            VaadinUtils.showConfirmationDialog("Rechnung bezahlen?", "Ja", "Abbrechen", () -> {
                Notifications.showInfo("Rechnung bezahlt");
            });
            mailSenderController.sendInvoiceMail(bowlingAlleyBooking);
            bowlingAlleyBooking.setCompleted(true);
            UI.getCurrent().navigate(ExtrasView.class);
        });

        horizontalLayout.add(payButton, createSumHeader());
        return horizontalLayout;
    }

    private Paragraph createSumHeader() {
        Paragraph sumHeader = new Paragraph();
        sumHeader.getStyle().set("border", "3px solid #338CFF").set("background-color", "#338CFF")
                .set("padding", "7px").set("border-radius", "10px").set("font-weight", "bold");
        sumHeader.setText("SUMME: " + calculateBookingTotal());
        return sumHeader;
    }

    // TODO wenn teilrechnung bezahlt wird und der Gesamtrechnungstab geclickt wird
    // muss die gesamtrechnung aktualisiert werden
    private void updateTotalInvoice() {

    }
}
