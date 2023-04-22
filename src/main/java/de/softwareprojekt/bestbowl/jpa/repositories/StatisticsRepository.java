package de.softwareprojekt.bestbowl.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.softwareprojekt.bestbowl.jpa.entities.Statistic;

/**
 * @author Matija Kopschek
 */
public interface StatisticsRepository extends JpaRepository<Statistic, Integer>{
    List<Statistic> findAllByActiveEquals(boolean active);

    @Query("from Statistic s where (cast(s.id as string) like %:s% or s.cID ilike %:s% or s.cLastName ilike %:s% or s.date ilike %:s% or s.total ilike %:s%) and s.active = :active order by s.cLastName")
    List<Statistic> findAllByAnyFieldContainingStringAndActive(@Param("s") String s, @Param("active") boolean active);

}
