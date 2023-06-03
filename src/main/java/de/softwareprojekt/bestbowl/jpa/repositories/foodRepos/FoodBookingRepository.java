package de.softwareprojekt.bestbowl.jpa.repositories.foodRepos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlleyEntities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.clientEntities.Client;
import de.softwareprojekt.bestbowl.jpa.entities.foodEntities.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.FoodBookingId;

/**
 * @author Marten Voß
 */
@Repository
public interface FoodBookingRepository extends JpaRepository<FoodBooking, FoodBookingId> {

    List<FoodBooking> findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(Client client, BowlingAlley bowlingAlley, long timeStamp);

    List<FoodBooking> findAllByClientEquals(Client client);

}
