package de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * BowlingAlley is a JPA entity representing a bowling alley.
 * 
 * @author Matija
 */
@Entity
public class BowlingAlley implements Serializable {
    @Serial
    private static final long serialVersionUID = -5386396429886134586L;

    @Id
    private int id;
    private boolean active;

    /**
     * Constructor for the BowlingAlley Entity
     */
    public BowlingAlley() {
        active = true;
    }

    /**
     * Getter for the id
     * 
     * @return {@code int}
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the id
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the active state
     * 
     * @return {@code boolean}
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for the active state
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}