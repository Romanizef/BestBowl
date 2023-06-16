package de.softwareprojekt.bestbowl.jpa.idclasses;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * DinkBookingId is the composite key for the {@code DrinkBooking}
 * entity.
 *
 * @author Ali
 */
public class DrinkBookingId implements Serializable {
    @Serial
    private static final long serialVersionUID = 5181429790230031383L;

    private Client client;
    private BowlingAlley bowlingAlley;
    private long timeStamp;
    private String name;

    /**
     * The DrinkBookingId function is a composite primary key class for the
     * DrinkBooking entity.
     * It consists of two attributes: client and bowlingAlley.
     */
    public DrinkBookingId() {
    }

    /**
     * The getClient function returns the client object.
     *
     * @return The client object
     */
    public Client getClient() {
        return client;
    }

    /**
     * The setClient function sets the client variable to the given Client object.
     *
     * @param client Set the client object in the class
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * The getBowlingAlley function returns the bowlingAlley object.
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
     * @param bowlingAlley Set the bowlingalley variable
     */
    public void setBowlingAlley(BowlingAlley bowlingAlley) {
        this.bowlingAlley = bowlingAlley;
    }

    /**
     * The getTimeStamp function returns the timeStamp variable.
     *
     * @return The timestamp variable, which is a long
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * The setTimeStamp function sets the timeStamp variable to the value of its
     * parameter.
     *
     * @param timeStamp Set the timestamp variable
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * The getName function returns the name of a bowling alley.
     *
     * @return The name of the person
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of a bowling alley.
     *
     * @param name Set the name of the object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The equals function is used to compare two objects of the same class.
     * It returns true if all fields are equal, false otherwise.
     *
     * @param o Compare the object to another object
     * @return True if the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DrinkBookingId that = (DrinkBookingId) o;
        return timeStamp == that.timeStamp && Objects.equals(client, that.client)
                && Objects.equals(bowlingAlley, that.bowlingAlley) && Objects.equals(name, that.name);
    }

    /**
     * The hashCode function is used to generate a unique hash value for each
     * object.
     * This function is used by the HashMap class, which uses the hashCode of an
     * object as its key.
     * The HashMap then stores objects in buckets based on their hashCode values.
     * If two objects have different hashCodes, they will be stored in different
     * buckets and can be retrieved separately from one another.
     * However, if two objects have identical hashCodes (which should only happen if
     * they are equal), they will both be stored in the same bucket and may
     * overwrite one another when being added to a HashMap or other
     *
     * @return An int, but the equals function returns a boolean
     */
    @Override
    public int hashCode() {
        return Objects.hash(client, bowlingAlley, timeStamp, name);
    }
}