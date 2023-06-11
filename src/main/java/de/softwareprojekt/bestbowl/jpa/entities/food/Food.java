package de.softwareprojekt.bestbowl.jpa.entities.food;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Max
 */
@Entity
public class Food implements Serializable {
    @Serial
    private static final long serialVersionUID = 8272575838721537250L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private double price;
    private int stock;
    private int reorderPoint;

    private boolean active;

    public Food() {
        active = true;
    }

    public Food(Food other) {
        this.id = other.id;
        this.name = other.name;
        this.price = other.price;
        this.stock = other.stock;
        this.reorderPoint = other.reorderPoint;
        this.active = other.active;
    }

    public void copyValuesOf(Food other) {
        this.id = other.id;
        this.name = other.name;
        this.price = other.price;
        this.stock = other.stock;
        this.reorderPoint = other.reorderPoint;
        this.active = other.active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
