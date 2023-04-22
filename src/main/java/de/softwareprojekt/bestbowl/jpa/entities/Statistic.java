package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Matija Kopschek
 */
@Entity
public class Statistic implements Serializable{
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private int cID;
    private int cLastName;
    private Date date;
    private double total;
    private boolean active;

    public Statistic() {
        active = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getcID() {
        return cID;
    }

    public void setcID(int cID) {
        this.cID = cID;
    }

    public int getcLastName() {
        return cLastName;
    }

    public void setcLastName(int cLastName) {
        this.cLastName = cLastName;
    }
}