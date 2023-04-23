package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marten Voß
 */
@Entity
public class Drink implements Serializable {
    private static final long serialVersionUID = 679638727317395915L;

    @Id
    @GeneratedValue
    private int id;

    private String name;
    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<DrinkVariant> drinkVariants;
    private int stockInMilliliters;
    private int reorderPoint;

    private boolean active;

    public Drink() {
        drinkVariants = new HashSet<>();
        active = true;
    }

    public void addDrinkVariant(DrinkVariant drinkVariant) {
        drinkVariants.add(drinkVariant);
        drinkVariant.setDrink(this);
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

    public Set<DrinkVariant> getDrinkVariants() {
        return drinkVariants;
    }

    public void setDrinkVariants(Set<DrinkVariant> drinkVariants) {
        this.drinkVariants = drinkVariants;
    }

    public int getStockInMilliliters() {
        return stockInMilliliters;
    }

    public void setStockInMilliliters(int stockInMilliliters) {
        this.stockInMilliliters = stockInMilliliters;
    }

    public int getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
