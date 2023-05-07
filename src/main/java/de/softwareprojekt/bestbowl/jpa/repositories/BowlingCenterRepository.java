package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Marten Voß
 */
@Repository
public interface BowlingCenterRepository extends JpaRepository<BowlingCenter, Integer> {
    @Query("from BowlingCenter bc where bc.id = 1")
    BowlingCenter getBowlingCenter();
}
