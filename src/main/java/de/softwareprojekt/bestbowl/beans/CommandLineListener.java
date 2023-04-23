package de.softwareprojekt.bestbowl.beans;

import com.github.javafaker.Faker;
import de.softwareprojekt.bestbowl.BestBowlApplication;
import de.softwareprojekt.bestbowl.jpa.entities.Address;
import de.softwareprojekt.bestbowl.jpa.entities.Association;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.utils.PDFUtils;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;

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
                    } else if (command.equals("generateRandomClients")) {
                        generateRandomClients(100);
                        LOGGER.info("Clients generated");
                    } else if (command.equals("generateRandomAssociations")) {
                        LOGGER.info("Associations generated");
                        generateRandomAssociations(10);
                    } else if (command.equals("connectSomeClientsToAssociations")) {
                        connectRandomClientsAndAssociations(50);
                        LOGGER.info("random connections done");
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

    private void generateRandomClients(int count) {
        List<Client> clientList = new ArrayList<>(count);
        Faker faker = new Faker();
        for (int i = 0; i < count; i++) {
            Client client = new Client();
            client.setFirstName(faker.name().firstName());
            client.setLastName(faker.name().lastName());
            client.setEmail(client.getFirstName() + "." + client.getLastName() + "@example.com");
            Address address = new Address();
            address.setStreet(faker.address().streetName());
            address.setHouseNr(Integer.parseInt(faker.bothify("##")));
            address.setPostCode(Integer.parseInt(faker.bothify("#####")));
            address.setCity(faker.address().cityName());
            client.addAddress(address);
            clientList.add(client);
        }
        Repos.getClientRepository().saveAll(clientList);
    }

    private void generateRandomAssociations(int count) {
        List<Association> existingAssociations = Repos.getAssociationRepository().findAll();
        Set<String> existingNames = new HashSet<>();
        existingAssociations.forEach(a -> existingNames.add(a.getName()));

        List<Association> associationList = new ArrayList<>(count);
        Faker faker = new Faker();
        for (int i = 0; i < count; i++) {
            Association association = new Association();
            String name = null;
            int counter = 0;
            while (name == null && counter < 20) {
                String s = faker.nation().language() + "-BowlingStars";
                if (!existingNames.contains(s)) {
                    name = s;
                }
                counter++;
            }
            if (name == null) {
                continue;
            }
            existingNames.add(name);
            association.setName(name);
            association.setDiscount(5);
            associationList.add(association);
        }
        Repos.getAssociationRepository().saveAll(associationList);
    }

    private void connectRandomClientsAndAssociations(int count) {
        List<Association> associationList = Repos.getAssociationRepository().findAll();
        List<Client> clientList = Repos.getClientRepository().findAll();
        Random random = new SecureRandom();
        for (int i = 0; i < count; i++) {
            Client client = clientList.get(random.nextInt(clientList.size()));
            if (client.getAssociation() == null) {
                Association association = associationList.get(random.nextInt(associationList.size()));
                association.addClient(client);
                Repos.getClientRepository().save(client);
                Repos.getAssociationRepository().save(association);
            }
        }
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
