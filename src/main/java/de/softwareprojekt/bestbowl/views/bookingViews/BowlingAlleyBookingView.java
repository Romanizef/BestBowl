package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.*;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.beans.SecurityService;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.checkers.AlleyBookingChecker;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.email.MailSenderService;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.toDateString;
import static de.softwareprojekt.bestbowl.utils.Utils.toHoursString;

/**
 * @author Marten Voß
 */
@Route(value = "bowlingAlleyBooking", layout = MainView.class)
@PageTitle("Bahn buchen")
@PermitAll
public class BowlingAlleyBookingView extends VerticalLayout implements HasUrlParameter<Integer> {
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient BowlingAlleyRepository bowlingAlleyRepository;
    private final transient SecurityService securityService;
    private final transient AlleyBookingChecker alleyBookingChecker = new AlleyBookingChecker();
    private final H1 clientHeader;
    private final Grid<BowlingAlleyBooking> bookingGrid;
    private final Button bookButton;
    private final Button continueToExtrasButton;
    private final transient MailSenderService mailSenderController = new MailSenderService();
    private final BowlingCenter bowlingCenter;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Select<Duration> durationSelect;
    private Button checkButton;
    private Label lastBookedBowlingAlleyLabel;
    private Button cancelBookingButton;
    private Client selectedClient = null;
    private BowlingAlleyBooking latestBooking = null;
    private long gridLowerBound;
    private long gridUpperBound;

    /**
     * The BowlingAlleyBookingView function is the main function of this class.
     * It creates a new BowlingAlleyBookingView object, which is used to display the
     * booking view for bowling alleys.
     * The user can select a date and time, as well as an alley number and book it
     * if it's available.
     *
     * @param bowlingAlleyBookingRepository Save the booking to the database
     * @param bowlingAlleyRepository        Get the bowling alleys from the database
     * @param bowlingCenterRepository       Get the bowling center
     * @param securityService               securityService
     */
    @Autowired
    public BowlingAlleyBookingView(BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
                                   BowlingAlleyRepository bowlingAlleyRepository,
                                   BowlingCenterRepository bowlingCenterRepository,
                                   SecurityService securityService) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        this.securityService = securityService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        bowlingCenter = bowlingCenterRepository.getBowlingCenter();

        clientHeader = new H1();
        Component menuComponent = createMenuComponent();
        bookButton = createBookButton();
        bookingGrid = createBookingGrid();
        Component footerComponent = createFooterComponent();
        continueToExtrasButton = createArticleBookingViewButton();

        add(clientHeader, menuComponent, bookButton, bookingGrid, footerComponent, continueToExtrasButton);

        updateInitialComponents();
        updateGridItems();
        updateLabelAndButtons();
        setBoundsToWholeDay();
    }

    /**
     * The setBoundsToWholeDay function sets the gridLowerBound and gridUpperBound
     * variables to the start of a day
     * (the current day if it is after the bowling center's opening time, or
     * yesterday otherwise) and one day later.
     *
     * @return The start time of the day, and the end time of that same day
     */
    private void setBoundsToWholeDay() {
        LocalDateTime startLDT = LocalDateTime.of(datePicker.getValue(),
                LocalTime.ofSecondOfDay(bowlingCenter.getStartTime()));
        if (LocalTime.now().isBefore(LocalTime.ofSecondOfDay(bowlingCenter.getStartTime()))) {
            startLDT = startLDT.minus(java.time.Duration.ofDays(1));
        }
        gridLowerBound = startLDT.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L;
        gridUpperBound = gridLowerBound + java.time.Duration.ofDays(1).toMillis();
    }

    /**
     * The createMenuComponent function creates a HorizontalLayout that contains the
     * following components:
     * - A DatePicker for selecting the date of a booking.
     * - A TimePicker for selecting the time of a booking.
     * - A ComboBox to select how long you want to book an alley for (in hours).
     * - The checkButton, which checks if there is an available bowling alley at
     * your selected time and duration. If so, it will notify you with its number in
     * Notifications.showInfo(). Otherwise it will tell you that no alleys are
     * available at your selected time and duration in Notifications.showInfo(). It
     *
     * @return A layout
     */
    private Component createMenuComponent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);

        datePicker = new DatePicker("Tag");
        configureDatePicker(datePicker);

        timePicker = new TimePicker("Uhrzeit");
        configureTimePicker(timePicker);

        durationSelect = new Select<>();
        durationSelect.setLabel("Dauer");
        List<Duration> durationList = generateDurationList();
        durationSelect.setItems(durationList);
        durationSelect.setValue(durationList.get(1));

        checkButton = new Button("Zeit prüfen");
        checkButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button wholeDayButton = new Button("Zeige ganzen Tag");

        layout.add(datePicker, timePicker, durationSelect, checkButton, wholeDayButton);

        checkButton.addClickListener(e -> {
            if (!checkForBowlingAlleys()) {
                Notifications.showInfo("Keine Bahn im System angelegt");
                return;
            }
            alleyBookingChecker.setTimeInfo(datePicker.getValue(), timePicker.getValue(),
                    (int) (durationSelect.getValue().hours() * 60));
            if (alleyBookingChecker.checkTime(securityService)) {
                gridLowerBound = alleyBookingChecker.getStartTime();
                gridUpperBound = alleyBookingChecker.getEndTime();
                if (alleyBookingChecker.checkAvailability()) {
                    Notifications.showInfo("Freie Bahn: Nr. " + alleyBookingChecker.getAvailableAlleyId());
                } else {
                    Notifications.showInfo("Keine Bahn frei zu der ausgewählten Zeit");
                }
                updateGridItems();
            }
        });
        wholeDayButton.addClickListener(e -> {
            setBoundsToWholeDay();
            updateGridItems();
        });

        return layout;
    }

    /**
     * The configureDatePicker function configures the datePicker to be used in the
     * BookingView.
     * It sets its locale to Germany, and sets its value to today's date. If the
     * current user is not an admin, it also
     * disables all dates before today's date (i.e., you can't book a bowling alley
     * for yesterday). Finally, it adds a
     * listener that checks if any of these rules are violated when changing the
     * value of this DatePicker: if so, it resets
     * this DatePicker back to its old value (i.e., you can't change your booking).
     *
     * @param datePicker Set the locale, value and min
     * @return A datepicker
     */
    private void configureDatePicker(DatePicker datePicker) {
        datePicker.setLocale(Locale.GERMANY);
        datePicker.setValue(Utils.getCurrentDateTimeRounded().toLocalDate());
        if (!securityService.isCurrentUserInRole(UserRole.ADMIN)) {
            datePicker.setMin(LocalDate.now());
        }
        datePicker.addValueChangeListener(e -> {
            if (!securityService.isCurrentUserInRole(UserRole.ADMIN) &&
                    (e.getValue().isBefore(datePicker.getMin()))) {
                datePicker.setValue(e.getOldValue());
            }
        });
    }

    /**
     * The configureTimePicker function configures the timePicker to be used in the
     * booking view.
     * It sets the locale to Germany, sets a step of 30 minutes and rounds down/up
     * to nearest half hour.
     * If the user is not an admin, it also restricts him from selecting times
     * outside of his bowling center's opening hours.
     *
     * @param timePicker Set the locale, step size and value of the time
     *                   picker
     */
    private void configureTimePicker(TimePicker timePicker) {
        timePicker.setLocale(Locale.GERMANY);
        timePicker.setStep(java.time.Duration.ofMinutes(30));
        timePicker.setValue(Utils.getCurrentDateTimeRounded().toLocalTime());
        if (!securityService.isCurrentUserInRole(UserRole.ADMIN)) {
            timePicker.setMin(LocalTime.ofSecondOfDay(bowlingCenter.getStartTime()));
            timePicker.setMax(LocalTime.ofSecondOfDay(bowlingCenter.getEndTime()));
        }
        timePicker.addValueChangeListener(e -> {
            if (!securityService.isCurrentUserInRole(UserRole.ADMIN) &&
                    (e.getValue().isBefore(timePicker.getMin()) || e.getValue().isAfter(timePicker.getMax()))) {
                timePicker.setValue(e.getOldValue());
            }
        });
    }

    /**
     * The createBookButton function creates a button that allows the user to book
     * an alley.
     *
     * @return A button
     */
    private Button createBookButton() {
        Button button = new Button("Zeit buchen");
        button.setWidth("65%");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickListener(e -> {
            if (!checkForBowlingAlleys()) {
                Notifications.showError("Keine Bahn im System angelegt");
                return;
            }
            alleyBookingChecker.setTimeInfo(datePicker.getValue(), timePicker.getValue(),
                    (int) (durationSelect.getValue().hours() * 60));
            if (alleyBookingChecker.checkTime(securityService)) {
                gridLowerBound = alleyBookingChecker.getStartTime();
                gridUpperBound = alleyBookingChecker.getEndTime();
                BowlingAlleyBooking newBooking = alleyBookingChecker.book();
                if (newBooking != null) {
                    latestBooking = newBooking;
                    Notifications.showInfo("Bahn Nr. " + alleyBookingChecker.getAvailableAlleyId() + " gebucht");
                    mailSenderController.sendBookingConfirmationMail(latestBooking);
                }
                updateGridItems();
                updateLabelAndButtons();
            }
        });
        return button;
    }

    /**
     * The createFooterComponent function creates a footer component for the booking
     * view.
     * The footer contains a label that displays the last booked bowling alley and a
     * button to cancel this booking.
     *
     * @return A horizontallayout
     */
    public Component createFooterComponent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);

        lastBookedBowlingAlleyLabel = new Label();

        cancelBookingButton = new Button("Stornieren");
        cancelBookingButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        layout.add(lastBookedBowlingAlleyLabel, cancelBookingButton);

        cancelBookingButton.addClickListener(e -> {
            if (latestBooking != null) {
                mailSenderController.sendBookingCancelationMail(latestBooking);
                latestBooking.setActive(false);
                bowlingAlleyBookingRepository.save(latestBooking);
                latestBooking = null;
                updateGridItems();
                updateLabelAndButtons();
                Notifications.showInfo("Storniert");
            }
        });
        return layout;
    }

    /**
     * The createBookingGrid function creates a Grid of BowlingAlleyBooking objects.
     * The grid is used to display the bookings in the booking view.
     *
     * @return A grid
     */
    private Grid<BowlingAlleyBooking> createBookingGrid() {
        Grid<BowlingAlleyBooking> grid = new Grid<>(BowlingAlleyBooking.class, false);
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(booking -> booking.getBowlingAlley().getId()).setHeader("Bahn");
        grid.addColumn(booking -> booking.getClient().getFullName()).setHeader("Kunde");
        grid.addColumn(booking -> toDateString(booking.getStartTime())).setHeader("Startzeit");
        grid.addColumn(booking -> toHoursString(booking.getDuration())).setHeader("Dauer");
        grid.addColumn(booking -> toDateString(booking.getEndTime() + 1)).setHeader("Endzeit");
        grid.getColumns().forEach(c -> c.setSortable(true).setResizable(true).setAutoWidth(true));
        return grid;
    }

    /**
     * The updateGridItems function updates the items in the bookingGrid.
     * It does this by querying all bookings from the database that overlap with a
     * given time period.
     * The time period is defined by gridLowerBound and gridUpperBound, which are
     * set to be one week before and after today's date respectively.
     */
    private void updateGridItems() {
        bookingGrid.setItems(
                bowlingAlleyBookingRepository.findAllByTimePeriodsOverlapping(gridLowerBound, gridUpperBound));
    }

    /**
     * The createExtrasButton function creates a button that navigates to the
     * ArticleBookingView.
     *
     * @return A button
     */
    private Button createArticleBookingViewButton() {
        Button button = new Button("Weiter zum Artikel buchen");
        button.setWidth("55%");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> UI.getCurrent().navigate(ArticleBookingView.class, latestBooking.getId()));
        return button;
    }

    /**
     * The generateDurationList function generates a list of durations that can be
     * used to book bowling alleys.
     * The duration is in hours and the minimum booking time is 30 minutes, so the
     * function returns a list of all possible durations from 0.5h to 8h in steps of
     * 0.5h
     *
     * @return A list of duration objects
     */
    private List<Duration> generateDurationList() {
        List<Duration> durationList = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            durationList.add(new Duration((double) i / 2));
        }
        return durationList;
    }

    /**
     * The updateInitialComponents function is used to update the initial components
     * of the view.
     * It sets the clientHeader text to &quot;Bahn buchen für: (kein Kunde
     * ausgewählt)&quot; if no client has been selected,
     * and it sets it to &quot;Bahn buchen für: [first name] [last name]&quot; if a
     * client has been selected.
     * It also enables or disables both checkButton and bookButton depending on
     * whether or not a client has been selected.
     */
    private void updateInitialComponents() {
        if (selectedClient == null) {
            clientHeader.setText("Bahn buchen für: (kein Kunde ausgewählt)");
            checkButton.setEnabled(false);
            bookButton.setEnabled(false);
        } else {
            clientHeader
                    .setText("Bahn buchen für: " + selectedClient.getFirstName() + " " + selectedClient.getLastName());
            checkButton.setEnabled(true);
            bookButton.setEnabled(true);
        }
    }

    /**
     * The updateLabelAndButtons function updates the label and buttons on the page.
     * If there is no latest booking, then it sets the text of
     * lastBookedBowlingAlleyLabel to &quot;Neuste Buchung: -&quot;, disables
     * continueToExtrasButton, and hides cancelBookingButton.
     * Otherwise, it sets lastBookedBowlingAlleyLabel's text to a string containing
     * information about latestBooking (the bowling alley number, start time in date
     * format, and duration in hours), enables continueToExtrasButton, and shows
     * cancelBookingButton.
     */
    private void updateLabelAndButtons() {
        if (latestBooking == null) {
            lastBookedBowlingAlleyLabel.setText("Neuste Buchung: -");
            continueToExtrasButton.setEnabled(false);
            cancelBookingButton.setVisible(false);
        } else {
            lastBookedBowlingAlleyLabel.setText("Neuste Buchung: Bahn Nr. " + latestBooking.getBowlingAlley().getId() +
                    " am " + Utils.toDateString(latestBooking.getStartTime()) +
                    " für " + Utils.toHoursString(latestBooking.getDuration()));
            continueToExtrasButton.setEnabled(true);
            cancelBookingButton.setVisible(true);
        }
    }

    /**
     * The checkForBowlingAlleys function checks if there are any bowling alleys in
     * the database.
     *
     * @return True if there are bowling alleys in the database, false otherwise
     */
    private boolean checkForBowlingAlleys() {
        List<BowlingAlley> bowlingAlleyList = bowlingAlleyRepository.findAll();
        return !bowlingAlleyList.isEmpty();
    }

    /**
     * The setParameter function is called by the framework when a new instance of
     * this view is created.
     * It sets the selectedClient to be used in this view.
     *
     * @param event              Get the url parameters
     * @param @OptionalParameter Integer parameter Pass the parameter from the url
     *                           to this function
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer parameter) {
        if (parameter == null) {
            return;
        }
        Optional<Client> optionalClient = Repos.getClientRepository().findById(parameter);
        optionalClient.ifPresent(client -> {
            if (client.isActive()) {
                selectedClient = client;
                alleyBookingChecker.setClient(client);
                updateInitialComponents();
            }
        });
    }

    private record Duration(double hours) {

        /**
         * The toString function is used to display the hours of a booking in the grid.
         *
         * @return A string, which is the formatted hours
         */
        @Override
        public String toString() {
            return Utils.formatDouble(hours) + " Std.";
        }
    }
}