package de.softwareprojekt.bestbowl.views.managementViews;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.Objects;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.showNotification;

/**
 * @author Marten Voß
 */
@Route(value = "bowlingCenterManagement", layout = MainView.class)
@PageTitle("Bowlingcenter Verwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class BowlingCenterManagementView extends VerticalLayout {
    private final transient BowlingCenterRepository bowlingCenterRepository;
    private final Binder<BowlingCenter> binder = new Binder<>();
    private BowlingCenter bowlingCenter;

    @Autowired
    public BowlingCenterManagementView(BowlingCenterRepository bowlingCenterRepository) {
        this.bowlingCenterRepository = bowlingCenterRepository;
        bowlingCenter = bowlingCenterRepository.getBowlingCenter();

        setSizeFull();

        TextField displayNameField = new TextField("Anzeigename");
        TextField businessNameField = new TextField("Geschäftsname");
        Label addressLabel = new Label("Adresse");
        TextField streetField = new TextField("Straße");
        IntegerField houseNrField = new IntegerField("Hausnummer");
        IntegerField postCodeField = new IntegerField("PLZ");
        TextField cityField = new TextField("Stadt");
        Label businessTimeLabel = new Label("Geschäftszeiten");
        TimePicker startTimePicker = new TimePicker("Start");
        TimePicker endTimePicker = new TimePicker("Ende");
        Label emailServerLabel = new Label("E-Mail Server Daten");
        TextField emailField = new TextField("E-Mail");
        TextField passwordField = new TextField("E-Mail Passwort");
        TextField smtpHostField = new TextField("SMTP Host");
        TextField smtpPortField = new TextField("SMTP Port");

        Button saveButton = new Button("Sichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(displayNameField, businessNameField,
                addressLabel, streetField, houseNrField, postCodeField, cityField,
                businessTimeLabel, startTimePicker, endTimePicker,
                emailServerLabel, emailField, passwordField, smtpHostField, smtpPortField,
                saveButton);

        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(bowlingCenter);
                bowlingCenterRepository.save(bowlingCenter);
                showNotification("gespeichert");
            } catch (ValidationException ex) {
                showNotification("Validierungsfehler");
            }
        });

        binder.bind(displayNameField, BowlingCenter::getDisplayName, BowlingCenter::setDisplayName);
        binder.bind(businessNameField, BowlingCenter::getBusinessName, BowlingCenter::setBusinessName);
        binder.bind(streetField, BowlingCenter::getStreet, BowlingCenter::setStreet);
        binder.bind(houseNrField, BowlingCenter::getHouseNr, (bc, i) -> bc.setHouseNr(Objects.requireNonNullElse(i, 0)));
        binder.bind(postCodeField, BowlingCenter::getPostCode, (bc, i) -> bc.setPostCode(Objects.requireNonNullElse(i, 0)));
        binder.bind(cityField, BowlingCenter::getCity, BowlingCenter::setCity);
        binder.bind(startTimePicker, bc -> LocalTime.ofSecondOfDay(bc.getStartTime()), (bc, time) -> bc.setStartTime(time.toSecondOfDay()));
        binder.bind(endTimePicker, bc -> LocalTime.ofSecondOfDay(bc.getEndTime()), (bc, time) -> bc.setEndTime(time.toSecondOfDay()));
        binder.bind(emailField, BowlingCenter::getEmail, BowlingCenter::setEmail);
        binder.bind(passwordField, BowlingCenter::getPassword, BowlingCenter::setPassword);
        binder.bind(smtpHostField, BowlingCenter::getSmtpHost, BowlingCenter::setSmtpHost);
        binder.bind(smtpPortField, BowlingCenter::getSmtpPort, BowlingCenter::setSmtpPort);

        binder.readBean(bowlingCenter);
    }
}
