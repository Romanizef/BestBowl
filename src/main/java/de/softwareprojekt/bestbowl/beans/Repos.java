package de.softwareprojekt.bestbowl.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.softwareprojekt.bestbowl.jpa.repositories.client.AddressRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.client.AssociationRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.client.ClientRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkVariantRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodRepository;
import jakarta.annotation.PostConstruct;

/**
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

    public static AddressRepository getAddressRepository() {
        return instance.addressRepository;
    }

    @Autowired
    public void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public static AssociationRepository getAssociationRepository() {
        return instance.associationRepository;
    }

    @Autowired
    public void setAssociationRepository(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    public static BowlingAlleyBookingRepository getBowlingAlleyBookingRepository() {
        return instance.bowlingAlleyBookingRepository;
    }

    @Autowired
    public void setBowlingAlleyBookingRepository(BowlingAlleyBookingRepository bowlingAlleyBookingRepository) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
    }

    public static BowlingAlleyRepository getBowlingAlleyRepository() {
        return instance.bowlingAlleyRepository;
    }

    @Autowired
    public void setBowlingAlleyRepository(BowlingAlleyRepository bowlingAlleyRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
    }

    public static BowlingCenterRepository getBowlingCenterRepository() {
        return instance.bowlingCenterRepository;
    }

    @Autowired
    public void setBowlingCenterRepository(BowlingCenterRepository bowlingCenterRepository) {
        this.bowlingCenterRepository = bowlingCenterRepository;
    }

    public static BowlingShoeBookingRepository getBowlingShoeBookingRepository() {
        return instance.bowlingShoeBookingRepository;
    }

    @Autowired
    public void setBowlingShoeBookingRepository(BowlingShoeBookingRepository bowlingShoeBookingRepository) {
        this.bowlingShoeBookingRepository = bowlingShoeBookingRepository;
    }

    public static BowlingShoeRepository getBowlingShoeRepository() {
        return instance.bowlingShoeRepository;
    }

    @Autowired
    public void setBowlingShoeRepository(BowlingShoeRepository bowlingShoeRepository) {
        this.bowlingShoeRepository = bowlingShoeRepository;
    }

    public static ClientRepository getClientRepository() {
        return instance.clientRepository;
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public static DrinkBookingRepository getDrinkBookingRepository() {
        return instance.drinkBookingRepository;
    }

    @Autowired
    public void setDrinkBookingRepository(DrinkBookingRepository drinkBookingRepository) {
        this.drinkBookingRepository = drinkBookingRepository;
    }

    public static DrinkRepository getDrinkRepository() {
        return instance.drinkRepository;
    }

    @Autowired
    public void setDrinkRepository(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    public static DrinkVariantRepository getDrinkVariantRepository() {
        return instance.drinkVariantRepository;
    }

    @Autowired
    public void setDrinkVariantRepository(DrinkVariantRepository drinkVariantRepository) {
        this.drinkVariantRepository = drinkVariantRepository;
    }

    public static FoodBookingRepository getFoodBookingRepository() {
        return instance.foodBookingRepository;
    }

    @Autowired
    public void setFoodBookingRepository(FoodBookingRepository foodBookingRepository) {
        this.foodBookingRepository = foodBookingRepository;
    }

    public static FoodRepository getFoodRepository() {
        return instance.foodRepository;
    }

    @Autowired
    public void setFoodRepository(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public static UserRepository getUserRepository() {
        return instance.userRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void init() {
        instance = this;
    }
}
