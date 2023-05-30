package de.softwareprojekt.bestbowl.jpa.entities;

import de.softwareprojekt.bestbowl.jpa.idclasses.FoodBookingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

/**
 * @author Marten Voß
 */
@Entity
@IdClass(FoodBookingId.class)
public class FoodBooking implements Serializable {
    private static final long serialVersionUID = -8958760050661309105L;

    @Id
    @ManyToOne
    private Client client;
    @Id
    @ManyToOne
    private BowlingAlley bowlingAlley;
    @Id
    private long timeStamp;

    @Id
    private String name;
    private int amount;
    private double price;

    private boolean active;

    public FoodBooking() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "FoodBooking{" +
                "client=" + client +
                ", bowlingAlley=" + bowlingAlley +
                ", timeStamp=" + timeStamp +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", active=" + active +
                '}';
    }
}
