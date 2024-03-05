package de.softwareprojekt.bestbowl.views.management_views;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.validators.BowlingCenterValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.Objects;

/**
 * @author Marten Voß
 * @author Matija Kopschek
 */
@Route(value = "bowlingCenterManagement", layout = MainView.class)
@PageTitle("Bowlingcenter Verwaltung")
@RolesAllowed({UserRole.OWNER})
public class BowlingCenterManagementView extends Div {
    private final Binder<BowlingCenter> binder = new Binder<>();
    private final BowlingCenter bowlingCenter;

    /**
     * The BowlingCenterManagementView function is used to manage all the
     * information of the bowling center.
     *
     * @param bowlingCenterRepository
     */
    @Autowired
    public BowlingCenterManagementView(BowlingCenterRepository bowlingCenterRepository) {
        bowlingCenter = bowlingCenterRepository.getBowlingCenter();
        setSizeFull();
        Accordion accordion = new Accordion();

        TextField displayNameField = new TextField("Anzeigename");
        displayNameField.setRequiredIndicatorVisible(true);
        TextField businessNameField = new TextField("Geschäftsname");
        businessNameField.setRequiredIndicatorVisible(true);
        VerticalLayout centerInformationLayout = new VerticalLayout(displayNameField,
                businessNameField);
        AccordionPanel centerInformationPanel = accordion.add("Bowlingcenterinformationen",
                centerInformationLayout);
        centerInformationPanel.addThemeVariants(DetailsVariant.FILLED);

        TextField streetField = new TextField("Straße");
        streetField.setRequiredIndicatorVisible(true);
        TextField houseNrField = new TextField("Hausnummer (ggf. mit Zusatz)");
        houseNrField.setRequiredIndicatorVisible(true);
        IntegerField postCodeField = new IntegerField("PLZ");
        postCodeField.setRequiredIndicatorVisible(true);
        TextField cityField = new TextField("Stadt");
        cityField.setRequiredIndicatorVisible(true);
        VerticalLayout centerAddressLayout = new VerticalLayout(streetField,
                houseNrField, postCodeField, cityField);
        AccordionPanel centerAddressPanel = accordion.add("Adresse", centerAddressLayout);
        centerAddressPanel.addThemeVariants(DetailsVariant.FILLED);

        TimePicker startTimePicker = new TimePicker("Start");
        startTimePicker.setRequiredIndicatorVisible(true);
        TimePicker endTimePicker = new TimePicker("Ende");
        endTimePicker.setRequiredIndicatorVisible(true);
        VerticalLayout centerBusinessHoursLayout = new VerticalLayout(startTimePicker,
                endTimePicker);
        AccordionPanel centerBusinessHoursPanel = accordion.add("Geschäftszeiten", centerBusinessHoursLayout);
        centerBusinessHoursPanel.addThemeVariants(DetailsVariant.FILLED);

        NumberField bowlingAlleyPricePerHourField = new NumberField("Bahnpreis pro Stunde");
        bowlingAlleyPricePerHourField.setRequiredIndicatorVisible(true);
        bowlingAlleyPricePerHourField.setSuffixComponent(new Span("EUR"));
        NumberField bowlingShoePriceField = new NumberField("Schuhpreis pro Ausleihe");
        bowlingShoePriceField.setRequiredIndicatorVisible(true);
        bowlingShoePriceField.setSuffixComponent(new Span("EUR"));
        VerticalLayout centerPriceLayout = new VerticalLayout(bowlingAlleyPricePerHourField,
                bowlingShoePriceField);
        AccordionPanel centerPricePanel = accordion.add("Bahn und Schuhpreise", centerPriceLayout);
        centerPricePanel.addThemeVariants(DetailsVariant.FILLED);

        IntegerField minShoeSizeField = new IntegerField("Minimale Schuhgröße");
        minShoeSizeField.setRequiredIndicatorVisible(true);
        IntegerField maxShoeSizeField = new IntegerField("Maximale Schuhgröße");
        maxShoeSizeField.setRequiredIndicatorVisible(true);
        VerticalLayout shoeSizeLayout = new VerticalLayout(minShoeSizeField, maxShoeSizeField);
        AccordionPanel shoeSizePanel = accordion.add("Schuhgrößen", shoeSizeLayout);
        shoeSizePanel.addThemeVariants(DetailsVariant.FILLED);

        Label emailInfoLabel = new Label("Damit das System E-Mails senden kann, müssen die folgenden Felder gefüllt sein:");
        TextField senderEmailField = new TextField("Sender E-Mail");
        PasswordField passwordField = new PasswordField("Sender E-Mail Passwort");
        TextField receiverEmailField = new TextField("Empfänger E-Mail (Test)");
        Label receiverInfoLabel = new Label("Wenn dieses Feld gefüllt ist, wird jede E-Mail an diese Adresse gesendet.");
        HorizontalLayout receiverLayout = new HorizontalLayout(receiverEmailField, receiverInfoLabel);
        receiverLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        receiverLayout.setPadding(false);
        receiverLayout.setMargin(false);
        TextField smtpHostField = new TextField("SMTP Host");
        TextField smtpPortField = new TextField("SMTP Port");
        VerticalLayout centerEmailLayout = new VerticalLayout(emailInfoLabel, senderEmailField, passwordField,
                receiverLayout, smtpHostField, smtpPortField);
        AccordionPanel centerEmailPanel = accordion.add("E-Mail Server Daten", centerEmailLayout);
        centerEmailPanel.addThemeVariants(DetailsVariant.FILLED);

        Button saveButton = new Button("Speichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-left", "10px").set("margin-top", "10px");

        add(accordion, saveButton);

        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(bowlingCenter);
                bowlingCenterRepository.save(bowlingCenter);
                Notifications.showInfo("gespeichert");
            } catch (ValidationException ex) {
                if (!ex.getValidationErrors().isEmpty()) {
                    Notifications.showError(ex.getValidationErrors().get(0).getErrorMessage());
                }
            }
        });

        binder.withValidator(new BowlingCenterValidator());
        binder.bind(displayNameField, BowlingCenter::getDisplayName, BowlingCenter::setDisplayName);
        binder.bind(businessNameField, BowlingCenter::getBusinessName, BowlingCenter::setBusinessName);
        binder.bind(streetField, BowlingCenter::getStreet, BowlingCenter::setStreet);
        binder.bind(houseNrField, BowlingCenter::getHouseNr, BowlingCenter::setHouseNr);
        binder.bind(postCodeField, BowlingCenter::getPostCode,
                (bc, i) -> bc.setPostCode(Objects.requireNonNullElse(i, 0)));
        binder.bind(cityField, BowlingCenter::getCity, BowlingCenter::setCity);
        binder.bind(startTimePicker, bc -> LocalTime.ofSecondOfDay(bc.getStartTime()),
                (bc, time) -> bc.setStartTime(time.toSecondOfDay()));
        binder.bind(endTimePicker, bc -> LocalTime.ofSecondOfDay(bc.getEndTime()),
                (bc, time) -> bc.setEndTime(time.toSecondOfDay()));
        binder.bind(bowlingAlleyPricePerHourField, BowlingCenter::getBowlingAlleyPricePerHour,
                (bc, price) -> bc.setBowlingAlleyPricePerHour(Objects.requireNonNullElse(price, 0.0)));
        binder.bind(bowlingShoePriceField, BowlingCenter::getBowlingShoePrice,
                (bc, price) -> bc.setBowlingShoePrice(Objects.requireNonNullElse(price, 0.0)));
        binder.bind(minShoeSizeField, BowlingCenter::getMinShoeSize,
                (bc, min) -> bc.setMinShoeSize(Objects.requireNonNullElse(min, 0)));
        binder.bind(maxShoeSizeField, BowlingCenter::getMaxShoeSize,
                (bc, max) -> bc.setMaxShoeSize(Objects.requireNonNullElse(max, 0)));
        binder.bind(senderEmailField, BowlingCenter::getSenderEmail, BowlingCenter::setSenderEmail);
        binder.bind(receiverEmailField, BowlingCenter::getReceiverEmail, BowlingCenter::setReceiverEmail);
        binder.bind(passwordField, BowlingCenter::getPassword, BowlingCenter::setPassword);
        binder.bind(smtpHostField, BowlingCenter::getSmtpHost, BowlingCenter::setSmtpHost);
        binder.bind(smtpPortField, BowlingCenter::getSmtpPort, BowlingCenter::setSmtpPort);

        binder.readBean(bowlingCenter);
    }
}