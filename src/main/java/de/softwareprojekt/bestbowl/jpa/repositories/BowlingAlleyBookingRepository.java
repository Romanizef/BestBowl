package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Marten Voß
 */
@Repository
public interface BowlingAlleyBookingRepository extends JpaRepository<BowlingAlleyBooking, Integer> {

    @Query("from BowlingAlleyBooking bab, BowlingAlley ba where bab.bowlingAlley.id = ba.id and ba.active = true and bab.active = true and " +
            "(bab.startTime between :lowerBound and :upperBound " +
            "or bab.endTime between :lowerBound and :upperBound " +
            "or :lowerBound between bab.startTime and bab.endTime " +
            ") order by bab.bowlingAlley.id")
    List<BowlingAlleyBooking> findAllByTimePeriodsOverlapping(@Param("lowerBound") long lowerBound, @Param("upperBound") long upperBound);
}
