package de.softwareprojekt.bestbowl.beans;

import com.github.javafaker.Faker;
import de.softwareprojekt.bestbowl.BestBowlApplication;
import de.softwareprojekt.bestbowl.jpa.entities.*;
import de.softwareprojekt.bestbowl.utils.DuplicateChecker;
import de.softwareprojekt.bestbowl.utils.PDFUtils;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

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
                        userManager.addNewUser("admin", "admin", "23.5.1993", "admin@bestbowl.de", UserRole.ADMIN);
                        userManager.addNewUser("owner", "owner", "7.3.1986", "owner@bestbowl.de", UserRole.OWNER);
                        userManager.addNewUser("employee", "employee", "19.10.2001", "employee@bestbowl.de", UserRole.EMPLOYEE);
                        LOGGER.info("Users added");
                    } else if (command.equals("generateRandomClients")) {
                        generateRandomClients(100);
                        LOGGER.info("Clients generated");
                    } else if (command.equals("generateRandomAssociations")) {
                        generateRandomAssociations(10);
                        LOGGER.info("Associations generated");
                    } else if (command.equals("connectSomeClientsToAssociations")) {
                        connectRandomClientsAndAssociations(50);
                        LOGGER.info("random connections done");
                    } else if (command.equals("generateRandomFoods")) {
                        generateRandomFoods(4);
                        LOGGER.info("Foods generated");
                    } else if (command.equals("generateRandomDrinks")) {
                        generateRandomDrinks(4);
                        LOGGER.info("Drinks generated");
                    } else if (command.equals("generateRandomShoes")) {
                        generateRandomShoes(50);
                        LOGGER.info("Shoes generated");
                    } else if (command.equals("generateEverything")) {
                        generateRandomClients(200);
                        generateRandomAssociations(20);
                        connectRandomClientsAndAssociations(100);
                        generateRandomFoods(7);
                        generateRandomDrinks(7);
                        generateRandomShoes(100);
                        LOGGER.info("everything generated");
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
        List<Association> existingAssociationList = Repos.getAssociationRepository().findAll();
        DuplicateChecker duplicateChecker = new DuplicateChecker(existingAssociationList.stream().map(Association::getName).collect(Collectors.toSet()));

        List<Association> associationList = new ArrayList<>(count);
        Faker faker = new Faker();
        for (int i = 0; i < count; i++) {
            duplicateChecker.generateNewValue(() -> faker.nation().language() + "-BowlingStars").ifPresent(name -> {
                Association association = new Association();
                association.setName(name);
                association.setDiscount(5);
                associationList.add(association);
            });
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
            }
        }
        Repos.getAssociationRepository().saveAll(associationList);
    }

    private void generateRandomFoods(int count) {
        List<Food> existingFoodList = Repos.getFoodRepository().findAll();
        DuplicateChecker duplicateChecker = new DuplicateChecker(existingFoodList.stream().map(Food::getName).collect(Collectors.toSet()));

        List<Food> foodList = new ArrayList<>(count);
        Faker faker = new Faker();
        for (int i = 0; i < count; i++) {
            duplicateChecker.generateNewValue(() -> faker.food().dish()).ifPresent(name -> {
                Food food = new Food();
                food.setName(name);
                food.setPrice(faker.random().nextInt(3, 10));
                food.setStock(faker.random().nextInt(5, 20));
                food.setReorderPoint(3);
                foodList.add(food);
            });
        }
        Repos.getFoodRepository().saveAll(foodList);
    }

    private void generateRandomDrinks(int count) {
        List<Drink> existingDrinkList = Repos.getDrinkRepository().findAll();
        DuplicateChecker duplicateChecker = new DuplicateChecker(existingDrinkList.stream().map(Drink::getName).collect(Collectors.toSet()));

        List<Drink> foodList = new ArrayList<>(count);
        Faker faker = new Faker();
        for (int i = 0; i < count; i++) {
            duplicateChecker.generateNewValue(() -> faker.beer().yeast()).ifPresent(name -> {
                Drink drink = new Drink();
                drink.setName(name);
                drink.setStockInMilliliters(faker.random().nextInt(15, 45) * 1000);
                drink.setReorderPoint(5000);
                double price = faker.random().nextInt(1, 3);
                for (int j = 1; j <= 3; j++) {
                    DrinkVariant drinkVariant = new DrinkVariant();
                    drinkVariant.setMl(j * 250);
                    drinkVariant.setPrice(j * price);
                    drink.addDrinkVariant(drinkVariant);
                }
                foodList.add(drink);
            });
        }
        Repos.getDrinkRepository().saveAll(foodList);
    }

    public void generateRandomShoes(int count) {
        List<BowlingShoe> bowlingShoeList = new ArrayList<>(count);
        Faker faker = new Faker();
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            BowlingShoe bowlingShoe = new BowlingShoe();
            bowlingShoe.setSize(faker.random().nextInt(30, 50));
            bowlingShoe.setBoughtAt(currentTime - faker.random().nextInt(1, 100) * Duration.ofDays(1).toMillis());
            bowlingShoeList.add(bowlingShoe);
        }
        Repos.getBowlingShoeRepository().saveAll(bowlingShoeList);
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
