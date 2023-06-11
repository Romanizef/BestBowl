package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.food.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodBookingRepository;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.softwareprojekt.bestbowl.utils.Utils.*;

/**
 * @author Marten Voß
 */
@Route(value = "pendingBookings", layout = MainView.class)
@PageTitle("Offene Buchungen")
@PermitAll
public class PendingBookingView extends VerticalLayout {
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private final Grid<BowlingAlleyBooking> bookingGrid;
    private final Button invoiceButton;
    private TextField searchField;
    private BowlingAlleyBooking selectedBooking;

    /**
     * The PendingBookingView function is a constructor for the PendingBookingView
     * class.
     * It creates a new instance of the PendingBookingView class and initializes its
     * fields with values passed as parameters.
     * 
     * @param bowlingAlleyBookingRepository
     * @param drinkBookingRepository
     * @param foodBookingRepository
     * @param shoeBookingRepository
     */
    @Autowired
    public PendingBookingView(BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
            DrinkBookingRepository drinkBookingRepository,
            FoodBookingRepository foodBookingRepository,
            BowlingShoeBookingRepository shoeBookingRepository) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.shoeBookingRepository = shoeBookingRepository;
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 header = new H1("Offene Buchungen");
        Component searchComponent = createSearchComponent();
        bookingGrid = createGrid();
        invoiceButton = createInvoiceButton();
        add(header, searchComponent, bookingGrid, invoiceButton);

        updateGridItems();
        updateComponents();
    }

    /**
     * The createSearchComponent function creates a search bar that allows the user
     * to filter through the bookings.
     * The function returns a Component object, which is then added to the main
     * layout of this view.
     * 
     * @return A horizontallayout
     */
    private Component createSearchComponent() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("70%");
        searchField = new TextField();
        searchField.setPlaceholder("Suche nach Bahn, Name oder Zeit ...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGridItems());
        Button searchButton = new Button();
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        searchButton.addClickListener(e -> updateGridItems());
        searchLayout.expand(searchField);
        searchLayout.add(searchField, searchButton);
        return searchLayout;
    }

    /**
     * The createGrid function creates a grid of bowling alley bookings.
     * 
     * @return A grid
     */
    private Grid<BowlingAlleyBooking> createGrid() {
        Grid<BowlingAlleyBooking> grid = new Grid<>(BowlingAlleyBooking.class, false);
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(booking -> booking.getBowlingAlley().getId()).setHeader("Bahn");
        grid.addColumn(booking -> booking.getClient().getFullName()).setHeader("Kunde");
        grid.addColumn(booking -> toDateString(booking.getStartTime())).setHeader("Startzeit");
        grid.addColumn(booking -> toHoursString(booking.getDuration())).setHeader("Dauer");
        grid.addColumn(booking -> toDateString(booking.getEndTime() + 1)).setHeader("Endzeit");
        grid.addColumn(this::calculateBookingTotal).setHeader("Summe");
        grid.getColumns().forEach(c -> c.setSortable(true).setResizable(true));

        grid.addSelectionListener(e -> {
            Optional<BowlingAlleyBooking> bowlingAlleyBookingOptional = e.getFirstSelectedItem();
            selectedBooking = bowlingAlleyBookingOptional.orElse(null);
            updateComponents();
        });
        return grid;
    }

    /**
     * The updateGridItems function is responsible for updating the items in the
     * grid.
     * It does this by first querying all bookings from the database that have an
     * end time less than or equal to now, and are not completed yet.
     * Then it checks if there is a search term entered into the search field, and
     * if so splits it up into individual words (search terms).
     * If there are any search terms present, then we iterate over each booking in
     * our list of bookings retrieved from our query above. For each booking we
     * check whether any of its properties contain one of our search terms (case
     * insensitive), and remove them from a copy of the list of search terms if so.
     * If there are no search terms left in the copy, then we remove the booking
     * from the list of bookings.
     */
    private void updateGridItems() {
        long currentTime = System.currentTimeMillis();
        List<BowlingAlleyBooking> bookingList = bowlingAlleyBookingRepository
                .findAllByEndTimeLessThanAndCompletedEquals(currentTime, false);
        String searchFieldValue = searchField.getValue();
        String[] searchTerms;
        if (searchFieldValue != null) {
            searchTerms = searchFieldValue.trim().split(" ");
            Iterator<BowlingAlleyBooking> bookingIterator = bookingList.iterator();
            while (bookingIterator.hasNext()) {
                BowlingAlleyBooking booking = bookingIterator.next();
                List<String> searchTermsCopy = new ArrayList<>(Arrays.stream(searchTerms).toList());
                matchAndRemoveIfContains(String.valueOf(booking.getBowlingAlley().getId()), searchTermsCopy);
                matchAndRemoveIfContains(booking.getClient().getFirstName(), searchTermsCopy);
                matchAndRemoveIfContains(booking.getClient().getLastName(), searchTermsCopy);
                matchAndRemoveIfContains(toDateString(booking.getStartTime()), searchTermsCopy);
                matchAndRemoveIfContains(toHoursString(booking.getDuration()), searchTermsCopy);
                matchAndRemoveIfContains(toDateString(booking.getEndTime()), searchTermsCopy);
                if (!searchTermsCopy.isEmpty()) {
                    bookingIterator.remove();
                }
            }
        }
        GridListDataView<BowlingAlleyBooking> clientGridListDataView = bookingGrid.setItems(bookingList);
        clientGridListDataView.setSortOrder(BowlingAlleyBooking::getEndTime, SortDirection.ASCENDING);
    }

    /**
     * The createInvoiceButton function creates a button that navigates to the
     * InvoiceView.
     * 
     * @return A button
     */
    private Button createInvoiceButton() {
        Button button = new Button("Zu Rechnung abschließen");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.setWidth("55%");
        button.addClickListener(e -> UI.getCurrent().navigate(InvoiceView.class, selectedBooking.getId()));
        return button;
    }

    /**
     * The updateComponents function enables the invoiceButton if a booking is
     * selected.
     */
    private void updateComponents() {
        invoiceButton.setEnabled(selectedBooking != null);
    }

    /**
     * The calculateBookingTotal function calculates the total price of a booking.
     * It does so by adding up the prices of all bookings that are associated with
     * this booking,
     * i.e., it adds up the price of all food and drink bookings as well as shoe
     * rentals that were made at the same time for this bowling alley.
     * 
     * @param BowlingAlleyBooking bowlingAlleyBooking Get the client, bowling alley
     *                            and start time of a booking
     *
     * @return A string
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
}