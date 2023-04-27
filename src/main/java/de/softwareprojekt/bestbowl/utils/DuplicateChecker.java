package de.softwareprojekt.bestbowl.utils;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Marten Voß
 */
public class DuplicateChecker {
    private final Set<String> existingValues;

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
