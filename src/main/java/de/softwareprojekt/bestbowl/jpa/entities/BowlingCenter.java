package de.softwareprojekt.bestbowl.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

/**
 * BowlingCenter is a JPA entity representing a bowling center.
 *
 * @author Marten Voß
 */
@Entity
public class BowlingCenter implements Serializable {
    @Serial
    private static final long serialVersionUID = -2875099517224293896L;
    @Id
    private int id;
    private String displayName;
    private String businessName;
    // address
    private String street;
    private int houseNr;
    private int postCode;
    private String city;
    // business hours
    private long startTime; // in seconds after day start
    private long endTime; // in seconds after day start
    // prices
    private double bowlingAlleyPricePerHour;
    private double bowlingShoePrice;
    // email server data
    private String senderEmail;
    private String receiverEmail;
    private String password;
    private String smtpHost;
    private String smtpPort;

    /**
     * The BowlingCenter function is a constructor that creates an object of type
     * BowlingCenter.
     */
    public BowlingCenter() {
    }

    /**
     * The getPostCodeString function returns a string representation of the
     * postCode field.
     * The format is 5 digits, with leading zeros if necessary.
     *
     * @return A string with the postcode formatted as a 5-digit number
     */
    public String getPostCodeString() {
        return String.format("%05d", postCode);
    }

    /**
     * The getId function returns the id of the bowling center.
     *
     * @return The id of the student
     */
    public int getId() {
        return id;
    }

    /**
     * The setId function sets the id of the bowling center.
     *
     * @param id Set the id of the object
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getDisplayName function returns the displayName of the bowling center.
     *
     * @return The displayname of the bowling center
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * The setDisplayName function sets the displayName variable to the value of its
     * parameter.
     *
     * @param displayName Set the displayname variable
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * The getBusinessName function returns the business name of the bowling center.
     *
     * @return The businessname variable
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * The setBusinessName function sets the businessName variable to the value of
     * its parameter.
     *
     * @param businessName Set the businessname field
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     * The getStreet function returns the street of the bowling center.
     *
     * @return The value of the street variable
     */
    public String getStreet() {
        return street;
    }

    /**
     * The setStreet function sets the street of the bowling center.
     *
     * @param street Set the value of the street variable
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * The getHouseNr function returns the house number of the bowling center
     *
     * @return The housenr variable
     */
    public int getHouseNr() {
        return houseNr;
    }

    /**
     * The setHouseNr function sets the house number of the bowling center.
     *
     * @param houseNr Set the housenr variable
     */
    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }

    /**
     * The getPostCode function returns the post code of the bowling center.
     *
     * @return The postcode variable
     */
    public int getPostCode() {
        return postCode;
    }

    /**
     * The setPostCode function sets the postCode variable to the value of its
     * parameter.
     *
     * @param postCode Set the postcode of the bowling center
     */
    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    /**
     * The getCity function returns the city of the bowling center.
     *
     * @return The city variable
     */
    public String getCity() {
        return city;
    }

    /**
     * The setCity function sets the city of the bowling center.
     *
     * @param city Set the city of the bowling center
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * The getStartTime function returns the startTime variable.
     *
     * @return The start time of the business hours
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * The setStartTime function sets the startTime variable to the value of its
     * parameter.
     *
     * @param startTime Set the starttime variable
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * The getEndTime function returns the endTime variable.
     *
     * @return The end time of the business hours
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * The setEndTime function sets the endTime variable to the value of its
     * parameter.
     *
     * @param endTime Set the endtime variable
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * The getBowlingAlleyPricePerHour function returns the price per hour of a
     * bowling alley.
     *
     * @return The value of the bowlingalleypriceperhour variable
     */
    public double getBowlingAlleyPricePerHour() {
        return bowlingAlleyPricePerHour;
    }

    /**
     * The setBowlingAlleyPricePerHour function sets the price per hour of a bowling
     * alley.
     *
     * @param bowlingAlleyPricePerHour Set the price per hour of a bowling
     *                                 alley
     */
    public void setBowlingAlleyPricePerHour(double bowlingAlleyPricePerHour) {
        this.bowlingAlleyPricePerHour = bowlingAlleyPricePerHour;
    }

    /**
     * The getBowlingShoePrice function returns the price of a bowling shoe.
     *
     * @return The value of the bowlingshoeprice variable
     */
    public double getBowlingShoePrice() {
        return bowlingShoePrice;
    }

    /**
     * The setBowlingShoePrice function sets the price of a bowling shoe.
     *
     * @param bowlingShoePrice Set the price of the bowling shoes
     */
    public void setBowlingShoePrice(double bowlingShoePrice) {
        this.bowlingShoePrice = bowlingShoePrice;
    }

    /**
     * The getSenderEmail function returns the sender's email address.
     *
     * @return The email address of the sender
     */
    public String getSenderEmail() {
        return senderEmail;
    }

    /**
     * The setSenderEmail function sets the senderEmail field to the email
     * parameter.
     *
     * @param email Set the senderemail field
     */
    public void setSenderEmail(String email) {
        this.senderEmail = email;
    }

    /**
     * The getReceiverEmail function returns the receiverEmail variable.
     *
     * @return The receiveremail variable
     */
    public String getReceiverEmail() {
        return receiverEmail;
    }

    /**
     * The setReceiverEmail function sets the receiverEmail field to the email
     * parameter.
     *
     * @param email Set the receiveremail variable
     */
    public void setReceiverEmail(String email) {
        this.receiverEmail = email;
    }

    /**
     * The getPassword function returns the password of the sender's email address.
     *
     * @return The password field
     */
    public String getPassword() {
        return password;
    }

    /**
     * The setPassword function sets the password of the sender's email address.
     *
     * @param password Set the password of the sender's email address
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The getSmtpHost function returns the smtpHost variable.
     *
     * @return The smtphost variable
     */
    public String getSmtpHost() {
        return smtpHost;
    }

    /**
     * The setSmtpHost function sets the smtpHost variable to the value of its
     * parameter.
     *
     * @param smtpHost Set the smtphost variable
     */
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    /**
     * The getSmtpPort function returns the smtpPort variable.
     *
     * @return The smtpport variable
     */
    public String getSmtpPort() {
        return smtpPort;
    }

    /**
     * The setSmtpPort function sets the smtpPort variable to the value of its
     * parameter.
     *
     * @param smtpPort Set the smtpport variable
     */
    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }
}