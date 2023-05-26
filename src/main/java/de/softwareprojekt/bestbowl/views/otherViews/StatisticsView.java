package de.softwareprojekt.bestbowl.views.otherViews;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
import de.softwareprojekt.bestbowl.utils.components.InvoiceDownloadButton;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.bookingViews.ClientSearchView;
import jakarta.annotation.security.RolesAllowed;

/**
 * Creates a view for all bookings to be displayed and downloaded
 * 
 * @author Matija Kopschek
 */
@Route(value = "statistics", layout = MainView.class)
@PageTitle("Statistiken")
@RolesAllowed({ UserRole.OWNER, UserRole.ADMIN })
@PreserveOnRefresh
public class StatisticsView extends VerticalLayout {
    private Grid<BowlingAlleyBooking> bookingGrid;
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private Client currentClient;
    private Button lastViewButton;
    private Button refreshButton;
    private final H1 clientHeader;
    private final H5 sumHeader;

    /**
     * Constructor for the StatsticView class. Creates all the components
     * 
     * @param bookingRepository
     * @param drinkBookingRepository
     * @param foodBookingRepository
     * @param shoeBookingRepository
     */
    @Autowired
    public StatisticsView(BowlingAlleyBookingRepository bookingRepository,
            DrinkBookingRepository drinkBookingRepository, FoodBookingRepository foodBookingRepository,
            BowlingShoeBookingRepository shoeBookingRepository) {
        this.bowlingAlleyBookingRepository = bookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.shoeBookingRepository = shoeBookingRepository;
        clientHeader = new H1();
        sumHeader = new H5();
        setSizeFull();
        bookingGrid = createGrid();
        Component footerComponent = createFooterComponent();
        add(clientHeader, bookingGrid, footerComponent);
        updateGridItems();
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
                    .setText("Statistiken für: " + currentClient.getFirstName() + " " + currentClient.getLastName());
        }
    }

    /**
     * Creates a {@code VerticalLayout} for the selected client label and the next
     * step button, which is used to navigate to the next view.
     *
     * @return {@code Component}
     */
    private Component createFooterComponent() {
        VerticalLayout verticallayout = new VerticalLayout();
        verticallayout.setWidth("80%");
        verticallayout.setAlignItems(Alignment.CENTER);
        verticallayout.add(createLastViewButton(verticallayout), createRefreshButton(verticallayout));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        sumHeader.setText("Summe:\n" + calculateTotal());
        sumHeader.getStyle().set("border", "5px solid #338CFF").set("background-color", "#6bbafa")
                .set("padding", "10px").set("border-radius", "30px").set("font-size", "22px");
        horizontalLayout.add(verticallayout, sumHeader);

        return horizontalLayout;
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
     * Creates a refresh button that updates the grid if a client is selected. Else
     * the user is returned to the ClientSearchView
     * 
     * @param layout
     * @return
     */
    private Button createRefreshButton(VerticalLayout layout) {
        refreshButton = new Button("Aktualisieren");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshButton.setIcon(new Icon(VaadinIcon.REFRESH));
        refreshButton.setWidth("50%");
        lastViewButton.addClickListener(e -> {
            if (this.currentClient != null) {
                updateGridItems();
            } else {
                UI.getCurrent().navigate(ClientSearchView.class);
            }
        });
        return refreshButton;
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
     * Creates a grid with the clients statistics
     * 
     * @return {@code Grid<BowlingAlleyBooking>}
     */
    private Grid<BowlingAlleyBooking> createGrid() {
        Grid<BowlingAlleyBooking> grid = new Grid<>(BowlingAlleyBooking.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();

        grid.addColumn(new ComponentRenderer<>(booking -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(new InvoiceDownloadButton(booking), new Label(String.valueOf(booking.getId())));
            return horizontalLayout;
        })).setHeader("Rechnungsnummer");
        grid.addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getId())
                .setHeader("Kundennummer").setSortable(true);
        grid.addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getLastName())
                .setHeader("Kundennachname").setSortable(true);
        grid.addColumn(booking -> Utils.toDateString(booking.getStartTime())).setHeader("Datum");
        grid.addColumn(booking -> calculateBookingTotal(booking)).setHeader("Summe");

        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        return grid;
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

    /**
     * Calculates the total sum of prices for the all the bookings
     * 
     * @return {@code double} total
     */
    private double calculateTotal() {
        List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyBookingRepository
                .findAllByClientEquals(currentClient);
        List<DrinkBooking> drinkBookingList = drinkBookingRepository.findAllByClientEquals(currentClient);
        List<FoodBooking> foodBookingList = foodBookingRepository.findAllByClientEquals(currentClient);
        List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository.findAllByClientEquals(currentClient);

        double total = 0.0;
        for (BowlingAlleyBooking alleyBooking : bowlingAlleyBookingList) {
            total += alleyBooking.getPrice();
        }
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