package de.softwareprojekt.bestbowl.jpa.entities.bowling_shoe;

import java.io.Serial;
import java.io.Serializable;

import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * BowlingShoe is a JPA Entity representing a bowling shoe.
 * 
 * @author Ali
 */
@Entity
public class BowlingShoe implements Serializable {
    @Serial
    private static final long serialVersionUID = 991398573020179381L;

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Client client;
    private long boughtAt;
    private int size;
    private boolean active;

    /**
     * Constructor for the BowlingShoe class.
     */
    public BowlingShoe() {
        active = true;
    }

    /**
     * Constructor for the BowlingShoe class.
     * 
     * @param other
     */
    public BowlingShoe(BowlingShoe other) {
        this.id = other.id;
        this.size = other.size;
        this.boughtAt = other.boughtAt;
        this.active = other.active;
    }

    /**
     * Copies the values of the other object to this object.
     * 
     * @param other
     */
    public void copyValueOf(BowlingShoe other) {
        this.id = other.id;
        this.size = other.size;
        this.boughtAt = other.boughtAt;
        this.active = other.active;
    }

    /**
     * Getter for the id.
     * 
     * @return int
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
     * @return Client
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
     * Getter for the date of purchase.
     * 
     * @return long
     */
    public long getBoughtAt() {
        return boughtAt;
    }

    /**
     * Setter for the date of purchase.
     * 
     * @param boughtAt
     */
    public void setBoughtAt(long boughtAt) {
        this.boughtAt = boughtAt;
    }

    /**
     * Getter for the sizeof the shoe.
     * 
     * @return int
     */
    public int getSize() {
        return size;
    }

    /**
     * Setter for the size of the shoe.
     * 
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gettter for the active state.
     * 
     * @return boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for the active state.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}