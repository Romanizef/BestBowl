package de.softwareprojekt.bestbowl.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;

/**
 * @author Ali
 */
@Repository
public interface BowlingCenterRepository extends JpaRepository<BowlingCenter, Integer> {
    @Query("from BowlingCenter bc where bc.id = 1")
    BowlingCenter getBowlingCenter();
}
