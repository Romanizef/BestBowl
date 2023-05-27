package de.softwareprojekt.bestbowl.jpa.idclasses;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.Client;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Marten Voß
 */
public class BowlingShoeBookingId implements Serializable {
    private static final long serialVersionUID = 7549075836963205963L;

    private Client client;
    private BowlingAlley bowlingAlley;
    private long timeStamp;
    private BowlingShoe bowlingShoe;

    public BowlingShoeBookingId() {
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

    public BowlingShoe getBowlingShoe() {
        return bowlingShoe;
    }

    public void setBowlingShoe(BowlingShoe bowlingShoe) {
        this.bowlingShoe = bowlingShoe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BowlingShoeBookingId that = (BowlingShoeBookingId) o;
        return timeStamp == that.timeStamp && Objects.equals(client, that.client) && Objects.equals(bowlingAlley, that.bowlingAlley) && Objects.equals(bowlingShoe, that.bowlingShoe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, bowlingAlley, timeStamp, bowlingShoe);
    }
}
