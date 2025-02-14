package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowling_shoe.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.food.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodBookingRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.components.InvoiceDownloadButton;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.booking_views.ClientSearchView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.VAADIN_PRIMARY_BLUE;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.applyTooltip;

/**
 * Creates a view for all bookings to be displayed and downloaded
 *
 * @author Matija Kopschek
 */
@Route(value = "statistics", layout = MainView.class)
@PageTitle("Statistik")
@RolesAllowed({UserRole.OWNER})
public class StatisticsView extends VerticalLayout implements HasUrlParameter<Integer> {
    private static final String ALL = "Alle";
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;
    private final H1 clientHeader;
    private final H5 sumHeader;
    private Select<Status> statusSelect;
    private Select<String> yearSelect;
    private Grid<BowlingAlleyBooking> bookingGrid;
    private Client currentClient;

    /**
     * Constructor for the StatisticsView class. Creates all the components
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
        Component headerComponent = createHeaderComponents();
        add(headerComponent, bookingGrid, footerComponent);
    }

    /**
     * Creates a {@code HorizontalLayout} headerLayout that contains the
     * clientHeader and year dropdownfilter.
     *
     * @return {@code HorizontalLayout}
     * @see #createYearSelect()
     */
    private Component createHeaderComponents() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.expand(clientHeader);
        headerLayout.setWidthFull();
        statusSelect = createStatusSelect();
        yearSelect = createYearSelect();
        headerLayout.add(clientHeader, statusSelect, yearSelect);
        return headerLayout;
    }

    private Select<Status> createStatusSelect() {
        Select<Status> select = new Select<>();
        select.setLabel("Filtern nach Status");
        select.getStyle().set("margin-right", "20px");
        select.setItems(Status.values());
        select.setValue(Status.ANY);
        select.setItemLabelGenerator(Status::getText);
        select.addValueChangeListener(e -> updateGridItems());
        return select;
    }

    /**
     * Creates a {@code Select<String>} select that contains all years of al the
     * bookings. A bookingyear can be selected so that the grid updates itself
     * with the bookings of that year.
     *
     * @return {@code Select<String>}
     */
    private Select<String> createYearSelect() {
        Select<String> select = new Select<>();
        select.setLabel("Filtern nach Jahr");
        select.getStyle().set("margin-right", "80px");
        return select;
    }

    private void initYearSelect() {
        List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyBookingRepository.findAllByClientEquals(currentClient);
        List<String> yearList = new ArrayList<>();

        yearList.add(ALL);
        for (BowlingAlleyBooking bowlingAlleyBooking : bowlingAlleyBookingList) {
            String stringTime = Utils.toDateString(bowlingAlleyBooking.getStartTime());
            String year = stringTime.substring(6, 10);
            if (!yearList.contains(year))
                yearList.add(year);
        }

        // Sort reversed alphabetically
        Comparator<String> reverseComparator = Comparator.reverseOrder();
        yearList.sort(reverseComparator);

        yearSelect.setItems(yearList);
        yearSelect.setValue(ALL);
        yearSelect.addValueChangeListener(e -> updateGridItems());
    }

    /**
     * If no client is selected a message appears. A {@code H1} shows the currently
     * selected client
     */
    private void updateInitialComponents() {
        if (currentClient == null) {
            Notifications.showError("Kein Kunde ausgewählt!");
            UI.getCurrent().navigate(ClientSearchView.class);
        } else {
            clientHeader
                    .setText("Statistik für: KdnNr: " + currentClient.getId() + ", " + currentClient.getFullName());
            initYearSelect();
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
        verticallayout.add(createPreviousViewButton());

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
     * @return {@code Button} lastViewButton
     */
    private Button createPreviousViewButton() {
        Button lastViewButton;
        lastViewButton = new Button("Zurück zu Kundensuche");
        lastViewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        lastViewButton.setIcon(new Icon(VaadinIcon.BACKWARDS));
        lastViewButton.setWidth("50%");
        lastViewButton.addClickListener(e -> UI.getCurrent().navigate(ClientSearchView.class));
        return lastViewButton;
    }

    private void updateGridItems() {
        String bookingYear = yearSelect.getValue();
        if (bookingYear.equals(ALL)) {
            updateGridItemsA();
        } else {
            // start and endtime of the chosen year
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(bookingYear));
            cal.set(Calendar.DAY_OF_YEAR, 1); // 1 = first day of the year
            Date start = cal.getTime();

            cal.set(Calendar.YEAR, Integer.parseInt(bookingYear));
            cal.set(Calendar.MONTH, 11); // 11 = december
            cal.set(Calendar.DAY_OF_MONTH, 31); // 31 = last day of december
            Date end = cal.getTime();

            long lowerBound = start.getTime();
            long upperBound = end.getTime();
            updateGridItemsA(lowerBound, upperBound);
        }
    }

    /**
     * Updates all the grid items to the matching year.
     *
     * @param lowerBound
     * @param upperBound
     */
    private void updateGridItemsA(long lowerBound, long upperBound) {
        List<BowlingAlleyBooking> bookingList = bowlingAlleyBookingRepository
                .findAllByStartTimeBetweenAndClientEquals(lowerBound, upperBound, currentClient);
        updateGridItemsA(bookingList);
        sumHeader.setText("Jahressumme: " + formatDouble(calculateTotal(bookingList)) + "€");
    }

    /**
     * Updates all the grid items to the latest state
     */
    private void updateGridItemsA() {
        List<BowlingAlleyBooking> bookingList = bowlingAlleyBookingRepository.findAllByClientEquals(currentClient);
        updateGridItemsA(bookingList);
        sumHeader.setText("Gesamtsumme: " + formatDouble(calculateTotal(bookingList)) + "€");
    }

    /**
     * filters bookings by status if status != any and updates the grid items
     *
     * @param bookingList
     */
    private void updateGridItemsA(List<BowlingAlleyBooking> bookingList) {
        Status status = statusSelect.getValue();
        if (status != Status.ANY) {
            bookingList.removeIf(booking -> getBookingStatus(booking) != status);
        }
        GridListDataView<BowlingAlleyBooking> dataView = bookingGrid.setItems(bookingList);
        dataView.setSortOrder(BowlingAlleyBooking::getStartTime, SortDirection.ASCENDING);
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

        bookingGrid.addColumn(new ComponentRenderer<>(booking -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setAlignItems(Alignment.CENTER);
            InvoiceDownloadButton invoiceDownloadButton = new InvoiceDownloadButton(booking);
            invoiceDownloadButton.setEnabled(booking.isActive());
            applyTooltip(invoiceDownloadButton, "Rechnung herunterladen");
            horizontalLayout.add(invoiceDownloadButton, new Label(String.valueOf(booking.getId())));
            return horizontalLayout;
        })).setHeader("Rechnungsnummer");
        bookingGrid.addColumn(booking -> getBookingStatus(booking).getText()).setHeader("Rechnungsstatus");
        bookingGrid.addColumn(booking -> Utils.toDateString(booking.getStartTime()))
                .setComparator(Comparator.comparingLong(BowlingAlleyBooking::getStartTime)).setHeader("Datum");
        bookingGrid.addColumn(booking -> formatDouble(calculateBookingTotal(booking)) + "€").setHeader("Summe");

        bookingGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        bookingGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        bookingGrid.setSizeFull();
        return bookingGrid;
    }

    /**
     * Calculates the total sum of prices for the current booking
     *
     * @param bowlingAlleyBooking
     * @return {@code double} total
     */
    private double calculateBookingTotal(BowlingAlleyBooking bowlingAlleyBooking) {
        //sum 0 if canceled
        if (!bowlingAlleyBooking.isActive()) {
            return 0.0;
        }

        List<DrinkBooking> drinkBookingList = drinkBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
        List<FoodBooking> foodBookingList = foodBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());
        List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(bowlingAlleyBooking.getClient(),
                        bowlingAlleyBooking.getBowlingAlley(), bowlingAlleyBooking.getStartTime());

        double total = bowlingAlleyBooking.getPriceWithDiscount();
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
    private double calculateTotal(List<BowlingAlleyBooking> bookingList) {
        double total = 0.0;
        for (BowlingAlleyBooking booking : bookingList) {
            total += calculateBookingTotal(booking);
        }
        return total;
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

    private Status getBookingStatus(BowlingAlleyBooking booking) {
        if (!booking.isActive()) {
            return Status.CANCELED;
        } else if (booking.isCompleted()) {
            return Status.COMPLETED;
        } else {
            return Status.OPEN;
        }
    }

    private enum Status {
        ANY(ALL),
        COMPLETED("Bezahlt"),
        CANCELED("Storniert"),
        OPEN("Offen");

        private final String text;

        Status(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}