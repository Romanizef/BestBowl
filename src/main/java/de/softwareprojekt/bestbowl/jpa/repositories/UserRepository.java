package de.softwareprojekt.bestbowl.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Ali
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByName(String name);

    @Query("select u.name from User u")
    Set<String> findAllNames();

    @Query("select u.email from User u")
    Set<String> findAllEmails();

    List<User> findAllByRoleEquals(String role);
}
