package de.softwareprojekt.bestbowl.jpa.entities.drink;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * 
 * @author Max
 */
@Entity
public class DrinkVariant implements Serializable {
    @Serial
    private static final long serialVersionUID = -4900276745273591624L;
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private Drink drink;
    private int ml;
    private double price;
    private boolean active;

    /**
     * The DrinkVariant function is used to create a new drink variant.
     * 
     * @return A drinkvariant object
     */
    public DrinkVariant() {
        active = true;
    }

    /**
     * The DrinkVariant function is a constructor that creates an instance of the
     * DrinkVariant class.
     * 
     * @param DrinkVariant other Create a new drinkvariant object
     *
     * @return A new drinkvariant object with the same values as other
     */
    public DrinkVariant(DrinkVariant other) {
        this.id = other.id;
        this.drink = other.drink;
        this.ml = other.ml;
        this.price = other.price;
        this.active = other.active;
    }

    /**
     * The copyValueOf function is used to copy the values of one DrinkVariant
     * object into another.
     * 
     * @param DrinkVariant other Copy the values from it to this object
     *
     * @return A copy of the drinkvariant object
     */
    public void copyValueOf(DrinkVariant other) {
        this.id = other.id;
        this.drink = other.drink;
        this.ml = other.ml;
        this.price = other.price;
        this.active = other.active;
    }

    /**
     * The getId function returns the id of thedrink variant.
     * 
     * @return The id of the employee
     */
    public int getId() {
        return id;
    }

    /**
     * The setId function sets the id of a given drink variant.
     * 
     * @param int id Set the id of the object
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getDrink function returns the drink object associated with this drink
     * variant.
     * 
     * @return A drink object
     */
    public Drink getDrink() {
        return drink;
    }

    /**
     * The setDrink function sets the drink for a given order.
     * 
     * @param Drink drink Set the drink variable in the class
     */
    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    /**
     * The getMl function returns the ml value of a drink variant.
     * 
     * @return The ml value
     */
    public int getMl() {
        return ml;
    }

    /**
     * The setMl function sets the ml variable to the value of its parameter.
     * 
     *
     * @param int ml Set the ml variable in the class
     *
     * @return Nothing, so the return type is void
     *
     * @docauthor Trelent
     */
    public void setMl(int ml) {
        this.ml = ml;
    }

    /**
     * The getPrice function returns the price of a drink variant.
     * 
     * @return The price of the item
     */
    public double getPrice() {
        return price;
    }

    /**
     * The setPrice function sets the price of a drink variant.
     * 
     * @param double price Set the price of the item
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * The isActive function returns a boolean value indicating whether or not the
     * drink variant is active.
     *
     * @return A boolean value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false.
     * 
     * @param boolean active Set the value of the active variable
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}