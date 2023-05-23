package de.softwareprojekt.bestbowl.views.otherViews;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

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
import de.softwareprojekt.bestbowl.utils.pdf.PDFUtils;
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
public class StatisticsView extends VerticalLayout {
    private Grid<BowlingAlleyBooking> bookingGrid;
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private Client currentClient = null;
    private Button lastViewButton;
    private Button refreshButton;
    private final H1 clientHeader;

    @Autowired
    public StatisticsView(BowlingAlleyBookingRepository bookingRepository,
            DrinkBookingRepository drinkBookingRepository, FoodBookingRepository foodBookingRepository,
            BowlingShoeBookingRepository shoeBookingRepository) {
        this.bowlingAlleyBookingRepository = bookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.shoeBookingRepository = shoeBookingRepository;
        clientHeader = new H1();
        setSizeFull();
        bookingGrid = createGrid();
        Component footerComponent = createFooterComponent();
        add(clientHeader, bookingGrid, footerComponent);
        updateGridItems();
    }

    // setter für aktuellen Kunden
    public void setSelectedClient(Client selectedClient) {
        this.currentClient = selectedClient;
        updateInitialComponents();
        updateGridItems();
    }

    // Message wenn kein Kunde ausgewählt
    // Text feld welcher kunde ausgewählt
    private void updateInitialComponents() {
        if (currentClient == null) {
            clientHeader.setText("Statistiken für: (kein Kunde ausgewählt)");
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
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.add(createLastViewButton(layout), createRefreshButton(layout));
        return layout;
    }

    private Button createLastViewButton(VerticalLayout layout) {
        lastViewButton = new Button("Zurück zu Kundensuche");
        lastViewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        lastViewButton.setIcon(new Icon(VaadinIcon.BACKWARDS));
        lastViewButton.setWidth("55%");
        lastViewButton.addClickListener(e -> UI.getCurrent().navigate(ClientSearchView.class));
        return lastViewButton;
    }

    // refresh button der updateGridItems aufruft (falls kunde leer nicht
    // aktuallisieren, zurück zu kundensuche)
    private Button createRefreshButton(VerticalLayout layout) {
        refreshButton = new Button("Aktualisieren");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshButton.setIcon(new Icon(VaadinIcon.REFRESH));
        refreshButton.setWidth("55%");
        lastViewButton.addClickListener(e -> {
            if (this.currentClient != null) {
                updateGridItems();
            } else {
                UI.getCurrent().navigate(ClientSearchView.class);
            }
        });
        return refreshButton;
    }

    private void updateGridItems() {
        List<BowlingAlleyBooking> bowlingalleybookinglist = bowlingAlleyBookingRepository
                .findAllByClientEquals(currentClient);
        bookingGrid.setItems(
                bowlingalleybookinglist);

    }

    /**
     * @return
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

        /*
         * grid.addSelectionListener(e ->
         * 
         * {
         * if (e.isFromClient()) {
         * Optional<BowlingAlleyBooking> optionalStatistic = e.getFirstSelectedItem();
         * selectedBowlingAlleyBooking = optionalStatistic.orElse(null);
         * updateFooterComponents();
         * }
         * });
         */
        return grid;
    }

    private double calculateBookingTotal(BowlingAlleyBooking bowlingAlleyBooking) {
        List<DrinkBooking> drinkBookingList = drinkBookingRepository.findByKey(bowlingAlleyBooking);
        List<FoodBooking> foodBookingList = foodBookingRepository.findByKey(bowlingAlleyBooking);
        List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository.findByKey(bowlingAlleyBooking);

        double total = 0.0;
        for (DrinkBooking drinkBooking : drinkBookingList) {
            total += drinkBooking.getPrice();
        }
        for (FoodBooking foodBooking : foodBookingList) {
            total += foodBooking.getPrice();
        }
        for (BowlingShoeBooking shoeBooking : shoeBookingList) {
            total += shoeBooking.getPrice();
        }

        return total;
    }
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
            total += drinkBooking.getPrice();
        }
        for (FoodBooking foodBooking : foodBookingList) {
            total += foodBooking.getPrice();
        }
        for (BowlingShoeBooking shoeBooking : shoeBookingList) {
            total += shoeBooking.getPrice();
        }

        return total;
    }

}
