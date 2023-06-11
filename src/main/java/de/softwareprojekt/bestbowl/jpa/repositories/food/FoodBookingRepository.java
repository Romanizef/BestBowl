package de.softwareprojekt.bestbowl.jpa.repositories.food;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.entities.food.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.FoodBookingId;

/**
 * The FoodBookingRepository is a Spring Data JPA repository for the
 * {@link FoodBooking} entities.
 * 
 * @author Ali
 */
@Repository
public interface FoodBookingRepository extends JpaRepository<FoodBooking, FoodBookingId> {

    /**
     * The findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals method
     * returns a list of all food bookings of the given client, bowling alley and
     * timestamp.
     * 
     * @param client
     * @param bowlingAlley
     * @param timeStamp
     * @return the list of food bookings
     */
    List<FoodBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client,
            BowlingAlley bowlingAlley, long timeStamp);

    /**
     * The findAllByClientEquals method returns all food bookings of the given
     * client.
     * 
     * @param client
     * @return the list of all food bookings of the given client
     */
    List<FoodBooking> findAllByClientEquals(Client client);

}