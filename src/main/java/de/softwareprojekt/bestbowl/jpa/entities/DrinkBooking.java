package de.softwareprojekt.bestbowl.jpa.entities;

import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Entity
@IdClass(DrinkBookingId.class)
public class DrinkBooking implements Serializable {
    private static final long serialVersionUID = -2299627824997591271L;

    @Id
    @ManyToOne
    private Client client;
    @Id
    @ManyToOne
    private BowlingAlley bowlingAlley;
    @Id
    private long timeStamp;

    @ManyToOne
    private Drink drink;

    private boolean active;

    public DrinkBooking() {
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

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
