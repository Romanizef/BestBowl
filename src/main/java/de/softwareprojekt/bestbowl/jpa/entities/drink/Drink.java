package de.softwareprojekt.bestbowl.jpa.entities.drink;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Drink is a JPA entity representing a drink in the database.
 *
 * @author Max
 */
@Entity
public class Drink implements Serializable {
    public static final Drink NO_DRINK;
    @Serial
    private static final long serialVersionUID = 679638727317395915L;

    static {
        NO_DRINK = new Drink();
        NO_DRINK.setId(-1);
        NO_DRINK.setName("-");
    }

    @Id
    @GeneratedValue
    private int id;
    private String name;
    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<DrinkVariant> drinkVariants;
    private int stockInMilliliters;
    private int reorderPoint;
    private boolean active;

    /**
     * The Drink function is a constructor that creates an instance of the Drink
     * class.
     * It initializes the drinkVariants field to be a new HashSet, and sets active
     * to true.
     *
     * @return A new drink object
     */
    public Drink() {
        drinkVariants = new HashSet<>();
        active = true;
    }

    /**
     * The Drink function is a constructor that creates an object of type Drink.
     *
     * @param other Copy the values of another drink object into this one
     * @return A new drink with the same properties as the other drink
     */
    public Drink(Drink other) {
        this.id = other.id;
        this.name = other.name;
        this.stockInMilliliters = other.stockInMilliliters;
        this.reorderPoint = other.reorderPoint;
        this.active = other.active;
    }

    /**
     * The addDrinkVariant function adds a DrinkVariant to the drinkVariants Set.
     *
     * @param drinkVariant Add a drink variant to the list of drink
     *                     variants
     */
    public void addDrinkVariant(DrinkVariant drinkVariant) {
        drinkVariants.add(drinkVariant);
        drinkVariant.setDrink(this);
    }

    /**
     * The copyValuesOf function copies the values of all fields from another
     * instance of Drink into this one.
     *
     * @param other
     */
    public void copyValuesOf(Drink other) {
        this.id = other.id;
        this.name = other.name;
        this.stockInMilliliters = other.stockInMilliliters;
        this.reorderPoint = other.reorderPoint;
        this.active = other.active;
    }

    /**
     * The getId function returns the id of the drink.
     *
     * @return The id of the drink
     */
    public int getId() {
        return id;
    }

    /**
     * The setId function sets the id of a drink to the value passed in as an
     * argument.
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getName function returns the name of a drink.
     *
     * @return The name of the drink
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of a drink.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The getDrinkVariants function returns a set of drink variants.
     *
     * @return A set of drinkvariant objects
     */
    public Set<DrinkVariant> getDrinkVariants() {
        return drinkVariants;
    }

    /**
     * The setDrinkVariants function is used to set the drinkVariants variable.
     *
     * @param drinkVariants
     * @return A set of drinkvariant objects
     */
    public void setDrinkVariants(Set<DrinkVariant> drinkVariants) {
        this.drinkVariants = drinkVariants;
    }

    /**
     * The getStockInMilliliters function returns the stockInMilliliters variable.
     *
     * @return The stockinmilliliters variable
     */
    public int getStockInMilliliters() {
        return stockInMilliliters;
    }

    /**
     * The setStockInMilliliters function sets the stockInMilliliters variable to
     * the value of its parameter.
     *
     * @param stockInMilliliters
     */
    public void setStockInMilliliters(int stockInMilliliters) {
        this.stockInMilliliters = stockInMilliliters;
    }

    /**
     * The getReorderPoint function returns the reorderPoint variable.
     *
     * @return The reorderpoint variable
     */
    public int getReorderPoint() {
        return reorderPoint;
    }

    /**
     * The setReorderPoint function sets the reorderPoint variable to the value of
     * its parameter.
     *
     * @param reorderPoint
     */
    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    /**
     * The isActive function returns a boolean value indicating if the
     * drink is active.
     *
     * @return A boolean value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false. Determining
     * if the drink is active or not.
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}