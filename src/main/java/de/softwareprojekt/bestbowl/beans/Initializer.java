package de.softwareprojekt.bestbowl.beans;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;

/**
 * Class for initializing the application.
 * 
 * @author Ali
 */
@Component
public class Initializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Initializer.class);
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";
    private final UserManager userManager;
    private final BowlingCenterRepository bowlingCenterRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for the Initializer bean.
     * 
     * @param userManager
     * @param bowlingCenterRepository
     * @param userRepository
     */
    @Autowired
    public Initializer(UserManager userManager, BowlingCenterRepository bowlingCenterRepository,
            UserRepository userRepository) {
        this.userManager = userManager;
        this.bowlingCenterRepository = bowlingCenterRepository;
        this.userRepository = userRepository;
    }

    /**
     * Initializes the class methods.
     * 
     * @see #createBowlingCenterIfNotPresent()
     * @see #createAdminUserIfNotPresent()
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        createBowlingCenterIfNotPresent();
        createAdminUserIfNotPresent();
        // init the in-memory user manager
        userManager.updateUsersFromDb();
    }

    /**
     * Creates the default bowling center if it does not exist yet.
     * 
     * @see #createDefaultBowlingCenter()
     */
    private void createBowlingCenterIfNotPresent() {
        Optional<BowlingCenter> bowlingCenterOptional = bowlingCenterRepository.findById(1);
        if (bowlingCenterOptional.isEmpty()) {
            BowlingCenter bowlingCenter = createDefaultBowlingCenter();
            bowlingCenterRepository.save(bowlingCenter);
        }
    }

    /**
     * Creates the default bowling center.
     * 
     * @return {@code BowlingCenter}
     */
    private BowlingCenter createDefaultBowlingCenter() {
        BowlingCenter bowlingCenter = new BowlingCenter();
        bowlingCenter.setId(1);
        bowlingCenter.setDisplayName("BestBowl");
        bowlingCenter.setBusinessName("BestBowl GmbH");
        bowlingCenter.setStreet("Interaktion");
        bowlingCenter.setHouseNr("1");
        bowlingCenter.setPostCode(33619);
        bowlingCenter.setCity("Bielefeld");
        bowlingCenter.setStartTime(Duration.ofHours(7).toSeconds());
        bowlingCenter.setEndTime(Duration.ofHours(23).toSeconds());
        bowlingCenter.setBowlingAlleyPricePerHour(10);
        bowlingCenter.setBowlingShoePrice(4.99);
        bowlingCenter.setMinShoeSize(20);
        bowlingCenter.setMaxShoeSize(60);
        bowlingCenter.setSenderEmail("");
        bowlingCenter.setPassword("");
        bowlingCenter.setSmtpHost("");
        bowlingCenter.setSmtpPort("");
        return bowlingCenter;
    }

    /**
     * Creates the default admin user if it does not exist yet.
     * 
     * @see #createDefaultAdminUser()
     */
    private void createAdminUserIfNotPresent() {
        List<User> adminUserList = userRepository.findAllByRoleEquals(UserRole.ADMIN);
        if (adminUserList.isEmpty()) {
            User adminUser = createDefaultAdminUser();
            Optional<User> userWithSameName = userRepository.findByName(adminUser.getName());
            userWithSameName.ifPresent(userRepository::delete);
            userRepository.save(adminUser);
            LOGGER.info("Initialer Admin Nutzer: Benutzername: '" + adminUser.getName() + "', Passwort: '"
                    + DEFAULT_ADMIN_PASSWORD + "'");
        }
    }

    /**
     * Creates the default admin user.
     * 
     * @return {@code User}
     */
    private User createDefaultAdminUser() {
        User user = new User();
        user.setName("admin");
        user.setEmail("admin@bestbowl.de");
        user.setEncodedPassword(userManager.encodePassword(DEFAULT_ADMIN_PASSWORD));
        user.setSecurityQuestion("admin");
        user.setSecurityQuestionAnswer("admin");
        user.setRole(UserRole.ADMIN);
        return user;
    }
}