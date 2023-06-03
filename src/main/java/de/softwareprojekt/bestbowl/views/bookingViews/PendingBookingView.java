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

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoeEntities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.foodEntities.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlleyRepos.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoeRepos.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drinkRepos.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.foodRepos.FoodBookingRepository;
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

    private void updateGridItems() {
        long currentTime = System.currentTimeMillis();
        List<BowlingAlleyBooking> bookingList = bowlingAlleyBookingRepository.findAllByEndTimeLessThanAndCompletedEquals(currentTime, false);
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

    private Button createInvoiceButton() {
        Button button = new Button("Zu Rechnung abschließen");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.setWidth("55%");
        button.addClickListener(e -> UI.getCurrent().navigate(InvoiceView.class, selectedBooking.getId()));
        return button;
    }

    private void updateComponents() {
        invoiceButton.setEnabled(selectedBooking != null);
    }

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
