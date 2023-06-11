package de.softwareprojekt.bestbowl.jpa.repositories.drink;

import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Ali
 */
@Repository
public interface DrinkRepository extends JpaRepository<Drink, Integer> {

    @Query("select d.name from Drink d")
    Set<String> findAllNames();

    @Query("from Drink d where d.active = true and d.reorderPoint > d.stockInMilliliters")
    List<Drink> findAllByReorderThresholdReached();

    Drink findByName(String name);
}
