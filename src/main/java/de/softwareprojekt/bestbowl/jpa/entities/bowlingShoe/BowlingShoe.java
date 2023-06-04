package de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

import de.softwareprojekt.bestbowl.jpa.entities.client.Client;

/**
 * @author Marten Voß
 */
@Entity
public class BowlingShoe implements Serializable {
    private static final long serialVersionUID = 991398573020179381L;

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Client client;
    private long boughtAt;
    private int size;
    private boolean active;

    public BowlingShoe() {
        active = true;
    }

    public BowlingShoe(BowlingShoe other){
        this.id = other.id;
        this.size = other.size;
        this.boughtAt = other.boughtAt;
        this.active = other.active;
    }

    public void copyValueOf(BowlingShoe other){
        this.id = other.id;
        this.size = other.size;
        this.boughtAt = other.boughtAt;
        this.active = other.active;
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

    public long getBoughtAt() {
        return boughtAt;
    }

    public void setBoughtAt(long boughtAt) {
        this.boughtAt = boughtAt;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
