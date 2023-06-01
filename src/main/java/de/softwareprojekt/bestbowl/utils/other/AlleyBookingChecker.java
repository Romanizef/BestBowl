package de.softwareprojekt.bestbowl.utils.other;

import com.vaadin.flow.spring.security.AuthenticationContext;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;

import java.time.*;
import java.util.List;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.isCurrentUserInRole;

/**
 * @author Marten Voß
 */
public class AlleyBookingChecker {
    private Client client;
    private long startTime;
    private long endTime;
    private BowlingAlley availableAlley;

    public void setClient(Client client) {
        this.client = client;
    }

    public void setTimeInfo(LocalDate date, LocalTime time, int durationInMin) {
        startTime = LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L;
        endTime = startTime + durationInMin * 60 * 1000L - 1; // -1ms to avoid overlap
    }

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

    private boolean isInBusinessHours() {
        BowlingCenter bowlingCenter = Repos.getBowlingCenterRepository().getBowlingCenter();
        LocalDate nowLD = LocalDate.now();
        LocalDateTime startTimeLDT = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
        LocalDateTime endTimeLDT = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault());
        return !startTimeLDT.isBefore(LocalDateTime.of(nowLD, LocalTime.ofSecondOfDay(bowlingCenter.getStartTime())))
                && endTimeLDT.isBefore(LocalDateTime.of(nowLD, LocalTime.ofSecondOfDay(bowlingCenter.getEndTime())));
    }

    public boolean checkAvailability() {
        List<BowlingAlley> freeAlleyList = Repos.getBowlingAlleyRepository().findAllByNoBookingOverlapBetweenTimeStamps(startTime, endTime);
        if (freeAlleyList.isEmpty()) {
            availableAlley = null;
            return false;
        } else {
            availableAlley = freeAlleyList.get(0);
            return true;
        }
    }

    public int getAvailableAlleyId() {
        return availableAlley == null ? 0 : availableAlley.getId();
    }

    public BowlingAlleyBooking book() {
        if (client == null || !checkAvailability()) {
            Notifications.showInfo("Keine Bahn frei zu der ausgewählten Zeit");
            return null;
        }

        double pricePerMinute = Repos.getBowlingCenterRepository().getBowlingCenter().getBowlingAlleyPricePerHour() / 60;

        BowlingAlleyBooking booking = new BowlingAlleyBooking();
        booking.setClient(client);
        booking.setBowlingAlley(availableAlley);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPrice(pricePerMinute * booking.getDuration() / 1000 / 60);

        Repos.getBowlingAlleyBookingRepository().save(booking);

        return booking;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
