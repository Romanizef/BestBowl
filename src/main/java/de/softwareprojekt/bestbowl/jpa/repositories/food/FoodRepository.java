package de.softwareprojekt.bestbowl.jpa.repositories.food;

import de.softwareprojekt.bestbowl.jpa.entities.food.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * The FoodRepository is a Spring Data JPA repository for the {@link Food}
 * entities.
 *
 * @author Ali
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    /**
     * The findAllNames method returns a Set of all names of foods.
     *
     * @return the names of all foods
     */
    @Query("select f.name from Food f")
    Set<String> findAllNames();

    /**
     * The findAllByReorderThresholdReached method returns a List of all foods which
     * are active and have a reorder point higher than stock.
     *
     * @return list of foods
     */
    @Query("from Food f where f.active = true and f.reorderPoint > f.stock")
    List<Food> findAllByReorderThresholdReached();

    /**
     * The findByName method returns food with the given name.
     *
     * @param name
     * @return Food
     */
    Food findByName(String name);

    /**
     * The updateStockById method updates the stock of food with the given id.
     *
     * @param id
     * @param stock
     */
    @Transactional
    @Modifying
    @Query("update Food f set f.stock = :stock where f.id = :foodId")
    void updateStockById(@Param("foodId") int id, @Param("stock") int stock);
}