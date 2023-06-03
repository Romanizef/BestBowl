package de.softwareprojekt.bestbowl.jpa.entities.bowlingcenterAnduserEntities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

/**
 * @author Marten Voß
 */
@Entity
@Table(name = "users")
public class User implements Serializable {
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

    public User() {
        active = true;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
