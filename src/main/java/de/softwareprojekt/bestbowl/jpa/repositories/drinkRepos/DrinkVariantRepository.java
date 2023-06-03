package de.softwareprojekt.bestbowl.jpa.repositories.drinkRepos;

import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.DrinkVariant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Max Ziller
 */
@Repository
public interface DrinkVariantRepository extends JpaRepository<DrinkVariant, Integer> {

    @Query("select dv.ml from DrinkVariant dv where dv.drink = :drink")
    Set<Integer> findAllMlForDrink(@Param("drink") Drink drink);
}
