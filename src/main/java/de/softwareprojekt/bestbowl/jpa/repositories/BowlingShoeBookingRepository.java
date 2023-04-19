package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.idclasses.BowlingShoeBookingId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Marten Voß
 */
public interface BowlingShoeBookingRepository extends JpaRepository<BowlingShoeBooking, BowlingShoeBookingId> {
}
