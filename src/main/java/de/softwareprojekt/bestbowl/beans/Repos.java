package de.softwareprojekt.bestbowl.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.client.AddressRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.client.AssociationRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.client.ClientRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkVariantRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodRepository;
import jakarta.annotation.PostConstruct;

/**
 * Class with Getters and Setters for the Repositories
 * 
 * @author Max
 */
@Component
public class Repos {
    private static Repos instance;
    private AddressRepository addressRepository;
    private AssociationRepository associationRepository;
    private BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private BowlingAlleyRepository bowlingAlleyRepository;
    private BowlingCenterRepository bowlingCenterRepository;
    private BowlingShoeBookingRepository bowlingShoeBookingRepository;
    private BowlingShoeRepository bowlingShoeRepository;
    private ClientRepository clientRepository;
    private DrinkBookingRepository drinkBookingRepository;
    private DrinkRepository drinkRepository;
    private DrinkVariantRepository drinkVariantRepository;
    private FoodBookingRepository foodBookingRepository;
    private FoodRepository foodRepository;
    private UserRepository userRepository;

    /**
     * Getter for an instance of the {@code AddressRepository}
     * 
     * @return {@code AddressRepository}
     */
    public static AddressRepository getAddressRepository() {
        return instance.addressRepository;
    }

    /**
     * Setter for the {@code AddressRepository}
     * 
     * @param addressRepository
     */
    @Autowired
    public void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Getter for an instance of the {@code AssociationRepository}
     * 
     * @return {@code AssociationRepository}
     */
    public static AssociationRepository getAssociationRepository() {
        return instance.associationRepository;
    }

    /**
     * Setter for the {@code AssociationRepository}
     * 
     * @param associationRepository
     */
    @Autowired
    public void setAssociationRepository(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    /**
     * Getter for an instance of the {@code BowlingAlleyBookingRepository}
     * 
     * @return {@code BowlingAlleyBookingRepository}
     */
    public static BowlingAlleyBookingRepository getBowlingAlleyBookingRepository() {
        return instance.bowlingAlleyBookingRepository;
    }

    /**
     * Setter for the {@code BowlingAlleyBookingRepository}
     * 
     * @param bowlingAlleyBookingRepository
     */
    @Autowired
    public void setBowlingAlleyBookingRepository(BowlingAlleyBookingRepository bowlingAlleyBookingRepository) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
    }

    /**
     * Getter for an instance of the {@code BowlingAlleyRepository}
     * 
     * @return {@code BowlingAlleyRepository}
     */
    public static BowlingAlleyRepository getBowlingAlleyRepository() {
        return instance.bowlingAlleyRepository;
    }

    /**
     * Setter for the {@code BowlingAlleyRepository}
     * 
     * @param bowlingAlleyRepository
     */
    @Autowired
    public void setBowlingAlleyRepository(BowlingAlleyRepository bowlingAlleyRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
    }

    /**
     * Getter for an instance of the {@code BowlingCenterRepository}
     * 
     * @return {@code BowlingCenterRepository}
     */
    public static BowlingCenterRepository getBowlingCenterRepository() {
        return instance.bowlingCenterRepository;
    }

    /**
     * Setter for the {@code BowlingCenterRepository}
     * 
     * @param bowlingCenterRepository
     */
    @Autowired
    public void setBowlingCenterRepository(BowlingCenterRepository bowlingCenterRepository) {
        this.bowlingCenterRepository = bowlingCenterRepository;
    }

    /**
     * Getter for an instance of the {@code BowlingShoeBookingRepository}
     * 
     * @return {@code BowlingShoeBookingRepository}
     */
    public static BowlingShoeBookingRepository getBowlingShoeBookingRepository() {
        return instance.bowlingShoeBookingRepository;
    }

    /**
     * Setter for the {@code BowlingShoeBookingRepository}
     * 
     * @param bowlingShoeBookingRepository
     */
    @Autowired
    public void setBowlingShoeBookingRepository(BowlingShoeBookingRepository bowlingShoeBookingRepository) {
        this.bowlingShoeBookingRepository = bowlingShoeBookingRepository;
    }

    /**
     * Getter for an instance of the {@code BowlingShoeRepository}
     * 
     * @return {@code BowlingShoeRepository}
     */
    public static BowlingShoeRepository getBowlingShoeRepository() {
        return instance.bowlingShoeRepository;
    }

    /**
     * Setter for the {@code BowlingShoeRepository}
     * 
     * @param bowlingShoeRepository
     */
    @Autowired
    public void setBowlingShoeRepository(BowlingShoeRepository bowlingShoeRepository) {
        this.bowlingShoeRepository = bowlingShoeRepository;
    }

    /**
     * Getter for an instance of the {@code ClientRepository}
     * 
     * @return {@code ClientRepository}
     */
    public static ClientRepository getClientRepository() {
        return instance.clientRepository;
    }

    /**
     * Setter for the {@code ClientRepository}
     * 
     * @param clientRepository
     */
    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Getter for an instance of the {@code DrinkBookingRepository}
     * 
     * @return {@code DrinkBookingRepository}
     */
    public static DrinkBookingRepository getDrinkBookingRepository() {
        return instance.drinkBookingRepository;
    }

    /**
     * Setter for the {@code DrinkBookingRepository}
     * 
     * @param drinkBookingRepository
     */
    @Autowired
    public void setDrinkBookingRepository(DrinkBookingRepository drinkBookingRepository) {
        this.drinkBookingRepository = drinkBookingRepository;
    }

    /**
     * Getter for an instance of the {@code DrinkRepository}
     * 
     * @return {@code DrinkRepository}
     */
    public static DrinkRepository getDrinkRepository() {
        return instance.drinkRepository;
    }

    /**
     * Setter for the {@code DrinkRepository}
     * 
     * @param drinkRepository
     */
    @Autowired
    public void setDrinkRepository(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    /**
     * Getter for an instance of the {@code DrinkVariantRepository}
     * 
     * @return {@code DrinkVariantRepository}
     */
    public static DrinkVariantRepository getDrinkVariantRepository() {
        return instance.drinkVariantRepository;
    }

    /**
     * Setter for the {@code DrinkVariantRepository}
     * 
     * @param drinkVariantRepository
     */
    @Autowired
    public void setDrinkVariantRepository(DrinkVariantRepository drinkVariantRepository) {
        this.drinkVariantRepository = drinkVariantRepository;
    }

    /**
     * Getter for an instance of the {@code FoodBookingRepository}
     * 
     * @return {@code FoodBookingRepository}
     */
    public static FoodBookingRepository getFoodBookingRepository() {
        return instance.foodBookingRepository;
    }

    /**
     * Setter for the {@code FoodBookingRepository}
     * 
     * @param foodBookingRepository
     */
    @Autowired
    public void setFoodBookingRepository(FoodBookingRepository foodBookingRepository) {
        this.foodBookingRepository = foodBookingRepository;
    }

    /**
     * Getter for an instance of the {@code FoodRepository}
     * 
     * @return {@code FoodRepository}
     */
    public static FoodRepository getFoodRepository() {
        return instance.foodRepository;
    }

    /**
     * Setter for the {@code FoodRepository}
     * 
     * @param foodRepository
     */
    @Autowired
    public void setFoodRepository(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    /**
     * Getter for an instance of the {@code UserRepository}
     * 
     * @return {@code UserRepository}
     */
    public static UserRepository getUserRepository() {
        return instance.userRepository;
    }

    /**
     * Setter for the {@code UserRepository}
     * 
     * @param userRepository
     */
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Constructs a new {@code Repos} object
     */
    @PostConstruct
    private void init() {
        instance = this;
    }
}