package de.softwareprojekt.bestbowl.jpa.repositories.food;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.entities.food.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.FoodBookingId;

/**
 * @author Marten Voß
 */
@Repository
public interface FoodBookingRepository extends JpaRepository<FoodBooking, FoodBookingId> {

    List<FoodBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client, BowlingAlley bowlingAlley, long timeStamp);

    List<FoodBooking> findAllByClientEquals(Client client);

}
