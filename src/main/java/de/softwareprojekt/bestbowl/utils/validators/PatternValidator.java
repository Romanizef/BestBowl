package de.softwareprojekt.bestbowl.utils.validators;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringNotEmpty;

import java.util.regex.Pattern;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;

/**
 * 
 * @author Matija
 */
public class PatternValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^([0]{1}[1-9]{1}|[1-9]{1}[0-9]{1})[0-9]{3}$");
    private static final Pattern ONLY_LETTERS_PATTERN = Pattern.compile("^[a-zA-ZäüößÄÜÖ]+$");
    private static final Pattern ONLY_LETTERS_AND_CHAR_PATTERN = Pattern.compile("^[a-zA-ZäüößÄÜÖ/.-]+$");
    private static final Pattern NO_DOUBLESPACES_PATTERN = Pattern.compile("^[\s\s]+$");

    private PatternValidator() {
    }

    /**
     * @param s string to be checked
     * @return if the string is a valid email address
     */
    public static boolean isStringValidEmail(String s) {
        if (isStringNotEmpty(s)) {
            return EMAIL_PATTERN.matcher(s).matches();
        }
        return false;
    }

    /**
     * The isStringOnlyLetters function checks if a given string contains only
     * letters.
     * 
     * @param s Check if the string is empty or not
     *
     * @return True if the string contains only letters
     */
    public static boolean isStringOnlyLetters(String s) {
        if (isStringNotEmpty(s)) {
            return ONLY_LETTERS_PATTERN.matcher(s).matches();
        }
        return false;
    }

    /**
     * The isStringOnlyLettersAndSpecialChars function checks if a string contains
     * only letters and special characters.
     * 
     * @param s Check if the string is empty or not
     *
     * @return True if the string contains only letters and special characters
     */
    public static boolean isStringOnlyLettersAndSpecialChars(String s) {
        if (isStringNotEmpty(s)) {
            return ONLY_LETTERS_AND_CHAR_PATTERN.matcher(s).matches();
        }
        return false;
    }

    /**
     * The isStringWithoutDoubleSpaces function checks if a string contains double
     * spaces.
     * 
     * @param s Check if the string is empty or not
     *
     * @return True if the string does not contain double spaces
     */
    public static boolean isStringWithoutDoubleSpaces(String s) {
        if (isStringNotEmpty(s)) {
            return NO_DOUBLESPACES_PATTERN.matcher(s).matches();
        }
        return false;
    }

    /**
     * The isValid function checks if the given postal code matches the pattern for
     * german postal codes.
     * 
     * @param pattern
     * @param postalCode
     *
     * @return A boolean value
     */
    public static boolean isStringValidPostalcode(String postalCode) {
        if (isStringNotEmpty(postalCode)) {
            return POSTAL_CODE_PATTERN.matcher(postalCode).matches();
        }
        return false;
    }

    /**
     * The createStringPostalcode function takes a Client object as input and
     * returns the postal code of that client.
     * If the postal code is only 4 digits long, it adds a 0 to the beginning of it.
     * 
     * @param client
     *
     * @return A string with the postal code
     */
    public static String createStringPostalcodeForClient(Client client) {
        String postalCode = "" + client.getAddress().getPostCode();
        if (postalCode.length() == 4)
            postalCode = "0" + postalCode;
        return postalCode;
    }

    /**
     * The createStringPostalcodeForBowlingcenter function takes a BowlingCenter
     * object as input and returns the postal code of that bowling center.
     * If the postal code is only 4 digits long, it adds a leading zero to make it 5
     * digits long.
     * 
     * @param BowlingCenter bowlingcenter Get the postal code of a bowling center
     *
     * @return A string with the postalcode of a bowlingcenter
     */
    public static String createStringPostalcodeForBowlingcenter(BowlingCenter bowlingcenter) {
        String postalCode = "" + bowlingcenter.getPostCode();
        if (postalCode.length() == 4)
            postalCode = "0" + postalCode;
        return postalCode;
    }
}