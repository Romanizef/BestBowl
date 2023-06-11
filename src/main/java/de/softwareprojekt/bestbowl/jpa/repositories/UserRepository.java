package de.softwareprojekt.bestbowl.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.User;

/**
 * UserRepository is a Spring Data JPA repository for the {@link User} entities.
 * 
 * @author Ali
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * The findByName method returns an Optional of User with the given name.
     * 
     * @param name
     * @return Optional of User with the given name.
     */
    Optional<User> findByName(String name);

    /**
     * The findAllNames method returns a Set of all names in the User table.
     * 
     * @return Set of all names in the User table.
     */
    @Query("select u.name from User u")
    Set<String> findAllNames();

    /**
     * The findAllEmails method returns a Set of all emails in the User table.
     * 
     * @return Set of all emails in the User table.
     */
    @Query("select u.email from User u")
    Set<String> findAllEmails();

    /**
     * The findAllByRoleEquals method returns a List of all users with the given
     * 
     * @param role
     * @return List of all users with the given role.
     */
    List<User> findAllByRoleEquals(String role);
}