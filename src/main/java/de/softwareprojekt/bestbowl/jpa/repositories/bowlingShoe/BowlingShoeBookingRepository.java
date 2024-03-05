package de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowling_shoe.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.idclasses.BowlingShoeBookingId;

/**
 * The BowlingShoeBookingRepository is a Spring Data JPA repository for the
 * {@link BowlingShoeBooking} entities.
 * 
 * @author Max
 */
@Repository
public interface BowlingShoeBookingRepository extends JpaRepository<BowlingShoeBooking, BowlingShoeBookingId> {

    /**
     * The findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals method
     * returns all {@link BowlingShoeBooking}s by given {@link Client},
     * {@link BowlingAlley} and {@link Long} timeStamp.
     * 
     * @param client
     * @param bowlingAlley
     * @param timeStamp
     * @return List of {@link BowlingShoeBooking}s
     */
    List<BowlingShoeBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client,
            BowlingAlley bowlingAlley, long timeStamp);

    /**
     * The findAllByClientEquals method returns all {@link BowlingShoeBooking}s by
     * given {@link Client}.
     * 
     * @param client
     * @return List of {@link BowlingShoeBooking}s
     */
    List<BowlingShoeBooking> findAllByClientEquals(Client client);
}