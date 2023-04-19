package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Marten Voß
 */
public interface BowlingAlleyBookingRepository extends JpaRepository<BowlingAlleyBooking, Integer> {
}
