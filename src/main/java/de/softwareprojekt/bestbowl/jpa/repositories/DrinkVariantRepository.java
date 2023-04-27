package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.DrinkVariant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Max Ziller
 */
public interface DrinkVariantRepository extends JpaRepository<DrinkVariant, Integer> {
}
