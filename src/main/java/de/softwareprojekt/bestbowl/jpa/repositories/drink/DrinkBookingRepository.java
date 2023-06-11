package de.softwareprojekt.bestbowl.jpa.repositories.drink;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;

/**
 * @author Ali
 */
@Repository
public interface DrinkBookingRepository extends JpaRepository<DrinkBooking, DrinkBookingId> {

    List<DrinkBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client,
            BowlingAlley bowlingAlley, long timeStamp);
    
    List<DrinkBooking> findAllByClientEquals(Client client);
}
