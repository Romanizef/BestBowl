package de.softwareprojekt.bestbowl.beans;

import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Marten Voß
 */
@Component
public class UserManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);
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
        for (User user : userList) {
            if (user.isActive()) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getName())
                        .password(user.getEncodedPassword())
                        .roles(user.getRole().toString())
                        .build();
                if (userDetailsManager.userExists(user.getName())) {
                    userDetailsManager.updateUser(userDetails);
                } else {
                    userDetailsManager.createUser(userDetails);
                }
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
    public void addNewUser(String name, String password, String email, UserRole userRole) {
        User user = new User();
        user.setName(name);
        user.setEncodedPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(userRole);
        Repos.getUserRepository().save(user);
        updateUsersFromDb();
        LOGGER.info("User: '" + name + "' added!");
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
