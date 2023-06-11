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
 * @author Ali
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    @Query("select f.name from Food f")
    Set<String> findAllNames();

    @Query("from Food f where f.active = true and f.reorderPoint > f.stock")
    List<Food> findAllByReorderThresholdReached();

    Food findByName(String name);

    @Transactional
    @Modifying
    @Query("update Food f set f.stock = :stock where f.id = :foodId")
    void updateStockById(@Param("foodId") int id, @Param("stock") int stock);
}
