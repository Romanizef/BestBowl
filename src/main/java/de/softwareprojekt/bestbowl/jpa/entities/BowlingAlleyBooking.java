package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Marten Voß
 */
@Entity
public class BowlingAlleyBooking implements Serializable {
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
    private boolean completed;
    private double price;



    private boolean active;

    public BowlingAlleyBooking() {
        active = true;
    }

    public long getDuration() {
        return endTime + 1 - startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
