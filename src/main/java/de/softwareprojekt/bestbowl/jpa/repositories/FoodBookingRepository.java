package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.FoodBookingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodBookingRepository extends JpaRepository<FoodBooking, FoodBookingId> {
}
