package de.softwareprojekt.bestbowl.jpa.entities.bowling_alley;

import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.io.Serial;
import java.io.Serializable;

/**
 * BowlingAlleyBooking is a JPA entity representing a booking of a bowling
 * alley.
 * 
 * @author Matija
 */
@Entity
public class BowlingAlleyBooking implements Serializable {
    @Serial
    private static final long serialVersionUID = -2451218547318750126L;

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Client client;
    @ManyToOne
    private BowlingAlley bowlingAlley;
    private long startTime;
    private long endTime;
    private double price;
    private double discount;
    private boolean completed;
    private boolean active;

    /**
     * Constructor for a new BowlingAlleyBooking.
     */
    public BowlingAlleyBooking() {
        active = true;
    }

    /**
     * Getter for the duration of the booking.
     * 
     * @return {@code long}
     */
    public long getDuration() {
        return endTime + 1 - startTime;
    }

    /**
     * Getter for the price with discount.
     * 
     * @return {@code double}
     */
    public double getPriceWithDiscount() {
        return price * (100 - discount) / 100;
    }

    /**
     * Getter for the id.
     * 
     * @return {@code int}
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the id.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the client.
     * 
     * @return {@code Client}
     */
    public Client getClient() {
        return client;
    }

    /**
     * Setter for the client.
     * 
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Getter for the bowling alley.
     * 
     * @return {@code BowlingAlley}
     */
    public BowlingAlley getBowlingAlley() {
        return bowlingAlley;
    }

    /**
     * Setter for the bowling alley.
     * 
     * @param bowlingAlley
     */
    public void setBowlingAlley(BowlingAlley bowlingAlley) {
        this.bowlingAlley = bowlingAlley;
    }

    /**
     * Getter for the start time of the booking.
     * 
     * @return {@code long}
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Setter for the start time of the booking.
     * 
     * @param startTime
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Getter for the end time of the booking.
     * 
     * @return {@code long}
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Setter for the end time of the booking.
     * 
     * @param endTime
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Getter for the price.
     * 
     * @return {@code double}
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

    /**
     * Getter for the completed status.
     * 
     * @return {@code boolean}
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Setter for the completed status.
     * 
     * @param completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Getter for the discount.
     * 
     * @return {@code double}
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Setter for the discount.
     * 
     * @param discount
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * Getter for the active status.
     * 
     * @return {@code boolean}
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for the active status.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}