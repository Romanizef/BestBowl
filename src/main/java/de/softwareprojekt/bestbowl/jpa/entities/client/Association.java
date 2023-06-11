package de.softwareprojekt.bestbowl.jpa.entities.client;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Association is a JPA entity class that represents an association.
 * 
 * @author Ali
 */
@Entity
public class Association implements Serializable {
    public static final Association NO_ASSOCIATION;
    @Serial
    private static final long serialVersionUID = 3267080689333108719L;

    static {
        NO_ASSOCIATION = new Association();
        NO_ASSOCIATION.setId(-1);
        NO_ASSOCIATION.setName("-");
    }

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private double discount;
    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Client> clients;

    private boolean active;

    /**
     * The Association function is a constructor that creates an Association object.
     * It initializes the clients field to be a new HashSet, and sets active to
     * true.
     * 
     */
    public Association() {
        clients = new HashSet<>();
        active = true;
    }

    /**
     * The addClient function adds a client to the association.
     * 
     * @param Client client Add a client to the list of clients
     *
     * @return Void
     */
    public void addClient(Client client) {
        clients.add(client);
        client.setAssociation(this);
    }

    /**
     * The getId function returns the id of a given object.
     * 
     * @return The id of the object
     */
    public int getId() {
        return id;
    }

    /**
     * The setId function sets the id of a given object to the value passed in as an
     * argument.
     * 
     * @param int id Set the id of the object
     *
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getName function returns the name of a given person.
     * 
     * @return The name of the object
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of a given object.
     * 
     * @param String name Set the name of the person
     *
     * @return Void, so it doesn't return anything
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The getDiscount function returns the discount of a particular item.
     * 
     * @return The discount variable
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * The setDiscount function sets the discount of a product.
     * 
     * @param double discount Set the discount rate of the product
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * The getClients function returns a set of clients.
     * 
     * @return A set of client objects
     */
    public Set<Client> getClients() {
        return clients;
    }

    /**
     * The setClients function is used to set a Set of clients.
     * 
     * @param Set&lt;Client&gt; clients Set the clients field
     */
    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    /**
     * The isActive function returns a boolean value indicating whether or not the
     * association is active.
     * 
     * @return A boolean value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false.
     * 
     * @param boolean active Set the active variable to true or false
     *
     * @return A boolean value
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}