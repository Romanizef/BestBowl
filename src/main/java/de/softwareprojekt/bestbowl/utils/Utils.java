package de.softwareprojekt.bestbowl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Marten Voß
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static final File WORKING_DIR_ROOT = new File("");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            Pattern.CASE_INSENSITIVE);

    private Utils() {
    }

    /**
     * Util method to start a new thread as a 1-liner
     *
     * @param runnable code to be executed
     * @param name     thread name
     * @param daemon   mark the thread as daemon
     */
    public static void startThread(Runnable runnable, String name, boolean daemon) {
        Thread thread = new Thread(runnable);
        thread.setName(name);
        thread.setDaemon(daemon);
        thread.start();
    }

    /**
     * Checks if the given File is a directory and if not attempts to create it
     *
     * @param dir directory to be checked/created
     * @return if the directory was created or is present
     */
    public static boolean createDirectoryIfMissing(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            if (dir.mkdirs()) {
                return true;
            } else {
                LOGGER.error("error creating directory: " + dir.getName());
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * @return the absolute path of the current working directory
     */
    public static String getWorkingDirPath() {
        return WORKING_DIR_ROOT.getAbsolutePath();
    }

    /**
     * Checks if the given String is not empty
     *
     * @param s string to be checked
     * @return if the string is not null or empty
     */
    public static boolean isStringNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    /**
     * Checks if all the given strings are not empty
     *
     * @param strings array of strings
     * @return if all strings are not null or empty value
     */
    public static boolean isStringNotEmpty(String... strings) {
        for (String s : strings) {
            if (!isStringNotEmpty(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param s         string to be checked
     * @param minLength minimum length of the string
     * @return if s != null and length >= minLength
     */
    public static boolean isStringMinNChars(String s, int minLength) {
        return s != null && s.length() >= minLength;
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
     * Checks if the search term is contained in the value
     *
     * @param value      string to be searched in
     * @param searchTerm containing string
     * @return true if the search term is empty or contained in the value
     */
    public static boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    /**
     * Checks if the value contains any of the search terms and if so removes that
     * search term from the list
     *
     * @param value       String that is checked
     * @param searchTerms list of search terms
     */
    public static void matchAndRemoveIfContains(String value, List<String> searchTerms) {
        if (!searchTerms.isEmpty()) {
            Iterator<String> searchTermIterator = searchTerms.iterator();
            while (searchTermIterator.hasNext()) {
                String searchTerm = searchTermIterator.next();
                if (matches(value, searchTerm)) {
                    searchTermIterator.remove();
                    return;
                }
            }
        }
    }

    /**
     * Formats a double to 2 decimal places with german locale
     *
     * @param d double to be formatted
     * @return the formatted string
     */
    public static String formatDouble(double d) {
        return String.format(Locale.GERMANY, "%.2f", d);
    }

    /**
     * @param ms unix timestamp
     * @return date string with day, month, year, hour and minute
     */
    public static String toDateString(Long ms) {
        return toDateString(ms, "dd.MM.yyyy HH:mm");
    }

    /**
     * @param ms unix timestamp
     * @return date string with day, month, year
     */
    public static String toDateOnlyString(Long ms) {
        return toDateString(ms, "dd.MM.yyyy");
    }

    /**
     * @param ms     unix timestamp
     * @param format format of the date parsable by SimpleDateFormat
     * @return formatted date string
     */
    private static String toDateString(Long ms, String format) {
        if (ms == null) {
            return "-";
        }
        try {
            return new SimpleDateFormat(format).format(new Date(ms));
        } catch (NumberFormatException ex) {
            return "NaN";
        }
    }

    /**
     * @param durationInMs duration in ms
     * @return hours rounded to 2 decimal places
     */
    public static String toHoursString(long durationInMs) {
        return Utils.formatDouble(java.time.Duration.ofMillis(durationInMs).toMinutes() / (double) 60) + " Std.";
    }

    /**
     * @return a LocalDateTime of the current time with the minute rounded to the
     *         next 1/4 hour
     */
    public static LocalDateTime getCurrentDateTimeRounded() {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);
        int newMinute = localDateTime.getMinute() + (75 - localDateTime.getMinute()) % 15;
        if (newMinute == 60) {
            return localDateTime.withMinute(0).plusHours(1);
        } else {
            return localDateTime.withMinute(newMinute);
        }
    }

    /**
     * @return a LocalDateTime of the current date with hour set to 5
     */
    public static LocalDateTime getCurrentDateTimeOfDayStart() {
        return setLocalDateTimeToDayStart(LocalDateTime.now());
    }

    /**
     * @param localDateTime LocalDateTime to be changed
     * @return the LocalDateTime with nano, second, minute set to 0 and hour to 5
     */
    public static LocalDateTime setLocalDateTimeToDayStart(LocalDateTime localDateTime) {
        return localDateTime.withNano(0).withSecond(0).withMinute(0).withHour(5);
    }
}
