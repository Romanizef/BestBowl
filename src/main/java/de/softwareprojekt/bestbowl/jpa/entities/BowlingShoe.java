package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author Marten Voß
 */
@Entity
public class BowlingShoe implements Serializable {
    private static final long serialVersionUID = 991398573020179381L;

    @Id
    @GeneratedValue
    private int id;

    private long boughtAt;
    @ManyToOne
    private Client client;
    private int stock;
    private boolean active;
    private int size;

    public BowlingShoe() {
        active = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getBoughtAt() {
        return boughtAt;
    }

    public void setBoughtAt(long boughtAt) {
        this.boughtAt = boughtAt;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
