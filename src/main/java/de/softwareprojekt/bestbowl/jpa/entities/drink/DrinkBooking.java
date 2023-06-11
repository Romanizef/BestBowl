package de.softwareprojekt.bestbowl.jpa.entities.drink;

import java.io.Serial;
import java.io.Serializable;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;

/**
 * DirnkBooking is a JPA entity that represents a booking of a drink.
 * 
 * @author Max
 */
@Entity
@IdClass(DrinkBookingId.class)
public class DrinkBooking implements Serializable {
    @Serial
    private static final long serialVersionUID = -2299627824997591271L;
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
     * The DrinkBooking function is used to create a new DrinkBooking object.
     *
     * @return A drinkbooking object
     */
    public DrinkBooking() {
        active = true;
    }

    /**
     * The DrinkBooking function is used to create a new DrinkBooking object.
     * 
     * @param DrinkVariant        drinkVariant Get the name and price of the drink
     * @param BowlingAlleyBooking bowlingAlleyBooking Get the client, bowlingalley
     *                            and starttime from the booking
     *
     * @return A drinkbooking object
     */
    public DrinkBooking(DrinkVariant drinkVariant, BowlingAlleyBooking bowlingAlleyBooking) {
        client = bowlingAlleyBooking.getClient();
        bowlingAlley = bowlingAlleyBooking.getBowlingAlley();
        timeStamp = bowlingAlleyBooking.getStartTime();
        name = drinkVariant.getDrink().getName() + " " + drinkVariant.getMl();
        price = drinkVariant.getPrice();
        active = true;
    }

    /**
     * The getDrinkName function returns the name of a drink.
     * 
     * @return The substring of the name variable up to the last space
     */
    public String getDrinkName() {
        return name.substring(0, name.lastIndexOf(" "));
    }

    /**
     * The getMl function returns the amount of milliliters in a drink.
     * 
     * @return The number of milliliters in the bottle
     */
    public int getMl() {
        return Integer.parseInt(name.substring(name.lastIndexOf(" ") + 1));
    }

    /**
     * The getClient function returns the client associated with this DrinkBooking.
     * 
     * @return The client object
     */
    public Client getClient() {
        return client;
    }

    /**
     * The setClient function sets the client of a DrinkBooking.
     * 
     * @param Client client Set the client object to a new client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * The getBowlingAlley function returns the bowlingAlley object.
     * 
     * @return A bowlingalley object
     */
    public BowlingAlley getBowlingAlley() {
        return bowlingAlley;
    }

    /**
     * The setBowlingAlley function sets the bowlingAlley variable to the value of
     * its parameter.
     * 
     * @param BowlingAlley bowlingAlley Set the bowlingalley variable
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
     * The setTimeStamp function sets the timeStamp variable to the given value. The
     * timeStamp variable is a timestamp of when the booking was made.
     * 
     * @param long timeStamp Set the timestamp field to the value of timestamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * The getName function returns the name of the drink.
     * 
     * @return The name of the student
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of a drink.
     * 
     * @param String name Set the name of the person
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The getAmount function returns the amount of drinks that have been ordered.
     * 
     * @return The amount of money in the account
     */
    public int getAmount() {
        return amount;
    }

    /**
     * The setAmount function sets the amount of drinks to be ordered.
     * 
     * @param int amount Set the amount of money in the account
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * The getPrice function returns the price of a drink.
     * 
     * @return The price of the item
     */
    public double getPrice() {
        return price;
    }

    /**
     * The setPrice function sets the price of a drink.
     * 
     * @param double price Set the price of the item
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * The isActive function returns a boolean value indicating whether the drink
     * booking is active.
     * 
     * @return A boolean value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false.
     * 
     * @param boolean active Determine whether the drinkBooking is active or not
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}