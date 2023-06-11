package de.softwareprojekt.bestbowl.jpa.entities.client;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

/**
 * Address is a class that represents a client's address.
 * 
 * @author Ali
 */
@Entity
public class Address implements Serializable {
    @Serial
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

    /**
     * Constructor for the Address
     */
    public Address() {
    }

    /**
     * @return String
     */
    public String getPostCodeString() {
        return String.format("%05d", postCode);
    }

    /**
     * Getter for the id.
     * 
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the id.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the street.
     * 
     * @return String
     */
    public String getStreet() {
        return street;
    }

    /**
     * Setter for the street.
     * 
     * @param street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Getter for the house number.
     * 
     * @return int
     */
    public int getHouseNr() {
        return houseNr;
    }

    /**
     * Setter for the house number.
     * 
     * @param houseNr
     */
    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }

    /**
     * Getter for the city.
     * 
     * @return String
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter for the city.
     * 
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter for the post code.
     * 
     * @return int
     */
    public int getPostCode() {
        return postCode;
    }

    /**
     * Setter for the post code.
     * 
     * @param postCode
     */
    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    /**
     * Getter for the client.
     * 
     * @return Client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Setter for the client.
     * 
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
    }
}