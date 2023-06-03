package de.softwareprojekt.bestbowl.jpa.entities.clientEntities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marten Voß
 */
@Entity
public class Association implements Serializable {
    public static final Association NO_ASSOCIATION;
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

    public Association() {
        clients = new HashSet<>();
        active = true;
    }

    public void addClient(Client client) {
        clients.add(client);
        client.setAssociation(this);
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
