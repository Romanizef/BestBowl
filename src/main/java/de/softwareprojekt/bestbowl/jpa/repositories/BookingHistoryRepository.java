package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Marten Voß
 */
@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, Integer> {
}
