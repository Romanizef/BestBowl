package de.softwareprojekt.bestbowl.views.bookingViews;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodBookingRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.VaadinUtils;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.PermitAll;

/**
 * Creates a View with all the not yet paid bookings
 * 
 * @author Matija
 */
@Route(value = "pendingBookings", layout = MainView.class)
@PageTitle("Offene Buchungen")
@PermitAll
@PreserveOnRefresh
public class PendingBookingView extends VerticalLayout {

    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private Grid<BowlingAlleyBooking> bookingGrid;
    private Button payButton;
    private Button lastViewButton;
    private BowlingAlleyBooking currentBowlingAlleyBooking;
    private final H1 clientHeader;
    private Client currentClient;

    @Autowired
    public PendingBookingView(BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
    DrinkBookingRepository drinkBookingRepository, FoodBookingRepository foodBookingRepository,
    BowlingShoeBookingRepository shoeBookingRepository) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.shoeBookingRepository = shoeBookingRepository;
        setSizeFull();
        clientHeader = new H1();
        HorizontalLayout gridLayout = createGridLayout();
        Component footerComponent = createFooterComponent();
        add(clientHeader, gridLayout, footerComponent);
    }

    /**
     * Setter for the current client
     * 
     * @param selectedClient
     */
    public void setSelectedClient(Client selectedClient) {
        this.currentClient = selectedClient;
        updateInitialComponents();
        updateGridItems();
        currentBowlingAlleyBooking =  new BowlingAlleyBooking();
        currentBowlingAlleyBooking.setClient(selectedClient);
    }

    /**
     * Updates all the grid items to the latest state
     */
    private void updateGridItems() {
        List<BowlingAlleyBooking> bowlingalleybookinglist = bowlingAlleyBookingRepository
                .findAllByClientEquals(currentClient);
        bookingGrid.setItems(
                bowlingalleybookinglist);

    }

    /**
     * If no client ist selected a message apears. A {@code H1} shows the currently
     * selected client
     */
    private void updateInitialComponents() {
        if (currentClient == null) {
            Notifications.showError("Kein Kunde ausgewählt!");
            UI.getCurrent().navigate(ClientSearchView.class);
        } else {
            clientHeader
                    .setText("Nicht bezahlte Buchungen von: " + currentClient.getFirstName() + " "
                            + currentClient.getLastName());
        }
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        bookingGrid = createGrid();
        layout.add(bookingGrid);
        return layout;
    }

    /**
     * Creates a grid with the clients statistics
     * 
     * @return {@code Grid<BowlingAlleyBooking>}
     */
    private Grid<BowlingAlleyBooking> createGrid() {
        Grid<BowlingAlleyBooking> grid = new Grid<>(BowlingAlleyBooking.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();

        grid.addColumn("id").setHeader("Buchungsnummer");
        grid.addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getId())
                .setHeader("Kundennummer").setSortable(true);
        grid.addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getLastName())
                .setHeader("Kundennachname").setSortable(true);
        grid.addColumn(booking -> Utils.toDateString(booking.getStartTime())).setHeader("Datum");
        grid.addColumn(booking -> calculateBookingTotal(booking)).setHeader("Summe");
        grid.addColumn("completed").setHeader("Bezahlt?");

        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        return grid;
    }

    /**
     * 
     *
     * @return {@code Component}
     */
    private Component createFooterComponent() {
        VerticalLayout verticallayout = new VerticalLayout();
        verticallayout.setWidth("80%");
        verticallayout.setAlignItems(Alignment.CENTER);
        verticallayout.add(createPayButton(verticallayout), createLastViewButton(verticallayout));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.add(verticallayout);

        return horizontalLayout;
    }

    private Button createPayButton(VerticalLayout layout) {
        payButton = new Button("Bezahlen");
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        payButton.setIcon(new Icon(VaadinIcon.MONEY_EXCHANGE));
        payButton.setWidth("50%");
        payButton.addClickListener(e -> {

            VaadinUtils.showConfirmationDialog("Rechnung bezahlen?", "Ja", "Abbrechen", () -> {
                UI.getCurrent().navigate(InvoiceView.class, currentBowlingAlleyBooking.getId());
            });
        });
        return payButton;
    }

    /**
     * Creates a {@code Button} that returns the user to the ClientSearchView
     * 
     * @param layout
     * @return {@code Button} lastViewButton
     */
    private Button createLastViewButton(VerticalLayout layout) {
        lastViewButton = new Button("Zurück zu Kundensuche");
        lastViewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        lastViewButton.setIcon(new Icon(VaadinIcon.BACKWARDS));
        lastViewButton.setWidth("50%");
        lastViewButton.addClickListener(e -> UI.getCurrent().navigate(ClientSearchView.class));
        return lastViewButton;
    }

        /**
     * Calculates the total sum of prices for the current booking
     * 
     * @param bowlingAlleyBooking
     * @return {@code double} total
     */
    private double calculateBookingTotal(BowlingAlleyBooking bowlingAlleyBooking) {
        List<DrinkBooking> drinkBookingList = drinkBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
        List<FoodBooking> foodBookingList = foodBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
        List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());

        double total = 0.0;
        for (DrinkBooking drinkBooking : drinkBookingList) {
            total += drinkBooking.getPrice() * drinkBooking.getAmount();
        }
        for (FoodBooking foodBooking : foodBookingList) {
            total += foodBooking.getPrice() * foodBooking.getAmount();
        }
        for (BowlingShoeBooking shoeBooking : shoeBookingList) {
            total += shoeBooking.getPrice();
        }
        return total;
    }
}