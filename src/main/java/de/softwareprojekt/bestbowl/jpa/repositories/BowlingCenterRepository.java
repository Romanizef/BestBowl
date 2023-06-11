package de.softwareprojekt.bestbowl.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;

/**
 * The BowlingCenterRepository is a Spring Data JPA repository for the {@link BowlingCenter} entities.
 * 
 * @author Ali
 */
@Repository
public interface BowlingCenterRepository extends JpaRepository<BowlingCenter, Integer> {

    /**
     * The getBowlingCenter method returns the bowling center.
     * 
     * @return the bowling center
     */
    @Query("from BowlingCenter bc where bc.id = 1")
    BowlingCenter getBowlingCenter();
}