package de.softwareprojekt.bestbowl.beans;

import de.softwareprojekt.bestbowl.BestBowlApplication;
import de.softwareprojekt.bestbowl.utils.PDFUtils;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

import static de.softwareprojekt.bestbowl.utils.Utils.startThread;

/**
 * @author Marten Voß
 */
@Component
public class CommandLineListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineListener.class);
    private UserManager userManager;

    @PostConstruct
    public void init() {
        startCommandLineThread();
    }

    private void startCommandLineThread() {
        startThread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String command = scanner.next();
                    if (command == null) {
                        continue;
                    }

                    if (command.equals("shutdown")) {
                        LOGGER.info("shutting down ...");
                        BestBowlApplication.shutdown();
                    } else if (command.equals("addUsers")) {
                        userManager.addNewUser("admin", "admin", "1.1.2000", "-", UserRole.ADMIN);
                        userManager.addNewUser("owner", "owner", "1.1.2000", "-", UserRole.OWNER);
                        userManager.addNewUser("employee", "employee", "1.1.2000", "-", UserRole.EMPLOYEE);
                        LOGGER.info("Users added");
                    } else if (command.equals("demoPdf")) {
                        PDFUtils.createDemoPdf();
                        LOGGER.info("pdf created");
                    }

                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        LOGGER.error(e.getMessage());
                    }
                    scanner = new Scanner(System.in);
                }
            }
        }, "Command Line", true);
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
