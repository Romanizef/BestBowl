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
    private static final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("^[1-9]\\d*(?:[ -]?(?:[a-zA-Z]+|[1-9]\\d*))?$");
    private static final Pattern SMTP_HOST_PATTERN = Pattern
            .compile("^[\\d.a-z-]+\\.[a-z]{2,63}$");
    private static final Pattern SMTP_PORT_PATTERN = Pattern
            .compile("^[1-9]{3}+$");

    private PatternValidator() {
    }

    /**
     * The isStringValidSMTPPort function checks if the given String is a valid SMTP
     * port.
     * 
     * @param s Check if the string is empty or not
     *
     * @return A boolean value
     */
    public static boolean isStringValidSMTPPort(String s) {
        if (isStringNotEmpty(s)) {
            return SMTP_PORT_PATTERN.matcher(s).matches();
        }
        return false;
    }

    /**
     * The isStringValidSMTPHost function checks if a given string is a valid SMTP
     * host.
     * 
     * @param s Check if the string is empty or not
     *
     * @return True if the string is not empty and matches the smtp_host_pattern
     */
    public static boolean isStringValidSMTPHost(String s) {
        if (isStringNotEmpty(s)) {
            return SMTP_HOST_PATTERN.matcher(s).matches();
        }
        return false;
    }

    /**
     * The isStringValidHouseNumber function checks if a given string is a valid
     * house number.
     * A valid house number consists of at least one digit and can be followed by an
     * optional letter.
     * 
     * @param s Check if the string is empty or not
     *
     * @return A boolean value
     */
    public static boolean isStringValidHouseNumber(String s) {
        if (isStringNotEmpty(s)) {
            return HOUSE_NUMBER_PATTERN.matcher(s).matches();
        }
        return false;
    }

    /**
     * The isStringValidEmail function checks if a given string is a valid email
     * address.
     * 
     * @param s Check if the string is empty or not
     *
     * @return True if the string is not empty and matches the email_pattern
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
     * @param bowlingcenter Get the postal code of a bowling center
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