package de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_shoe.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The BowlingShoeRepository is a Spring Data JPA repository for the
 * {@link BowlingShoe} entities.
 *
 * @author Max
 */
@Repository
public interface BowlingShoeRepository extends JpaRepository<BowlingShoe, Integer> {

    /**
     * The findAllByActiveIsTrueAndClientIsNull method returns the first active bowling
     * shoes by given size and without a client.
     *
     * @param size
     * @return List of BowlingShoes
     */
    Optional<BowlingShoe> findFirstBySizeEqualsAndActiveIsTrueAndClientIsNull(int size);

    /**
     * The findAllByActiveIsTrueAndClientIsNull method returns all active bowling
     * shoes without a client.
     *
     * @return List of BowlingShoes
     */
    List<BowlingShoe> findAllByClientIsNullAndActiveIsTrue();

    /**
     * The findAllByActiveIsTrueAndClientIsNull method updates the client of a
     * bowling shoe by given id and client.
     *
     * @param id
     * @param client
     */
    @Transactional
    @Modifying
    @Query("update BowlingShoe bs set bs.client = :client where bs.id = :shoeId")
    void updateClientById(@Param("shoeId") int id, @Param("client") Client client);
}