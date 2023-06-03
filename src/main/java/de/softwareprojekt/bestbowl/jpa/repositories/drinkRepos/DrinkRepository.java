package de.softwareprojekt.bestbowl.jpa.repositories.drinkRepos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.Drink;

import java.util.Set;

/**
 * @author Marten Voß
 */
@Repository
public interface DrinkRepository extends JpaRepository<Drink, Integer> {

    @Query("select d.name from Drink d")
    Set<String> findAllNames();

    Drink findByName(String name);
}
