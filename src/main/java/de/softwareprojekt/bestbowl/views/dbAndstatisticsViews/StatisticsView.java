package de.softwareprojekt.bestbowl.views.dbAndstatisticsViews;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.VAADIN_PRIMARY_BLUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoeEntities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.clientEntities.Client;
import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.foodEntities.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlleyRepos.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoeRepos.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drinkRepos.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.foodRepos.FoodBookingRepository;
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
public class StatisticsView extends VerticalLayout implements HasUrlParameter<Integer> {
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private final H1 clientHeader;
    private final H5 sumHeader;
    private Grid<BowlingAlleyBooking> bookingGrid;
    private Client currentClient;

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
        Component headerComponent = createHeaderComponents(bookingRepository);
        add(headerComponent, bookingGrid, footerComponent);
        updateGridItems();
    }

    /**
     * Creates a {@code HorizontalLayout} headerLayout that contains the
     * clientHeader and year dropdownfilter.
     * 
     * @param bookingRepository
     * @see #createYearDropDown(BowlingAlleyBookingRepository)
     * @return {@code HorizontalLayout}
     */
    private Component createHeaderComponents(BowlingAlleyBookingRepository bookingRepository) {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.expand(clientHeader);
        headerLayout.setWidthFull();
        headerLayout.add(clientHeader, createYearDropDown(bookingRepository));
        return headerLayout;
    }

    /**
     * Creates a {@code Select<String>} select that contains all years of bookings.
     * 
     * @param bookingRepository
     * @return {@code Select<String>}
     */
    private Component createYearDropDown(BowlingAlleyBookingRepository bookingRepository) {
        List<BowlingAlleyBooking> list = bookingRepository.findAll();
        List<String> yearList = new ArrayList<>();
        Select<String> select = new Select<>();
        select.setLabel("Sortieren nach Jahr");
        select.getStyle().set("margin-right", "80px");

        for (BowlingAlleyBooking bowlingAlleyBooking : list) {
            String stringTime = Utils.toDateString(bowlingAlleyBooking.getEndTime());
            String year = stringTime.substring(6, 10);
            yearList.add(year);
        }

        select.setItems(yearList);
        select.addValueChangeListener(e -> {
            for (BowlingAlleyBooking bowlingAlleyBooking : list) {
                String stringTime = Utils.toDateString(bowlingAlleyBooking.getEndTime());
                String bookingYear = stringTime.substring(6, 10);
                if (bookingYear.equals(select.getValue())) {
                    updateGridYear(bowlingAlleyBooking.getEndTime());
                }

            }
        });
        return select;
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
        verticallayout.add(createLastViewButton(verticallayout), createRefreshButton());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        sumHeader.getStyle()
                .set("border", "5px solid " + VAADIN_PRIMARY_BLUE)
                .set("background-color", VAADIN_PRIMARY_BLUE + "60")
                .set("padding", "10px")
                .set("border-radius", "30px")
                .set("font-size", "22px");
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
        Button lastViewButton;
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
    private Button createRefreshButton() {
        Button refreshButton;
        refreshButton = new Button("Aktualisieren");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshButton.setIcon(new Icon(VaadinIcon.REFRESH));
        refreshButton.setWidth("50%");
        refreshButton.addClickListener(e -> {
            if (this.currentClient != null) {
                updateGridItems();
            } else {
                UI.getCurrent().navigate(ClientSearchView.class);
            }
        });
        return refreshButton;
    }

    /**
     * Updates all the grid items to the matching year.
     * 
     * @param l
     */
    private void updateGridYear(long l) {
        bookingGrid.setItems(
                bowlingAlleyBookingRepository
                        .findAllByTimePeriodsOverlapping(l));
    }

    /**
     * Updates all the grid items to the latest state
     */
    private void updateGridItems() {
        bookingGrid.setItems(
                bowlingAlleyBookingRepository
                        .findAllByClientEquals(currentClient));

        sumHeader.setText("Summe:\n" + calculateTotal());
    }

    /**
     * Creates a grid with the clients statistics
     *
     * @return {@code Grid<BowlingAlleyBooking>}
     */
    private Grid<BowlingAlleyBooking> createGrid() {
        bookingGrid = new Grid<>(BowlingAlleyBooking.class);
        bookingGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        bookingGrid.removeAllColumns();

        bookingGrid
                .addColumn(new ComponentRenderer<>(booking -> {
                    HorizontalLayout horizontalLayout = new HorizontalLayout();
                    horizontalLayout.add(new InvoiceDownloadButton(booking),
                            new Label(String.valueOf(booking.getId())));
                    return horizontalLayout;
                })).setHeader("Rechnungsnummer");
        bookingGrid
                .addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getId())
                .setHeader("Kundennummer").setSortable(true);
        bookingGrid
                .addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getLastName())
                .setHeader("Kundennachname").setSortable(true);
        bookingGrid
                .addColumn(booking -> Utils.toDateString(booking.getStartTime())).setHeader("Datum");
        bookingGrid.addColumn(this::calculateBookingTotal)
                .setHeader("Summe");

        bookingGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        bookingGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        bookingGrid.setSizeFull();
        // prepareFilterFields();
        return bookingGrid;
    }

    /**
     * Calculates the total sum of prices for the current booking
     *
     * @param bowlingAlleyBooking
     * @return {@code double} total
     */
    private String calculateBookingTotal(BowlingAlleyBooking bowlingAlleyBooking) {
        List<DrinkBooking> drinkBookingList = drinkBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
        List<FoodBooking> foodBookingList = foodBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
        List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());

        double total = bowlingAlleyBooking.getPrice();
        for (DrinkBooking drinkBooking : drinkBookingList) {
            total += drinkBooking.getPrice() * drinkBooking.getAmount();
        }
        for (FoodBooking foodBooking : foodBookingList) {
            total += foodBooking.getPrice() * foodBooking.getAmount();
        }
        for (BowlingShoeBooking shoeBooking : shoeBookingList) {
            total += shoeBooking.getPrice();
        }
        return String.format(Locale.GERMANY, "%.2f", total) + "€";
    }

    /**
     * Calculates the total sum of prices for the all the bookings
     *
     * @return {@code double} total
     */
    private String calculateTotal() {
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
        return String.format(Locale.GERMANY, "%.2f", total) + "€";
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer parameter) {
        if (parameter == null) {
            return;
        }
        Optional<Client> optionalClient = Repos.getClientRepository().findById(parameter);
        optionalClient.ifPresent(client -> {
            if (client.isActive()) {
                currentClient = client;
                updateInitialComponents();
                updateGridItems();
            }
        });
    }
}