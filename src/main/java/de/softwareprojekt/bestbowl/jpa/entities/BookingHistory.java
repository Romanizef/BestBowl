package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Entity
public class BookingHistory implements Serializable {
    private static final long serialVersionUID = 2226728990930276146L;

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Client client;
    @ManyToOne
    private BowlingAlley bowlingAlley;
    private long timeStamp;
    private byte[] invoice;

    private boolean active;

    public BookingHistory() {
        active = true;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public byte[] getInvoice() {
        return invoice;
    }

    public void setInvoice(byte[] invoice) {
        this.invoice = invoice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
