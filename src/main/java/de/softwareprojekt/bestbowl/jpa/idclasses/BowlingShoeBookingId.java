package de.softwareprojekt.bestbowl.jpa.idclasses;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * BowlingShoeBookingId is the composite key for the {@code BowlingShoeBooking}
 * entity.
 *
 * @author Ali
 */
public class BowlingShoeBookingId implements Serializable {
    @Serial
    private static final long serialVersionUID = 7549075836963205963L;

    private Client client;
    private BowlingAlley bowlingAlley;
    private long timeStamp;
    private BowlingShoe bowlingShoe;

    /**
     * The BowlingShoeBookingId function is a composite primary key class for the
     * BowlingShoeBooking entity.
     * It consists of two attributes: client and bowlingAlley, which are both
     * foreign keys to their respective entities.
     */
    public BowlingShoeBookingId() {
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
     * @param client Set the client object to the one passed in
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
     * @param bowlingAlley Set the bowlingalley field
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
     * @param timeStamp Set the timestamp variable
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * The getBowlingShoe function returns the bowlingShoe object.
     *
     * @return The bowlingshoe object
     */
    public BowlingShoe getBowlingShoe() {
        return bowlingShoe;
    }

    /**
     * The setBowlingShoe function sets the bowlingShoe variable to the value of its
     * parameter.
     *
     * @param bowlingShoe Set the bowlingshoe field to the value of the
     *                    parameter
     */
    public void setBowlingShoe(BowlingShoe bowlingShoe) {
        this.bowlingShoe = bowlingShoe;
    }

    /**
     * The equals function is used to compare two objects of the same type.
     * It returns true if all fields are equal, false otherwise.
     *
     * @param o Compare the current object with another object
     * @return A boolean, so you can't return a hashcode from it
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BowlingShoeBookingId that = (BowlingShoeBookingId) o;
        return timeStamp == that.timeStamp && Objects.equals(client, that.client)
                && Objects.equals(bowlingAlley, that.bowlingAlley) && Objects.equals(bowlingShoe, that.bowlingShoe);
    }

    /**
     * The hashCode function is used to generate a unique hash value for each
     * object.
     * This function is called when the object needs to be stored in a HashTable,
     * HashMap or HashSet.
     * The hashCode function should return distinct integers for distinct objects.
     * If two objects are equal according to the equals(Object) method, then calling
     * the hashCode method on each of the two objects must produce the same integer
     * result (unless an exception is thrown).
     *
     * @return The hashcode of the objects
     */
    @Override
    public int hashCode() {
        return Objects.hash(client, bowlingAlley, timeStamp, bowlingShoe);
    }
}