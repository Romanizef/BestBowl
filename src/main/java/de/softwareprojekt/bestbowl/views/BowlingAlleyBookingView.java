package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Marten Voß
 */
@Route(value = "bowlingAlleyBooking", layout = MainView.class)
@PageTitle("Bahn buchen")
@PermitAll
public class BowlingAlleyBookingView extends VerticalLayout {
    private final BowlingAlleyRepository bowlingAlleyRepository;
    private final BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final H1 clientHeader;
    private final Button bookButton;
    private Button checkButton;
    private Client selectedClient = null;

    @Autowired
    public BowlingAlleyBookingView(BowlingAlleyRepository bowlingAlleyRepository, BowlingAlleyBookingRepository bowlingAlleyBookingRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        clientHeader = new H1();
        Component menuComponent = createMenuComponent();
        bookButton = new Button("Zeit buchen");
        bookButton.setWidth("55%");
        bookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(clientHeader, menuComponent, bookButton);
        updateComponents();
    }

    private Component createMenuComponent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        DatePicker datePicker = new DatePicker("Tag");
        datePicker.setLocale(Locale.GERMANY);
        datePicker.setValue(LocalDate.now());
        TimePicker timePicker = new TimePicker("Uhrzeit");
        timePicker.setLocale(Locale.GERMANY);
        timePicker.setValue(LocalTime.now());
        ComboBox<Duration> durationCB = new ComboBox<>("Länge");
        durationCB.setAllowCustomValue(false);
        List<Duration> durationList = generateDurationList();
        durationCB.setItems(durationList);
        durationCB.setValue(durationList.get(2));
        checkButton = new Button("Zeit prüfen");
        checkButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(datePicker, timePicker, durationCB, checkButton);
        return layout;
    }

    private List<Duration> generateDurationList() {
        List<Duration> durationList = new ArrayList<>();
        for (int i = 2; i <= 32; i++) {
            durationList.add(new Duration((double) i / 4));
        }
        return durationList;
    }

    private void updateComponents() {
        if (selectedClient == null) {
            clientHeader.setText("Bahn buchen für: (kein Kunde ausgewählt)");
            checkButton.setEnabled(false);
            bookButton.setEnabled(false);
        } else {
            clientHeader.setText("Bahn buchen für: " + selectedClient.getFirstName() + " " + selectedClient.getLastName());
            checkButton.setEnabled(true);
            bookButton.setEnabled(true);
        }
    }

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
        updateComponents();
    }

    private record Duration(double hours) {
        @Override
        public String toString() {
            return Utils.formatDouble(hours) + " Std.";
        }
    }
}
