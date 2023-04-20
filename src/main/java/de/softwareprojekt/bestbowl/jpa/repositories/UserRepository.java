package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Marten Voß
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);
}
