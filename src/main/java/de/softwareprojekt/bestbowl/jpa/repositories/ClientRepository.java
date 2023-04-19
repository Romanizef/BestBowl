package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Marten Voß
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findFirstByOrderById();
}
