package de.softwareprojekt.bestbowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BestBowlApplication {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(BestBowlApplication.class, args);
    }

    /**
     * Initiates an orderly shutdown of the application, db and the jvm
     */
    public static void shutdown() {
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }
}
