package de.softwareprojekt.bestbowl.utils;

import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

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
            return null;
        }

        BowlingAlleyBooking booking = new BowlingAlleyBooking();
        booking.setClient(client);
        booking.setBowlingAlley(availableAlley);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);

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
