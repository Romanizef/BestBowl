package de.softwareprojekt.bestbowl.jpa.idclasses;

import java.io.Serializable;
import java.util.Objects;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;

/**
 * FoodBookingId is the composite key for the {@code FoodBooking} entity.
 * 
 * @author Ali
 */
public class FoodBookingId implements Serializable {
    private static final long serialVersionUID = -3469166002469195644L;
    private Client client;
    private BowlingAlley bowlingAlley;
    private long timeStamp;
    private String name;

    /**
     * The FoodBookingId function is a composite primary key class for the
     * FoodBooking entity.
     * It consists of two attributes: client and bowlingAlley.
     * 
     * @return A string
     */
    public FoodBookingId() {
    }

    /**
     * The getClient function returns the client object.
     * 
     * @return A client object
     */
    public Client getClient() {
        return client;
    }

    /**
     * The setClient function sets the client variable to the given Client object.
     * 
     * @param Client client Set the client variable to the value of the parameter
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
     * The setBowlingAlley function sets the bowlingAlley variable to the given
     * parameter.
     * 
     * @param BowlingAlley bowlingAlley Set the bowlingalley field in the class
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
     * @param long timeStamp Set the timestamp variable
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * The getName function returns the name of the food booking.
     * 
     * @return The name of the object
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of the food booking.
     * 
     * @param String name Set the name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The equals function is used to compare two objects of the same class.
     * It returns true if all fields are equal, false otherwise.
     * 
     * @param Object o Compare the current object with another object
     *
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FoodBookingId that = (FoodBookingId) o;
        return timeStamp == that.timeStamp && Objects.equals(client, that.client)
                && Objects.equals(bowlingAlley, that.bowlingAlley) && Objects.equals(name, that.name);
    }

    /**
     * The hashCode function is used to generate a unique hash value for each
     * object.
     * This function is called when the object needs to be stored in a HashTable,
     * HashMap or HashSet.
     * The hashCode function should return distinct integers for distinct objects.
     * If two objects are equal (using the equals method), then their hash code must
     * be same as well.
     * 
     * @return The hash code of the given object
     */
    @Override
    public int hashCode() {
        return Objects.hash(client, bowlingAlley, timeStamp, name);
    }
}