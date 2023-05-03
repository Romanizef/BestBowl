package de.softwareprojekt.bestbowl.beans;

import de.softwareprojekt.bestbowl.jpa.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marten Voß
 */
@Component
public class UserManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);
    private final List<String> currentUserNameList = new ArrayList<>();
    private final Map<String, Boolean> userDrawerStateMap = new HashMap<>();
    private UserDetailsManager userDetailsManager;
    private PasswordEncoder passwordEncoder;

    /**
     * Adds all users from the db to the UserDetailsManager on application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        updateUsersFromDb();
    }

    /**
     * Adds or updates all users in the db to the UserDetailsManager
     * After this is called all users that are active can authenticate
     */
    public void updateUsersFromDb() {
        List<User> userList = Repos.getUserRepository().findAll();

        currentUserNameList.forEach(s -> userDetailsManager.deleteUser(s));
        currentUserNameList.clear();

        for (User user : userList) {
            if (user.isActive()) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getName())
                        .password(user.getEncodedPassword())
                        .roles(user.getRole())
                        .build();
                currentUserNameList.add(user.getName());
                userDetailsManager.createUser(userDetails);
                userDrawerStateMap.putIfAbsent(user.getName(), true);
            }
        }
    }

    /**
     * Creates a new User entity, encodes the password, saves it to the db and updates the UserDetailManager
     *
     * @param name     name of the user
     * @param password cleartext password of the user
     * @param email    e-mail of the user
     * @param userRole role of the user
     */
    public void addNewUser(String name, String password, String securityQuestionAnswer, String email, String userRole) {
        User user = new User();
        user.setName(name);
        user.setEncodedPassword(passwordEncoder.encode(password));
        user.setSecurityQuestionAnswer(securityQuestionAnswer);
        user.setEmail(email);
        user.setRole(userRole);
        Repos.getUserRepository().save(user);
        updateUsersFromDb();
    }

    /**
     * Changes the password of the user to the new password
     *
     * @param user     the user to be changed
     * @param password new password
     */
    public void changePassword(User user, String password) {
        user.setEncodedPassword(passwordEncoder.encode(password));
        Repos.getUserRepository().save(user);
        updateUsersFromDb();
    }

    /**
     * Encodes a password
     *
     * @param password password to be encoded
     * @return encoded password
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean getDrawerStateForUser(String userName) {
        return userDrawerStateMap.getOrDefault(userName, true);
    }

    public void toggleDrawerStateForUser(String userName) {
        userDrawerStateMap.put(userName, !getDrawerStateForUser(userName));
    }

    @Autowired
    public void setUserDetailsManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
