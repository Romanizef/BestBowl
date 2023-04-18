package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.Association;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssociationRepository extends JpaRepository<Association, Integer> {
    Optional<Association> findFirstByOrderById();
}
