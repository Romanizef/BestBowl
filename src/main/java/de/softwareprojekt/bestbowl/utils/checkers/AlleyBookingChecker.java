package de.softwareprojekt.bestbowl.utils.checkers;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.isCurrentUserInRole;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import com.vaadin.flow.spring.security.AuthenticationContext;

import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;

/**
 * AlleyBookingChecker is used to check if a booking is valid.
 * 
 * @author Marten Voß
 */
public class AlleyBookingChecker {
    private Client client;
    private LocalDate bookingDate;
    private long startTime;
    private long endTime;
    private BowlingAlley availableAlley;

    /**
     * The setClient function sets the client variable to the given Client object.
     * 
     * @param Client client Set the client field
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * The setTimeInfo function sets the bookingDate, startTime and endTime
     * variables.
     * The bookingDate is set to the date parameter.
     * The startTime is set to a LocalDateTime object created from the date and time
     * parameters, converted into epoch milliseconds using
     * {@code ZoneId.systemDefault().toEpochSecond()*1000L;}
     * The endtime is calculated by adding {@code durationInMin*60*1000L- 1ms} (to
     * avoid overlap) to starttime
     * 
     * @param LocalDate date Set the bookingdate variable
     * @param LocalTime time Set the starttime variable
     * @param int       durationInMin Set the duration of the booking
     *
     * @return A boolean value
     */
    public void setTimeInfo(LocalDate date, LocalTime time, int durationInMin) {
        bookingDate = date;
        startTime = LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L;
        endTime = startTime + durationInMin * 60 * 1000L - 1; // -1ms to avoid overlap
    }

    /**
     * The checkTime function checks if the booking time is in the past or not.
     * If it is, then an error message will be shown to the user.
     * 
     * @param AuthenticationContext authenticationContext Check if the current user
     *                              is an admin
     *
     * @return A boolean
     */
    public boolean checkTime(AuthenticationContext authenticationContext) {
        // admin can book at any time (mainly for testing)
        if (isCurrentUserInRole(authenticationContext, UserRole.ADMIN)) {
            return true;
        }

        // allow up to 15 minutes into the past
        if (startTime < System.currentTimeMillis() - Duration.ofMinutes(15).toMillis()) {
            Notifications.showError("Die Buchungszeit darf nicht in der Vergangenheit sein");
            return false;
        }
        if (!isInBusinessHours()) {
            Notifications.showError("Die Buchungszeit muss innerhalb der Geschäftszeit liegen");
            return false;
        }
        return true;
    }

    /**
     * The isInBusinessHours function checks if the booking is within the business
     * hours of a bowling center.
     * 
     * @return A boolean value
     */
    private boolean isInBusinessHours() {
        BowlingCenter bowlingCenter = Repos.getBowlingCenterRepository().getBowlingCenter();
        LocalDate nowLD = bookingDate;
        LocalDateTime startTimeLDT = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
        LocalDateTime endTimeLDT = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault());
        return !startTimeLDT.isBefore(LocalDateTime.of(nowLD, LocalTime.ofSecondOfDay(bowlingCenter.getStartTime())))
                && endTimeLDT.isBefore(LocalDateTime.of(nowLD, LocalTime.ofSecondOfDay(bowlingCenter.getEndTime())));
    }

    /**
     * The checkAvailability function checks if there is a free bowling alley
     * available for the given time period.
     * If there is one, it will be saved in the availableAlley variable and true
     * will be returned.
     * If not, false will be returned and no bowling alley will be saved in the
     * availableAlley variable.
     * 
     * @return True if there is a free alley, and false otherwise
     */
    public boolean checkAvailability() {
        List<BowlingAlley> freeAlleyList = Repos.getBowlingAlleyRepository()
                .findAllByNoBookingOverlapBetweenTimeStamps(startTime, endTime);
        if (freeAlleyList.isEmpty()) {
            availableAlley = null;
            return false;
        } else {
            availableAlley = freeAlleyList.get(0);
            return true;
        }
    }

    /**
     * The getAvailableAlleyId function returns the id of the availableAlley object.
     * If no such object exists, it returns 0.
     * 
     * @return The id of the availablealley
     */
    public int getAvailableAlleyId() {
        return availableAlley == null ? 0 : availableAlley.getId();
    }

    /**
     * The book function is used to book a bowling alley.
     * 
     * @return A bowling alley booking object
     */
    public BowlingAlleyBooking book() {
        if (client == null || !checkAvailability()) {
            Notifications.showInfo("Keine Bahn frei zu der ausgewählten Zeit");
            return null;
        }

        double pricePerMinute = Repos.getBowlingCenterRepository().getBowlingCenter().getBowlingAlleyPricePerHour()
                / 60;

        BowlingAlleyBooking booking = new BowlingAlleyBooking();
        booking.setClient(client);
        booking.setBowlingAlley(availableAlley);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPrice(pricePerMinute * booking.getDuration() / 1000 / 60);
        if (client.getAssociation() != null) {
            booking.setDiscount(client.getAssociation().getDiscount());
        }

        Repos.getBowlingAlleyBookingRepository().save(booking);

        return booking;
    }

    /**
     * The getStartTime function returns the startTime variable.
     * 
     * @return The starttime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * The getEndTime function returns the endTime variable.
     * 
     * @return The endtime variable
     */
    public long getEndTime() {
        return endTime;
    }
}