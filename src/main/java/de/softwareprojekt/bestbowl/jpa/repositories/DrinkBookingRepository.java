package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Marten Voß
 */
public interface DrinkBookingRepository extends JpaRepository<DrinkBooking, DrinkBookingId> {
    List<DrinkBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client, BowlingAlley bowlingAlley, long timeStamp);
}
