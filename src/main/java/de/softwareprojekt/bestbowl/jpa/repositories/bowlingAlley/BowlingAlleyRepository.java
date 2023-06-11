package de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;

/**
 * The BowlingAlleyRepository is a Spring Data JPA repository for the
 * {@link BowlingAlley} entities.
 * 
 * @author Matija
 */
@Repository
public interface BowlingAlleyRepository extends JpaRepository<BowlingAlley, Integer> {

    /**
     * The findAllByNoBookingOverlapBetweenTimeStamps method returns a list of all
     * {@link BowlingAlley}s that do not overlap with any booking between the given
     * time stamps: lowerBound and upperBound.
     * 
     * @param lowerBound
     * @param upperBound
     * @return List of {@link BowlingAlley}s
     */
    @Query("select ba from BowlingAlley ba where ba.active = true and ba.id not in (" +
            "select distinct bab.bowlingAlley.id from BowlingAlleyBooking bab, BowlingAlley ba " +
            "where ba.active = true and ba.id = bab.bowlingAlley.id and bab.active = true and " +
            "(bab.startTime between :lowerBound and :upperBound " +
            "or bab.endTime between :lowerBound and :upperBound " +
            "or :lowerBound between bab.startTime and bab.endTime)" +
            ") order by ba.id")
    List<BowlingAlley> findAllByNoBookingOverlapBetweenTimeStamps(@Param("lowerBound") long lowerBound,
            @Param("upperBound") long upperBound);
}