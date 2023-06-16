package de.softwareprojekt.bestbowl.jpa.entities.food;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

/**
 * Food is a JPA entity representing a food item.
 *
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

    /**
     * The Food function is a constructor for the Food class.
     * It sets the active variable to true, which means that it is available in the
     * menu.
     */
    public Food() {
        active = true;
    }

    /**
     * The Food function is a constructor that takes in an object of type Food and
     * copies its values into the new object.
     *
     * @param other Copy the values of another food object into this one
     */
    public Food(Food other) {
        this.id = other.id;
        this.name = other.name;
        this.price = other.price;
        this.stock = other.stock;
        this.reorderPoint = other.reorderPoint;
        this.active = other.active;
    }

    /**
     * The copyValuesOf function copies the values of all fields from another
     * instance of this class.
     * This is useful for when you want to copy an object's state without having to
     * worry about if it has been persisted yet.
     *
     * @param other Pass the object that you want to copy
     */
    public void copyValuesOf(Food other) {
        this.id = other.id;
        this.name = other.name;
        this.price = other.price;
        this.stock = other.stock;
        this.reorderPoint = other.reorderPoint;
        this.active = other.active;
    }

    /**
     * The getId function returns the id of the food item.
     *
     * @return The id of the object
     */
    public int getId() {
        return id;
    }

    /**
     * The setId function sets the id of a food item.
     *
     * @param id Set the id of the object
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getName function returns the name of the food item.
     *
     * @return The name of the food item
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of a food item.
     *
     * @param name Set the name of the food item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The getStock function returns the stock of a food item.
     *
     * @return The stock variable
     */
    public int getStock() {
        return stock;
    }

    /**
     * The setStock function sets the stock of a food item.
     *
     * @param stock Set the stock of a food item
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * The getReorderPoint function returns the reorder point of a food item.
     *
     * @return The reorderpoint variable
     */
    public int getReorderPoint() {
        return reorderPoint;
    }

    /**
     * The setReorderPoint function sets the reorder point for a food item.
     *
     * @param reorderPoint Set the reorderpoint variable
     */
    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    /**
     * The getPrice function returns the price of a product.
     *
     * @return The price of the item
     */
    public double getPrice() {
        return price;
    }

    /**
     * The setPrice function sets the price of a product.
     *
     * @param price Set the price of the item
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * The isActive function returns a boolean value indicating if the
     * food item is active.
     *
     * @return A boolean value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false.
     *
     * @param active Set the value of the active field
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}