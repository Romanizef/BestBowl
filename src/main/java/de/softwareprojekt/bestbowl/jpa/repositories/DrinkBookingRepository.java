package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.DrinkBookingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrinkBookingRepository extends JpaRepository<DrinkBooking, DrinkBookingId> {
}
