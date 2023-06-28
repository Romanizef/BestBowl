package de.softwareprojekt.bestbowl.jpa.repositories.drink;

import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * The DrinkRepository is a Spring Data JPA repository for the
 * {@link Drink} entities.
 *
 * @author Ali
 */
@Repository
public interface DrinkRepository extends JpaRepository<Drink, Integer> {

    /**
     * The findAllNames method finds all Drink entities and returns their names.
     *
     * @return Set of Drinks
     */
    @Query("select d.name from Drink d")
    Set<String> findAllNames();

    /**
     * The findAllByReorderThresholdReached method finds all Drink entities that are
     * active and have a reorderPoint higher than the stockInMilliliters.
     *
     * @return List of Drinks
     */
    @Query("from Drink d where d.active = true and d.reorderPoint > d.stockInMilliliters")
    List<Drink> findAllByReorderThresholdReached();


    List<Drink> findAllByActiveEquals(boolean active);

    /**
     * The findByName method find all Drink entities with the given name
     *
     * @param name
     * @return Drink entity with the given name
     */
    Drink findByName(String name);
}