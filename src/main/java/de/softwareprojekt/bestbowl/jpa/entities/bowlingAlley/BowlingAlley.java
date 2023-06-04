package de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;

/**
 * @author Marten Voß
 */
@Entity
public class BowlingAlley implements Serializable {
    private static final long serialVersionUID = -5386396429886134586L;

    @Id
    private int id;

    private boolean active;

    public BowlingAlley() {
        active = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
