package de.softwareprojekt.bestbowl.jpa.repositories.drinkRepos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.clientEntities.Client;
import de.softwareprojekt.bestbowl.jpa.entities.drinkEntities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;

/**
 * @author Marten Voß
 */
@Repository
public interface DrinkBookingRepository extends JpaRepository<DrinkBooking, DrinkBookingId> {

    List<DrinkBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client,
            BowlingAlley bowlingAlley, long timeStamp);
    
    List<DrinkBooking> findAllByClientEquals(Client client);
}
