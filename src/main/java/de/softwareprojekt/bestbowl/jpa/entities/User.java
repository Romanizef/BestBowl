package de.softwareprojekt.bestbowl.jpa.entities;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * User is a JPA entity class that represents a user in the database.
 * 
 * @author Ali
 */
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -91524394447561603L;
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String email;
    private String encodedPassword;
    private String securityQuestion;
    private String securityQuestionAnswer;
    private String role;
    private boolean darkMode;
    private boolean active;

    /**
     * The User function is a constructor for the User class.
     * It sets the active variable to true, which means that
     * when a new user is created, they are automatically set to active.
     * 
     * @return The active variable
     */
    public User() {
        active = true;
    }

    /**
     * The User function is a constructor that takes in an object of type User and
     * copies the values from that object into this one.
     * 
     * @param User other Copy the properties of another user object into this one
     *
     * @return A user object with the same fields as other
     */
    public User(User other) {
        this.id = other.id;
        this.name = other.name;
        this.email = other.email;
        this.encodedPassword = other.encodedPassword;
        this.securityQuestion = other.securityQuestion;
        this.securityQuestionAnswer = other.securityQuestionAnswer;
        this.role = other.role;
        this.darkMode = other.darkMode;
        this.active = other.active;
    }

    /**
     * The copyValuesOf function copies the values of all fields from another User
     * object into this one.
     * This is useful for when you want to copy a user's information, but not their
     * ID or other unique identifiers.
     * 
     * @param User other Copy the values of the other user into this user
     *
     * @return A user object
     */
    public void copyValuesOf(User other) {
        this.id = other.id;
        this.name = other.name;
        this.email = other.email;
        this.encodedPassword = other.encodedPassword;
        this.securityQuestion = other.securityQuestion;
        this.securityQuestionAnswer = other.securityQuestionAnswer;
        this.role = other.role;
        this.darkMode = other.darkMode;
        this.active = other.active;
    }

    /**
     * The getId function returns the id of the user.
     * 
     * @return The value of the id variable
     */
    public int getId() {
        return id;
    }

    /**
     * The setId function sets the id of the user.
     * 
     * @param int id Set the id of the object
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getName function returns the name of the user.
     * 
     * @return The name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * The setName function sets the name of the user.
     * 
     * @param String name Set the name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The getEmail function returns the email of a user.
     * 
     * @return The email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * The setEmail function sets the email of a user.
     * 
     * @param String email Set the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The getEncodedPassword function returns the encoded password.
     * 
     * @return The encoded password
     */
    public String getEncodedPassword() {
        return encodedPassword;
    }

    /**
     * The setEncodedPassword function sets the encodedPassword field to the value
     * of its parameter.
     * 
     * @param String encodedPassword Set the encodedpassword variable
     */
    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    /**
     * The getSecurityQuestion function returns the security question of a user.
     * 
     * @return A string called securityquestion
     */
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    /**
     * The setSecurityQuestion function sets the security question for a user.
     * 
     * @param String securityQuestion Set the security question
     */
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    /**
     * The getSecurityQuestionAnswer function returns the security question answer.
     * 
     * @return The securityquestionanswer
     */
    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    /**
     * The setSecurityQuestionAnswer function sets the security question answer for
     * a user.
     * 
     * @param String securityQuestionAnswer Set the security question answer
     */
    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    /**
     * The getRole function returns the role of a user.
     * 
     * @return The role of the employee
     */
    public String getRole() {
        return role;
    }

    /**
     * The setRole function sets the role of a user.
     * 
     * @param String role Set the role of the user
     */
    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    /**
     * The setDarkMode function sets the darkMode variable to true or false.
     * 
     * @param boolean darkMode Determine whether the dark mode is enabled or not
     */
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    /**
     * The isActive function returns a boolean value indicating whether or not the
     * user is active.
     * 
     * @return A boolean value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The setActive function sets the active variable to true or false.
     * 
     * @param boolean active Set the value of the active variable
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}