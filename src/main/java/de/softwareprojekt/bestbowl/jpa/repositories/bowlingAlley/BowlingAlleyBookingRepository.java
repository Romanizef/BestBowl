package de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Marten Voß
 * @author Ali Cicek
 */
@Repository
public interface BowlingAlleyBookingRepository extends JpaRepository<BowlingAlleyBooking, Integer> {

    @Query("from BowlingAlleyBooking bab, BowlingAlley ba where bab.bowlingAlley.id = ba.id and ba.active = true and bab.active = true and " +
            "(bab.startTime between :lowerBound and :upperBound " +
            "or bab.endTime between :lowerBound and :upperBound " +
            "or :lowerBound between bab.startTime and bab.endTime " +
            ") order by bab.bowlingAlley.id")
    List<BowlingAlleyBooking> findAllByTimePeriodsOverlapping(@Param("lowerBound") long lowerBound, @Param("upperBound") long upperBound);

    @Query("FROM BowlingAlleyBooking bab, BowlingAlley ba " +
            "WHERE bab.bowlingAlley.id = ba.id " +
            "AND ba.active = true " +
            "AND bab.active = true " +
            "AND :currentTime BETWEEN bab.startTime AND bab.endTime " +
            "ORDER BY bab.bowlingAlley.id")
    List<BowlingAlleyBooking> findAllByTimePeriodsOverlapping(@Param("currentTime") long currentTime);

    List<BowlingAlleyBooking> findAllByClientEquals(Client client);

    List<BowlingAlleyBooking> findAllByEndTimeLessThanAndCompletedEquals(long endTime, boolean completed);

    List<BowlingAlleyBooking> findAllByStartTimeBetweenOrderByStartTime(long lowerBound, long upperBound);
}
