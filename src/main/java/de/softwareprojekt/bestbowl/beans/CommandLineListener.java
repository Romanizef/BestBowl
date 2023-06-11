package de.softwareprojekt.bestbowl.beans;

import static de.softwareprojekt.bestbowl.utils.Utils.startThread;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import de.softwareprojekt.bestbowl.BestBowlApplication;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.client.Address;
import de.softwareprojekt.bestbowl.jpa.entities.client.Association;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.entities.food.Food;
import de.softwareprojekt.bestbowl.utils.checkers.DuplicateChecker;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import jakarta.annotation.PostConstruct;

/**
 * Class that listens for commands in the command line.
 * 
 * @author Marten Voß
 */
@Component
public class CommandLineListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineListener.class);
    private UserManager userManager;

    /**
     * Initializes the listening method.
     * 
     * @see #startCommandLineThread()
     */
    @PostConstruct
    public void init() {
        startCommandLineThread();
    }

    /**
     * Scans the command line for commands.
     * 
     * @see #generateBowlingAlleys()
     * @see #generateRandomClients(int)
     * @see #generateRandomAssociations(int)
     * @see #connectRandomClientsAndAssociations(int)
     * @see #generateRandomFoods(int)
     * @see #generateRandomDrinks(int)
     * @see #generateRandomShoes(int)
     */
    private void startCommandLineThread() {
        startThread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String command = scanner.next();
                    if (command == null) {
                        continue;
                    }

                    switch (command) {
                        case "shutdown" -> {
                            LOGGER.info("shutting down ...");
                            BestBowlApplication.shutdown();
                        }
                        case "addUsers" -> {
                            userManager.addNewUser("admin", "admin", "10 + 5 = ?",
                                    "15", "admin@bestbowl.de", UserRole.ADMIN);
                            userManager.addNewUser("owner", "owner", "2 - 3 = ?",
                                    "-1", "owner@bestbowl.de", UserRole.OWNER);
                            userManager.addNewUser("employee", "employee", "5 * 7 = ?",
                                    "35", "employee@bestbowl.de", UserRole.EMPLOYEE);
                            LOGGER.info("Users added");
                        }
                        case "generate" -> {
                            generateRandomClients(200);
                            generateRandomAssociations(20);
                            connectRandomClientsAndAssociations(100);
                            generateRandomFoods(7);
                            generateRandomDrinks(7);
                            generateRandomShoes(100);
                            generateBowlingAlleys();
                            LOGGER.info("everything generated");
                        }
                        default -> LOGGER.info("unknown command");
                    }
                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        LOGGER.error(e.getMessage());
                    }
                    scanner.close();
                    scanner = new Scanner(System.in);
                }
            }
        }, "Command Line", true);
    }

    /**
     * Generates a given amount of {@code Client}s with random data.
     * 
     * @param count
     */
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
            address.setHouseNr(faker.number().numberBetween(1, 500));
            address.setPostCode(faker.number().numberBetween(1001, 99999));
            address.setCity(faker.address().cityName());
            client.addAddress(address);
            clientList.add(client);
        }
        Repos.getClientRepository().saveAll(clientList);
    }

    /**
     * Generates a given amount of {@code Association}s with random data.
     * 
     * @param count
     */
    private void generateRandomAssociations(int count) {
        List<Association> existingAssociationList = Repos.getAssociationRepository().findAll();
        DuplicateChecker duplicateChecker = new DuplicateChecker(
                existingAssociationList.stream().map(Association::getName).collect(Collectors.toSet()));

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

    /**
     * Connects a given amount of {@code Client}s to a random {@code Association}.
     * 
     * @param count
     */
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

    /**
     * Generates a given amount of {@code Food}s with random data.
     * 
     * @param count
     */
    private void generateRandomFoods(int count) {
        List<Food> existingFoodList = Repos.getFoodRepository().findAll();
        DuplicateChecker duplicateChecker = new DuplicateChecker(
                existingFoodList.stream().map(Food::getName).collect(Collectors.toSet()));

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

    /**
     * Generates a given amount of {@code Drink}s with random data.
     * 
     * @param count
     */
    private void generateRandomDrinks(int count) {
        List<Drink> existingDrinkList = Repos.getDrinkRepository().findAll();
        DuplicateChecker duplicateChecker = new DuplicateChecker(
                existingDrinkList.stream().map(Drink::getName).collect(Collectors.toSet()));

        List<Drink> foodList = new ArrayList<>(count);
        Faker faker = new Faker();
        for (int i = 0; i < count; i++) {
            duplicateChecker.generateNewValue(() -> faker.beer().yeast()).ifPresent(name -> {
                Drink drink = new Drink();
                drink.setName(name.replaceAll("\\d+ - ", ""));
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

    /**
     * Generates a given amount of {@code BowlingShoe}s with random data.
     * 
     * @param count
     */
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

    /**
     * Generates 10 {@code BowlingAlley}s with random data.
     */
    public void generateBowlingAlleys() {
        List<BowlingAlley> bowlingAlleyList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            BowlingAlley bowlingAlley = new BowlingAlley();
            bowlingAlley.setId(i + 1);
            bowlingAlleyList.add(bowlingAlley);
        }
        Repos.getBowlingAlleyRepository().saveAll(bowlingAlleyList);
    }

    /**
     * Setter for {@code UserManager}
     * 
     * @param userManager
     */
    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}