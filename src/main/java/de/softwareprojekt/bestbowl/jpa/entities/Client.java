package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author Marten Voß
 */
@Entity
public class Client implements Serializable {
    private static final long serialVersionUID = 4443604125629860648L;

    @Id
    @GeneratedValue
    private int id;

    private String firstName;
    private String lastName;
    private String email;
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Address address;
    @ManyToOne
    private Association association;

    private boolean active;

    public Client() {
        active = true;
    }

    public void addAddress(Address address) {
        this.address = address;
        address.setClient(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
