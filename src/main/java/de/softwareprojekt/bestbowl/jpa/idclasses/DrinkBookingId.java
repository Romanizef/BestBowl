package de.softwareprojekt.bestbowl.jpa.idclasses;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.Client;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Marten Voß
 */
public class DrinkBookingId implements Serializable {
    private static final long serialVersionUID = 5181429790230031383L;

    private Client client;
    private BowlingAlley bowlingAlley;
    private long timeStamp;

    public DrinkBookingId() {
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public BowlingAlley getBowlingAlley() {
        return bowlingAlley;
    }

    public void setBowlingAlley(BowlingAlley bowlingAlley) {
        this.bowlingAlley = bowlingAlley;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrinkBookingId that = (DrinkBookingId) o;
        return timeStamp == that.timeStamp && Objects.equals(client, that.client) && Objects.equals(bowlingAlley, that.bowlingAlley);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, bowlingAlley, timeStamp);
    }
}
