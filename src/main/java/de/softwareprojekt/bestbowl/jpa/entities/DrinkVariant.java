package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Entity
public class DrinkVariant implements Serializable {
    private static final long serialVersionUID = -4900276745273591624L;

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Drink drink;
    private int ml;
    private double price;

    public DrinkVariant() {
    }

    public DrinkVariant(DrinkVariant other){
        this.id = other.id;
        this.ml = other.ml;
        this.price = other.price;
    }

    public void copyValueOf(DrinkVariant other){
        this.id = other.id;
        this.ml = other.ml;
        this.price = other.price;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public int getMl() {
        return ml;
    }

    public void setMl(int ml) {
        this.ml = ml;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
