package de.softwareprojekt.bestbowl.jpa.entities.client;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Client is a JPA entity that represents a client.
 *
 * @author Ali
 */
@Entity
public class Client implements Serializable {
    @Serial
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
    private String comment;

    private boolean active;

    /**
     * The Client function is a constructor for the Client class.
     * It initializes the comment and active fields to empty strings and true,
     * respectively.
     *
     * @return A client object
     */
    public Client() {
        comment = "";
        active = true;
    }

    /**
     * The addAddress function adds an address to the client.
     *
     * @param address Set the address of the client
     */
    public void addAddress(Address address) {
        this.address = address;
        address.setClient(this);
    }

    /**
     * The getFullName function returns the first name and last name of a person.
     *
     * @return A string with the first name and last name of the person
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * The getId function returns the id of the object.
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
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getFirstName function returns the first name of a person.
     *
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * The setFirstName function sets the first name of a client.
     *
     * @param firstName Set the firstname variable
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * The getLastName function returns the last name of a client.
     *
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * The setLastName function sets the last name of a client.
     *
     * @param lastName Set the lastname field
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * The getEmail function returns the email of a client.
     *
     * @return The email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * The setEmail function sets the email of a client.
     *
     * @param email Set the email field to the value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The getAddress function returns the address of a client.
     *
     * @return The address object
     */
    public Address getAddress() {
        return address;
    }

    /**
     * The setAddress function sets the address of a client.
     *
     * @param address Set the address of the customer
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * The getAssociation function returns the association object.
     *
     * @return {@code Association}
     */
    public Association getAssociation() {
        return association;
    }

    /**
     * The setAssociation function sets the association of a given object to an
     * Association.
     *
     * @param association Set the association field in the class
     */
    public void setAssociation(Association association) {
        this.association = association;
    }

    /**
     * The getComment function returns the comment.
     *
     * @return The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * The setComment function sets the comment field for the client.
     *
     * @param comment Set the comment for a particular review
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * The isActive function returns a boolean value indicating if the
     * Client is active.
     *
     * @return A boolean value of true or false
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false.
     *
     * @param active Set the value of the active variable
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}