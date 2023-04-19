package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Marten Voß
 */
public interface BowlingAlleyRepository extends JpaRepository<BowlingAlley, Integer> {
}
