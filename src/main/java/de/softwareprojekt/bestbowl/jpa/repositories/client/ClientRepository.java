package de.softwareprojekt.bestbowl.jpa.repositories.client;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.client.Client;

/**
 * The ClientRepository is a Spring Data JPA repository for the
 * {@link Client} entities.
 * 
 * @author Ali
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * The findAllByActiveEqualsOrderByLastName method returns a list of all clients
     * by given boolean value active.
     * 
     * @param active
     * @return List of Clients
     */
    List<Client> findAllByActiveEqualsOrderByLastName(boolean active);

    /**
     * The findAllByActiveEqualsOrderByLastName method returns a list of all client
     * emails.
     * 
     * @return Set of Strings
     */
    @Query("select c.email from Client c")
    Set<String> findAllEmails();
}