package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Marten Voß
 */
@Repository
public interface BowlingShoeRepository extends JpaRepository<BowlingShoe, Integer> {
}
