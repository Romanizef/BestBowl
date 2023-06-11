package de.softwareprojekt.bestbowl.jpa.repositories.drink;

import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Ali
 */
@Repository
public interface DrinkVariantRepository extends JpaRepository<DrinkVariant, Integer> {

    @Query("select dv.ml from DrinkVariant dv where dv.drink = :drink")
    Set<Integer> findAllMlForDrink(@Param("drink") Drink drink);
}
