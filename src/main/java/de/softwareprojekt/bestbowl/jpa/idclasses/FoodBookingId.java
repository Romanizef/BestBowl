package de.softwareprojekt.bestbowl.jpa.idclasses;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.Client;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Marten Voß
 */
public class FoodBookingId implements Serializable {
    private static final long serialVersionUID = -3469166002469195644L;

    private Client client;
    private BowlingAlley bowlingAlley;
    private long timeStamp;
    private String name;

    public FoodBookingId() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodBookingId that = (FoodBookingId) o;
        return timeStamp == that.timeStamp && Objects.equals(client, that.client) && Objects.equals(bowlingAlley, that.bowlingAlley) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, bowlingAlley, timeStamp, name);
    }
}
