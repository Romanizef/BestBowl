package de.softwareprojekt.bestbowl.beans;

import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class to manage the users and their permissions.
 *
 * @author Marten Voß
 */
@Component
public class UserManager {
    private final List<String> currentUserNameList = new ArrayList<>();
    private final Map<String, Boolean> userDrawerStateMap = new HashMap<>();
    private final UserRepository userRepository;
    private UserDetailsManager userDetailsManager;
    private PasswordEncoder passwordEncoder;

    /**
     * Constructor for the UserManager.
     *
     * @param userRepository
     */
    @Autowired
    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Adds or updates all users in the db to the UserDetailsManager
     * After this is called all users that are active can authenticate
     */
    public void updateUsersFromDb() {
        List<User> userList = userRepository.findAll();

        currentUserNameList.forEach(s -> userDetailsManager.deleteUser(s));
        currentUserNameList.clear();

        for (User user : userList) {
            if (user.isActive()) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getName())
                        .password(user.getEncodedPassword())
                        .roles(getSubRoles(user.getRole()))
                        .build();
                currentUserNameList.add(user.getName());
                userDetailsManager.createUser(userDetails);
                userDrawerStateMap.putIfAbsent(user.getName(), true);
            }
        }
    }

    private String[] getSubRoles(String role) {
        List<String> roles = new ArrayList<>();
        switch (role) {
            case UserRole.ADMIN -> {
                roles.add(UserRole.ADMIN);
                roles.add(UserRole.OWNER);
                roles.add(UserRole.EMPLOYEE);
            }
            case UserRole.OWNER -> {
                roles.add(UserRole.OWNER);
                roles.add(UserRole.EMPLOYEE);
            }
            case UserRole.EMPLOYEE -> roles.add(UserRole.EMPLOYEE);
        }
        return roles.toArray(new String[0]);
    }

    /**
     * Creates a new User entity, encodes the password, saves it to the db and
     * updates the {@code UserDetailManager}
     *
     * @param name     name of the user
     * @param password cleartext password of the user
     * @param email    e-mail of the user
     * @param userRole role of the user
     * @see #updateUsersFromDb()
     */
    public void addNewUser(String name, String password, String securityQuestion, String securityQuestionAnswer,
                           String email, String userRole) {
        User user = new User();
        user.setName(name);
        user.setEncodedPassword(passwordEncoder.encode(password));
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityQuestionAnswer(securityQuestionAnswer);
        user.setEmail(email);
        user.setRole(userRole);

        Optional<User> userWithSameName = userRepository.findByName(user.getName());
        userWithSameName.ifPresent(userRepository::delete);

        userRepository.save(user);
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
        userRepository.save(user);
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

    /**
     * Getter for the current drawer state of the user
     *
     * @param userName
     * @return {@code boolean}
     */
    public boolean getDrawerStateForUser(String userName) {
        return userDrawerStateMap.getOrDefault(userName, true);
    }

    /**
     * Toggles the current drawer state of the user
     *
     * @param userName
     */
    public void toggleDrawerStateForUser(String userName) {
        userDrawerStateMap.put(userName, !getDrawerStateForUser(userName));
    }

    /**
     * Getter for the current dark mode state of the user
     *
     * @param userName
     * @return {@code boolean}
     */
    public boolean getDarkModeStateForUser(String userName) {
        Optional<User> user = userRepository.findByName(userName);
        return user.map(User::isDarkMode).orElse(false);
    }

    /**
     * Setter for the darkmode
     *
     * @param userName
     * @param darkMode
     */
    public void setDarkModeStateForUser(String userName, boolean darkMode) {
        Optional<User> user = userRepository.findByName(userName);
        user.ifPresent(u -> {
            u.setDarkMode(darkMode);
            userRepository.save(u);
        });
    }

    /**
     * Setter for the {@code UserDetailsManager}
     *
     * @param userDetailsManager
     */
    @Autowired
    public void setUserDetailsManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    /**
     * Setter for the {@code PasswordEncoder}
     *
     * @param passwordEncoder
     */
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}