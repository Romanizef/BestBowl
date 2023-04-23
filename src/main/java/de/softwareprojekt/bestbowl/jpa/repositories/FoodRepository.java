package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 * @author Marten Voß
 */
public interface FoodRepository extends JpaRepository<Food, Integer> {
    Optional<Food> findByName(String name);
}
