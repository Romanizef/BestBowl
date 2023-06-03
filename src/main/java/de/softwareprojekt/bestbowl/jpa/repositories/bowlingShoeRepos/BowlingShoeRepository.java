package de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoeRepos;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoeEntities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.clientEntities.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Marten Voß
 */
@Repository
public interface BowlingShoeRepository extends JpaRepository<BowlingShoe, Integer> {

    List<BowlingShoe> findAllBySizeEqualsAndActiveIsTrueAndClientIsNull(int size);
    List<BowlingShoe> findAllByClientIsNullAndActiveIsTrue();
    List<BowlingShoe> findAllByClientEqualsAndActiveIsTrue(Client client);



    @Transactional
    @Modifying
    @Query("update BowlingShoe bs set bs.client = :client where bs.id = :shoeId")
    void updateClientById(@Param("shoeId") int id, @Param("client") Client client );
}
