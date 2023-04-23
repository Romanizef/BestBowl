package de.softwareprojekt.bestbowl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;

/**
 * @author Marten Voß
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static final File WORKING_DIR_ROOT = new File("");

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
        return s != null && s.length() != 0;
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
     * Formats a double to 2 decimal places with german locale
     *
     * @param d double to be formatted
     * @return the formatted string
     */
    public static String formatDouble(double d) {
        return String.format(Locale.GERMANY, "%.2f", d);
    }
}
