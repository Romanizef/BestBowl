package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author Marten Voß
 */
@Entity
public class Address implements Serializable {
    private static final long serialVersionUID = 2927541278047572706L;

    @Id
    @Column(name = "client_id")
    private int id;

    private String street;
    private int houseNr;
    private String city;
    private int postCode;
    @OneToOne
    @MapsId
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Address() {
    }

    public String getPostCodeString() {
        return String.format("%05d", postCode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
