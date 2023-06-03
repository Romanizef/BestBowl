package de.softwareprojekt.bestbowl.jpa.repositories.ClientRepos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.clientEntities.Address;

/**
 * @author Marten Voß
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}
