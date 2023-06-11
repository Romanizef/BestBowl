package de.softwareprojekt.bestbowl.utils.checkers;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * DuplicateChecker checks for duplicate values.
 * 
 * @author Marten Voß
 */
public class DuplicateChecker {
    private final Set<String> existingValues;

    /**
     * The DuplicateChecker function is used to check if a value already exists in
     * the database.
     * If it does, then it will return an Optional with the existing value.
     * If not, then it will add the new value to the database and return an empty
     * Optional.
     * 
     * @param existingValues Store the existing values in a set
     *
     * @return The existingvalues
     */
    public DuplicateChecker(Set<String> existingValues) {
        this.existingValues = existingValues;
    }

    /**
     * Tries up to 20 times to generate a new unique value
     *
     * @param stringSupplier source of new values
     * @return the optional result
     */
    public Optional<String> generateNewValue(Supplier<String> stringSupplier) {
        int counter = 0;
        while (counter < 20) {
            String newValue = stringSupplier.get();
            if (!existingValues.contains(newValue)) {
                existingValues.add(newValue);
                return Optional.of(newValue);
            }
            counter++;
        }
        return Optional.empty();
    }
}