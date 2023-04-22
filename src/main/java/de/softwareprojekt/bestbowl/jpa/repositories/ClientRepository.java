package de.softwareprojekt.bestbowl.jpa.repositories;

import de.softwareprojekt.bestbowl.jpa.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Marten Voß
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    List<Client> findAllByActiveEqualsOrderByLastName(boolean active);

    @Query("from Client c where (cast(c.id as string) like %:s% or c.firstName ilike %:s% or c.lastName ilike %:s% or c.email ilike %:s%) and c.active = :active order by c.lastName")
    List<Client> findAllByAnyFieldContainingStringAndActive(@Param("s") String s, @Param("active") boolean active);
}
