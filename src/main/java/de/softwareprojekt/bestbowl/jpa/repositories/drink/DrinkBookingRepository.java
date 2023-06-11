package de.softwareprojekt.bestbowl.jpa.repositories.drink;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;

/**
 * The DrinkBookingRepository is a Spring Data JPA repository for the
 * {@link DrinkBooking} entities.
 * 
 * @author Ali
 */
@Repository
public interface DrinkBookingRepository extends JpaRepository<DrinkBooking, DrinkBookingId> {

    /**
     * The findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals method finds
     * all DrinkBookings by the given client, bowlingAlley and timeStamp.
     * 
     * @param client
     * @param bowlingAlley
     * @param timeStamp
     * @return List of DrinkBookings
     */
    List<DrinkBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client,
            BowlingAlley bowlingAlley, long timeStamp);

    /**
     * The findAllByClientEquals method finds all DrinkBookings by the given client.
     * 
     * @param client
     * @return List of DrinkBookings
     */
    List<DrinkBooking> findAllByClientEquals(Client client);
}