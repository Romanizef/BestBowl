package de.softwareprojekt.bestbowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Ali
 */
@SpringBootApplication
public class BestBowlApplication {
    private static ConfigurableApplicationContext context;

    /**
     * The main function of the BestBowlApplication class.
     * 
     * @param String[] args Pass arguments to the main method
     *
     * @return A springapplication
     */
    public static void main(String[] args) {
        context = SpringApplication.run(BestBowlApplication.class, args);
    }

    /**
     * The shutdown function is used to gracefully shutdown the application.
     * It will wait for all threads to finish before shutting down.
     * 
     * @return The exit code of the springapplication
     */
    public static void shutdown() {
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }
}