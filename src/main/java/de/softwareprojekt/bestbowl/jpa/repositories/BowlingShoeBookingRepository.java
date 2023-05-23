package de.softwareprojekt.bestbowl.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.idclasses.BowlingShoeBookingId;

/**
 * @author Marten Voß
 */
@Repository
public interface BowlingShoeBookingRepository extends JpaRepository<BowlingShoeBooking, BowlingShoeBookingId> {

    List<BowlingShoeBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client, BowlingAlley bowlingAlley, long timeStamp);
    List<BowlingShoeBooking> findAllByClientEquals(Client client);

    @Query("from BowlingShoeBooking bsb where bsb.client = :bab.client and bsb.bowlingAlley = :bab.bowlingAlley and bsb.timeStamp = :bab.timeStamp")
    List<BowlingShoeBooking> findByKey(@Param("bab")BowlingAlleyBooking bab);
    
}
