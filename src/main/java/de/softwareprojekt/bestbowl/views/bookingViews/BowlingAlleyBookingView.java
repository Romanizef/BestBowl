package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.email.MailSenderService;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.other.AlleyBookingChecker;
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
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.isCurrentUserInRole;

/**
 * @author Marten Voß
 */
@Route(value = "bowlingAlleyBooking", layout = MainView.class)
@PageTitle("Bahn buchen")
@PermitAll
public class BowlingAlleyBookingView extends VerticalLayout implements HasUrlParameter<Integer> {
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient BowlingAlleyRepository bowlingAlleyRepository;
    private final transient AuthenticationContext authenticationContext;
    private final transient AlleyBookingChecker alleyBookingChecker = new AlleyBookingChecker();
    private final H1 clientHeader;
    private final Grid<BowlingAlleyBooking> bookingGrid;
    private final Button bookButton;
    private final Button continueToExtrasButton;
    private final transient MailSenderService mailSenderController = new MailSenderService();
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ComboBox<Duration> durationCB;
    private Button checkButton;
    private Label lastBookedBowlingAlleyLabel;
    private Button cancelBookingButton;
    private Client selectedClient = null;
    private BowlingAlleyBooking latestBooking = null;
    private long gridLowerBound;
    private long gridUpperBound;

    @Autowired
    public BowlingAlleyBookingView(BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
                                   BowlingAlleyRepository bowlingAlleyRepository,
                                   AuthenticationContext authenticationContext) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        this.authenticationContext = authenticationContext;
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        gridLowerBound = Utils.getCurrentDateTimeOfDayStart().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L;
        gridUpperBound = gridLowerBound + java.time.Duration.ofDays(1).toMillis();

        clientHeader = new H1();
        Component menuComponent = createMenuComponent();
        bookButton = createBookButton();
        bookingGrid = createBookingGrid();
        Component footerComponent = createFooterComponent();
        continueToExtrasButton = createExtrasButton();

        add(clientHeader, menuComponent, bookButton, bookingGrid, footerComponent, continueToExtrasButton);

        updateInitialComponents();
        updateGridItems();
        updateLabelAndButtons();
    }

    private Component createMenuComponent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);

        datePicker = new DatePicker("Tag");
        configureDatePicker(datePicker);

        timePicker = new TimePicker("Uhrzeit");
        configureTimePicker(timePicker);

        durationCB = new ComboBox<>("Dauer");
        durationCB.setAllowCustomValue(false);
        List<Duration> durationList = generateDurationList();
        durationCB.setItems(durationList);
        durationCB.setValue(durationList.get(1));

        checkButton = new Button("Zeit prüfen");
        checkButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button wholeDayButton = new Button("Zeige ganzen Tag");

        layout.add(datePicker, timePicker, durationCB, checkButton, wholeDayButton);

        checkButton.addClickListener(e -> {
            if (!checkForBowlingAlleys()) {
                Notifications.showInfo("Keine Bahn im System angelegt");
                return;
            }
            alleyBookingChecker.setTimeInfo(datePicker.getValue(), timePicker.getValue(),
                    (int) (durationCB.getValue().hours() * 60));
            if (alleyBookingChecker.checkTime(authenticationContext)) {
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
            gridLowerBound = Utils
                    .setLocalDateTimeToDayStart(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()))
                    .atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L;
            gridUpperBound = gridLowerBound + java.time.Duration.ofDays(1).toMillis();
            updateGridItems();
        });

        return layout;
    }

    private void configureDatePicker(DatePicker datePicker) {
        datePicker.setLocale(Locale.GERMANY);
//        datePicker.setValue(Utils.getCurrentDateTimeRounded().toLocalDate());
        datePicker.setValue(LocalDate.now());
        if (!isCurrentUserInRole(authenticationContext, UserRole.ADMIN)) {
            datePicker.setMin(LocalDate.now());
        }
    }

    private void configureTimePicker(TimePicker timePicker) {
        BowlingCenter bowlingCenter = Repos.getBowlingCenterRepository().getBowlingCenter();
        timePicker.setLocale(Locale.GERMANY);
        timePicker.setMin(LocalTime.ofSecondOfDay(bowlingCenter.getStartTime()));
        timePicker.setMax(LocalTime.ofSecondOfDay(bowlingCenter.getEndTime()));
//        timePicker.setValue(Utils.getCurrentDateTimeRounded().toLocalTime());
        timePicker.setValue(LocalTime.now());
    }

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
                    (int) (durationCB.getValue().hours() * 60));
            if (alleyBookingChecker.checkTime(authenticationContext)) {
                gridLowerBound = alleyBookingChecker.getStartTime();
                gridUpperBound = alleyBookingChecker.getEndTime();
                if ((latestBooking = alleyBookingChecker.book()) != null) {
                    Notifications.showInfo("Bahn Nr. " + alleyBookingChecker.getAvailableAlleyId() + " gebucht");
                    mailSenderController.sendBookingConfirmationMail(latestBooking);
                }
                updateGridItems();
                updateLabelAndButtons();
            }
        });
        return button;
    }

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

    private void updateGridItems() {
        bookingGrid.setItems(
                bowlingAlleyBookingRepository.findAllByTimePeriodsOverlapping(gridLowerBound, gridUpperBound));
    }

    private Button createExtrasButton() {
        Button button = new Button("Weiter zum Extras buchen");
        button.setWidth("55%");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> UI.getCurrent().navigate(ExtrasView.class, latestBooking.getId()));
        return button;
    }

    private List<Duration> generateDurationList() {
        List<Duration> durationList = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            durationList.add(new Duration((double) i / 2));
        }
        return durationList;
    }

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

    private boolean checkForBowlingAlleys() {
        List<BowlingAlley> bowlingAlleyList = bowlingAlleyRepository.findAll();
        return !bowlingAlleyList.isEmpty();
    }

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
        @Override
        public String toString() {
            return Utils.formatDouble(hours) + " Std.";
        }
    }
}
