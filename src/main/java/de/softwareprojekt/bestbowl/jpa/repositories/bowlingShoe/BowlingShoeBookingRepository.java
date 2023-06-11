package de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.idclasses.BowlingShoeBookingId;

/**
 * @author Max
 */
@Repository
public interface BowlingShoeBookingRepository extends JpaRepository<BowlingShoeBooking, BowlingShoeBookingId> {

    List<BowlingShoeBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client, BowlingAlley bowlingAlley, long timeStamp);
    List<BowlingShoeBooking> findAllByClientEquals(Client client);

    
}
