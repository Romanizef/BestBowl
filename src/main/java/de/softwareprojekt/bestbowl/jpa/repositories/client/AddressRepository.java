package de.softwareprojekt.bestbowl.jpa.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.softwareprojekt.bestbowl.jpa.entities.client.Address;

/**
 * The AddressRepository is a Spring Data JPA repository for the
 * {@link Address} entities.
 * 
 * @author Max
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}