package de.softwareprojekt.bestbowl.jpa.repositories.drink;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;

/**
 * The DrinkVariantRepository is a Spring Data JPA repository for the
 * {@link DrinkVariant} entities.
 * 
 * @author Ali
 */
@Repository
public interface DrinkVariantRepository extends JpaRepository<DrinkVariant, Integer> {

    /**
     * The findAllMlForDrink method returns all the ml values for a given drink.
     * 
     * @param drink
     * @return Set of Integers
     */
    @Query("select dv.ml from DrinkVariant dv where dv.drink = :drink")
    Set<Integer> findAllMlForDrink(@Param("drink") Drink drink);
}