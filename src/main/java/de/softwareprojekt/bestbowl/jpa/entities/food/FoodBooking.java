package de.softwareprojekt.bestbowl.jpa.entities.food;

import java.io.Serial;
import java.io.Serializable;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.idclasses.FoodBookingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;

/**
 * FoodBooking is a JPA entity class that represents a booking of food.
 * 
 * @author Max
 */
@Entity
@IdClass(FoodBookingId.class)
public class FoodBooking implements Serializable {
    @Serial
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

    /**
     * The FoodBooking function is used to book food for a client at a bowling
     * alley.
     * 
     * @return A boolean value, true or false
     */
    public FoodBooking() {
        active = true;
    }

    /**
     * The FoodBooking function is used to create a new FoodBooking object.
     * 
     * @param Food                food Get the name and price of the food
     * @param BowlingAlleyBooking bowlingAlleyBooking Get the client, bowlingalley
     *                            and starttime from
     *
     * @return A foodbooking object, which is a subclass of booking
     */
    public FoodBooking(Food food, BowlingAlleyBooking bowlingAlleyBooking) {
        client = bowlingAlleyBooking.getClient();
        bowlingAlley = bowlingAlleyBooking.getBowlingAlley();
        timeStamp = bowlingAlleyBooking.getStartTime();
        name = food.getName();
        price = food.getPrice();
        active = true;
    }

    /**
     * The getClient function returns the client object associated with this
     * FoodBooking.
     * 
     * @return The client object
     */
    public Client getClient() {
        return client;
    }

    /**
     * The setClient function sets the client of a FoodBooking.
     * 
     * @param Client client Set the client of this class
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * The getBowlingAlley function returns the bowlingAlley variable.
     * 
     * @return The bowlingalley object
     */
    public BowlingAlley getBowlingAlley() {
        return bowlingAlley;
    }

    /**
     * The setBowlingAlley function sets the bowlingAlley variable to the value of
     * its parameter.
     * 
     * @param BowlingAlley bowlingAlley Set the bowlingalley variable in the class
     */
    public void setBowlingAlley(BowlingAlley bowlingAlley) {
        this.bowlingAlley = bowlingAlley;
    }

    /**
     * The getTimeStamp function returns the timeStamp variable.
     * 
     * @return The timestamp variable
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * The setTimeStamp function sets the timeStamp variable to the value of its
     * parameter.
     * 
     * @param long timeStamp Set the timestamp field of the class
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * The getName function returns the name of the food item.
     * 
     * @return The name of the object
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of a FoodBooking object.
     * 
     * @param String name Set the name of the object
     *
     * @return Nothing, so it is a void function
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The getAmount function returns the amount of food ordered.
     * 
     * @return The amount of the transaction
     */
    public int getAmount() {
        return amount;
    }

    /**
     * The setAmount function sets the amount of food to be ordered.
     * 
     * @param int amount Set the amount of money in the account
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * The getPrice function returns the price of a food item.
     * 
     * @return The price of the item
     */
    public double getPrice() {
        return price;
    }

    /**
     * The setPrice function sets the price of a food item.
     * 
     * @param double price Set the price of the item
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * The isActive function returns a boolean value indicating whether the
     * FoodBooking is active or not.
     * 
     * @return A boolean value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false.
     * 
     * @param boolean active Set the value of the active variable
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * The toString function is used to print out the contents of a FoodBooking
     * object.
     * 
     * @return A string
     */
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