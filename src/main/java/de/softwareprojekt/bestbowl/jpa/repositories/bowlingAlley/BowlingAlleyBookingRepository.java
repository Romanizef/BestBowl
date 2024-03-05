package de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The BowlingAlleyBookingRepository is a Spring Data JPA repository for the
 * {@link BowlingAlleyBooking} entities.
 *
 * @author Matija
 * @author Ali Cicek
 */
@Repository
public interface BowlingAlleyBookingRepository extends JpaRepository<BowlingAlleyBooking, Integer> {

    /**
     * The findAllByTimePeriodsOverlapping method returns all
     * {@link BowlingAlleyBooking}s that are overlapping with the given time stamps:
     * lowerBound and upperBound
     *
     * @param lowerBound
     * @param upperBound
     * @return List of {@link BowlingAlleyBooking}s
     */
    @Query("from BowlingAlleyBooking bab, BowlingAlley ba where bab.bowlingAlley.id = ba.id and ba.active = true and bab.active = true and "
            +
            "(bab.startTime between :lowerBound and :upperBound " +
            "or bab.endTime between :lowerBound and :upperBound " +
            "or :lowerBound between bab.startTime and bab.endTime " +
            ") order by bab.bowlingAlley.id")
    List<BowlingAlleyBooking> findAllByTimePeriodsOverlapping(@Param("lowerBound") long lowerBound,
                                                              @Param("upperBound") long upperBound);

    /**
     * The findAllByTimePeriodsOverlapping method returns all
     * {@link BowlingAlleyBooking}s that are overlapping with the given time stamp:
     * currentTime.
     *
     * @param currentTime
     * @return List of {@link BowlingAlleyBooking}s
     */
    @Query("FROM BowlingAlleyBooking bab, BowlingAlley ba " +
            "WHERE bab.bowlingAlley.id = ba.id " +
            "AND ba.active = true " +
            "AND bab.active = true " +
            "AND :currentTime BETWEEN bab.startTime AND bab.endTime " +
            "ORDER BY bab.bowlingAlley.id")
    List<BowlingAlleyBooking> findAllByTimePeriodsOverlapping(@Param("currentTime") long currentTime);

    /**
     * The findAllByClientEquals method returns all {@link BowlingAlleyBooking}s by
     * given client.
     *
     * @param client
     * @return List of {@link BowlingAlleyBooking}s
     */
    List<BowlingAlleyBooking> findAllByClientEquals(Client client);

    @Query("from BowlingAlleyBooking bab where bab.active = true and bab.completed = false and bab.startTime > :currentTime")
    List<BowlingAlleyBooking> findAllUpcomingBookings(@Param("currentTime") long currentTime);

    @Query("from BowlingAlleyBooking bab where bab.active = true and bab.completed = false and " +
            ":currentTime between bab.startTime and bab.endTime")
    List<BowlingAlleyBooking> findAllCurrentBookings(@Param("currentTime") long currentTime);

    @Query("from BowlingAlleyBooking bab where bab.active = true and bab.completed = false and bab.endTime < :currentTime")
    List<BowlingAlleyBooking> findAllOverdueBookings(@Param("currentTime") long currentTime);

    /**
     * The findAllByClientEqualsAndStartTimeBetween method returns
     * all {@link BowlingAlleyBooking}s where the startTime is between the given
     * lowerBound and upperBound and the client equals the given client.
     *
     * @param lowerBound
     * @param upperBound
     * @param client
     * @return List of {@link BowlingAlleyBooking}s
     */
    List<BowlingAlleyBooking> findAllByStartTimeBetweenAndClientEquals(long lowerBound,
                                                                       long upperBound, Client client);
}