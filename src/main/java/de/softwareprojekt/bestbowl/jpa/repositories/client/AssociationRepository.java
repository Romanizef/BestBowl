package de.softwareprojekt.bestbowl.jpa.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.client.Association;

import java.util.Set;

/**
 * @author Marten Voß
 */
@Repository
public interface AssociationRepository extends JpaRepository<Association, Integer> {

    @Query("select a.name from Association a")
    Set<String> findAllNames();
}
