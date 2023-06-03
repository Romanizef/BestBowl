package de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoeRepos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoeEntities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.clientEntities.Client;
import de.softwareprojekt.bestbowl.jpa.idclasses.BowlingShoeBookingId;

/**
 * @author Marten Voß
 */
@Repository
public interface BowlingShoeBookingRepository extends JpaRepository<BowlingShoeBooking, BowlingShoeBookingId> {

    List<BowlingShoeBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client, BowlingAlley bowlingAlley, long timeStamp);
    List<BowlingShoeBooking> findAllByClientEquals(Client client);

    
}
