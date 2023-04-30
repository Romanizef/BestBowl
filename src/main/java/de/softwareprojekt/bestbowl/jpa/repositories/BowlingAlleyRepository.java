package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Marten Voß
 */
public interface BowlingAlleyRepository extends JpaRepository<BowlingAlley, Integer> {

    @Query("select ba from BowlingAlley ba where ba.active = true and ba.id not in (" +
            "select distinct bab.bowlingAlley.id from BowlingAlleyBooking bab, BowlingAlley ba " +
            "where ba.active = true and ba.id = bab.bowlingAlley.id and bab.active = true and " +
            "(bab.startTime between :lowerBound and :upperBound " +
            "or bab.endTime between :lowerBound and :upperBound " +
            "or :lowerBound between bab.startTime and bab.endTime)" +
            ") order by ba.id")
    List<BowlingAlley> findAllByNoBookingOverlapBetweenTimeStamps(@Param("lowerBound") long lowerBound, @Param("upperBound") long upperBound);
}
