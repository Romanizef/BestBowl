package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Marten Voß
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    @Query("select f.name from Food f")
    Set<String> findAllNames();


}
