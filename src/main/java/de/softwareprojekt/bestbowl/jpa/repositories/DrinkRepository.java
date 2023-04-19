package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Marten Voß
 */
public interface DrinkRepository extends JpaRepository<Drink, Integer> {
}
