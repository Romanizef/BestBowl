package de.softwareprojekt.bestbowl.jpa.entities;

import de.softwareprojekt.bestbowl.jpa.idclasses.BowlingShoeBookingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

/**
 * @author Marten Voß
 */
@Entity
@IdClass(BowlingShoeBookingId.class)
public class BowlingShoeBooking implements Serializable {
    private static final long serialVersionUID = 7908165986912369981L;

    @Id
    @ManyToOne
    private Client client;
    @Id
    @ManyToOne
    private BowlingAlley bowlingAlley;
    @Id
    private long timeStamp;
    @ManyToOne
    private BowlingShoe bowlingShoe;
    private int amount;
    private boolean active;

    public BowlingShoeBooking() {
        active = true;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
