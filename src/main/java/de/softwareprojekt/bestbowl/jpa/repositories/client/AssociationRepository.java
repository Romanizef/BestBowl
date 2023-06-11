package de.softwareprojekt.bestbowl.jpa.repositories.client;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.client.Association;

/**
 * The AssociationRepository is a Spring Data JPA repository for the
 * {@link Association} entities.
 * 
 * @author Ali
 */
@Repository
public interface AssociationRepository extends JpaRepository<Association, Integer> {

    /**
     * The findAllNames method returns a Set of all association names.
     * 
     * @return Set of Strings
     */
    @Query("select a.name from Association a")
    Set<String> findAllNames();
}