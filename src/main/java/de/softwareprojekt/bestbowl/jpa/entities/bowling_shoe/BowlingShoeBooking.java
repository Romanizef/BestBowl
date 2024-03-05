package de.softwareprojekt.bestbowl.jpa.entities.bowling_shoe;

import java.io.Serial;
import java.io.Serializable;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.idclasses.BowlingShoeBookingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;

/**
 * BowlingShoeBooking is a JPA entity class that represents a booking for a
 * bowling shoe.
 * 
 * @author Ali
 */
@Entity
@IdClass(BowlingShoeBookingId.class)
public class BowlingShoeBooking implements Serializable {
    @Serial
    private static final long serialVersionUID = 7908165986912369981L;

    @Id
    @ManyToOne
    private Client client;
    @Id
    @ManyToOne
    private BowlingAlley bowlingAlley;
    @Id
    private long timeStamp;

    @Id
    @ManyToOne
    private BowlingShoe bowlingShoe;
    private double price;

    private boolean active;

    /**
     * Constructor for BowlingShoeBooking.
     */
    public BowlingShoeBooking() {
        active = true;
    }

    /**
     * Getter for the Client.
     * 
     * @return Client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Setter for the Client.
     * 
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Getter for the BowlingAlley.
     * 
     * @return BowlingAlley
     */
    public BowlingAlley getBowlingAlley() {
        return bowlingAlley;
    }

    /**
     * Setter for the BowlingAlley.
     * 
     * @param bowlingAlley
     */
    public void setBowlingAlley(BowlingAlley bowlingAlley) {
        this.bowlingAlley = bowlingAlley;
    }

    /**
     * Getter for the timeStamp of the booking. This is the time when the booking
     * was made.
     * 
     * @return long
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Setter for the timeStamp of the booking.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Getter for the BowlingShoe object.
     * 
     * @return BowlingShoe
     */
    public BowlingShoe getBowlingShoe() {
        return bowlingShoe;
    }

    /**
     * Setter for the BowlingShoe object.
     * 
     * @param bowlingShoe
     */
    public void setBowlingShoe(BowlingShoe bowlingShoe) {
        this.bowlingShoe = bowlingShoe;
    }

    /**
     * Getter for the active state.
     * 
     * @return boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for the active state.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Getter for the price.
     * 
     * @return double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter for the price.
     * 
     * @param price
     */
    public void setPrice(double price) {
        this.price = price;
    }
}