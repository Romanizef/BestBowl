package de.softwareprojekt.bestbowl.jpa.repositories.drinkRepos;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.clientEntities.Client;
import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Marten Voß
 */
@Repository
public interface DrinkBookingRepository extends JpaRepository<DrinkBooking, DrinkBookingId> {

    List<DrinkBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client,
            BowlingAlley bowlingAlley, long timeStamp);
    
    List<DrinkBooking> findAllByClientEquals(Client client);
}
