package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.*;
import de.softwareprojekt.bestbowl.jpa.repositories.*;
import de.softwareprojekt.bestbowl.utils.VaadinUtils;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.extrasElements.DrinkPanel;
import de.softwareprojekt.bestbowl.views.extrasElements.FoodPanel;
import de.softwareprojekt.bestbowl.views.extrasElements.ShoePanel;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
@Route(value = "extras", layout = MainView.class)
@PageTitle("Extras")
@PermitAll
@PreserveOnRefresh
public class ExtrasView extends VerticalLayout {
    private final DrinkRepository drinkRepository;
    private final FoodRepository foodRepository;

    private final BowlingShoeRepository bowlingShoeRepository;
    private final DrinkVariantRepository drinkVariantRepository;
    private final Map<String, DrinkBooking> drinkBookingMap = new HashMap<>();
    private final BowlingAlleyRepository bowlingAlleyRepository;
    private FoodBookingRepository foodBookingRepository;

    private BowlingShoeBookingRepository bowlingShoeBookingRepository;
    private DrinkBookingRepository drinkBookingRepository;
    private int currentBowlingAlleyId;
    private BowlingAlleyBooking currentBowlingAlleyBooking;
    private Map<Integer, Button> buttonMap;
    private BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private HorizontalLayout alleyLayout;
    private HorizontalLayout tabLayout;
    private TabSheet tabs;
    private Div drinkDiv;
    private Div foodDiv;

    private ShoePanel shoePanel;
    private Div shoeDiv;

    private FormLayout foodFormLayoutForAddItem;
    private VerticalLayout drinkVerticalLayoutForAddItem;
    private Button addItem;
    private Button goToBill;


    @Autowired
    public ExtrasView(DrinkRepository drinkRepository, FoodRepository foodRepository,
                      BowlingAlleyRepository bowlingAlleyRepository, BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
                      FoodBookingRepository foodBookingRepository, DrinkBookingRepository drinkBookingRepository,
                      DrinkVariantRepository drinkVariantRepository, BowlingShoeRepository bowlingShoeRepository,
                      BowlingShoeBookingRepository bowlingShoeBookingRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        this.drinkRepository = drinkRepository;
        this.drinkVariantRepository = drinkVariantRepository;
        this.foodRepository = foodRepository;
        this.bowlingShoeRepository = bowlingShoeRepository;
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.bowlingShoeBookingRepository = bowlingShoeBookingRepository;
        this.currentBowlingAlleyId = 1;
        buttonMap = new HashMap<>();
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        Component alleyButtonsComponent = createAlleyButtonsComponent();
        Component articlePanelComponent = createArticlePanelComponent();
        Component footerButtons = createFooterButtons();
        goToBill.setEnabled(false);
        addItem.setEnabled(false);
        add(alleyButtonsComponent, articlePanelComponent, footerButtons);
    }


    private Component createArticlePanelComponent() {
        tabs = new TabSheet();
        tabLayout = new HorizontalLayout();
        tabs.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        Tab drink = new Tab(VaadinIcon.COFFEE.create(), new Span("Getränke"));
        Tab food = new Tab(VaadinIcon.CROSS_CUTLERY.create(), new Span("Speisen"));
        Tab shoe = new Tab(VaadinIcon.RETWEET.create(), new Span("Schuhe"));
        drinkDiv = new Div(new Text("Keine Bahn ausgewählt"));
        foodDiv = new Div(new Text("Keine Bahn ausgewählt"));
        shoeDiv = new Div(new Text("Keine Bahn ausgewählt"));
        shoeDiv.getStyle().set("text-align", "center");
        foodDiv.getStyle().set("text-align", "center");
        drinkDiv.getStyle().set("text-align", "center");
        tabs.add(drink, drinkDiv);
        tabs.add(food, foodDiv);
        tabs.add(shoe, shoeDiv);
        tabLayout.add(tabs);
        return tabLayout;
    }


    private Component createShoePanel() {
        shoePanel = new ShoePanel(bowlingShoeRepository, currentBowlingAlleyBooking.getClient());

        return shoePanel;
    }

    private Component createDrinkPanel() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setWidthFull();
        List<Drink> drinkList = drinkRepository.findAll();

        for (Drink drink : drinkList) {
            verticalLayout.add(new DrinkPanel(drink, currentBowlingAlleyBooking, drinkBookingMap));
        }
        verticalLayout.setMaxHeight("400px");
        drinkVerticalLayoutForAddItem = verticalLayout;
        return verticalLayout;
    }

    private Component createFoodPanel() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("100px", 2));
        List<Food> foodList = foodRepository.findAll();
        for (Food food : foodList) {
            formLayout.add(new FoodPanel(food));
        }
        formLayout.setMaxWidth("1000px");
        foodFormLayoutForAddItem = formLayout;
        return formLayout;
    }

    private final Component createAlleyButtonsComponent() {
        alleyLayout = new HorizontalLayout();
        alleyLayout.setPadding(true);
        alleyLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        List<BowlingAlley> bowlingAlleyList = bowlingAlleyRepository.findAll();
        if (bowlingAlleyList.isEmpty()) {
            Notifications.showError("Es sind keine Bahnen im System vorhanden./nBitte trage Bahnen ins System ein.");
        }
        bowlingAlleyList.sort(Comparator.comparingInt(BowlingAlley::getId));
        long currentTime = System.currentTimeMillis();
        List<BowlingAlley> freeBowlingAlleyList = bowlingAlleyRepository.findAllByNoBookingOverlapBetweenTimeStamps(currentTime, currentTime);
        Set<Integer> freeBowlingAlleyHashSet = freeBowlingAlleyList.stream().map(BowlingAlley::getId).collect(Collectors.toSet());

        List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyBookingRepository.findAllByTimePeriodsOverlapping(System.currentTimeMillis());

        //Todo exception handling wenn keine bahnen

        for (BowlingAlley bowlingAlley : bowlingAlleyList) {
            Button alleyButton = new Button("Bahn " + bowlingAlley.getId());
            buttonMap.put(bowlingAlley.getId(), alleyButton);
            alleyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            alleyButton.setEnabled(!freeBowlingAlleyHashSet.contains(bowlingAlley.getId()));
            alleyButton.addClickListener(buttonClickEvent -> {
                changePreviousButtonStyle(currentBowlingAlleyId);
                currentBowlingAlleyId = Integer.parseInt(alleyButton.getText().replaceAll("\\D+(\\d+)", "$1"));
                changeCurrentButtonSytle(currentBowlingAlleyId);
                currentBowlingAlleyBooking = bowlingAlleyBookingList.stream().filter(bowlingAlleyBooking ->
                                bowlingAlleyBooking.getBowlingAlley().getId() == currentBowlingAlleyId)
                        .findFirst()
                        .orElse(null);


                changeTabs();
                goToBill.setEnabled(true);
                addItem.setEnabled(true);
            });

            alleyLayout.add(alleyButton);
        }

        return alleyLayout;
    }

    private void changeTabs() {
        drinkDiv.removeAll();
        foodDiv.removeAll();
        shoeDiv.removeAll();

        drinkDiv.add(createDrinkPanel());
        foodDiv.add(createFoodPanel());
        shoeDiv.add(createShoePanel());
    }

    private void changePreviousButtonStyle(int currentBowlAlleyId) {
        buttonMap.get(currentBowlAlleyId).removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
    }

    private void changeCurrentButtonSytle(int currentBowlAlleyId) {
        buttonMap.get(currentBowlAlleyId).addThemeVariants(ButtonVariant.LUMO_SUCCESS);
    }


    private Component createFooterButtons() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        addItem = new Button("Bestellung speichern");
        goToBill = new Button("Zur Rechnungserstellung");
        addItem.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItem.setWidth("55%");
        String bHeight = "55px";
        addItem.setHeight(bHeight);
        goToBill.setWidth("40%");
        goToBill.setHeight(bHeight);

        //ToDo Dialog Fenster "Sind Sie sicher das sie Bezahlen wollen?"

        addItem.addClickListener(buttonClickEvent -> {
            addAllNewDrinkBookings();
            addAllNewFoodBookings();
            addAllNewShoeBookings();
            changeTabs();

        });

        goToBill.addClickListener(buttonClickEvent -> {
            addAllNewDrinkBookings();//TODO Check ob Stock vorhanden ist
            addAllNewFoodBookings(); //TODO Check ob Stock vorhanden ist
            VaadinUtils.showConfirmationDialog("Rechnung bezahlen?", "Ja", "Abbrechen", () -> {
                UI.getCurrent().navigate(InvoiceView.class).ifPresent(view -> view.setBowlingAlleyBooking(currentBowlingAlleyBooking));
                //TODO zu neuer View mit Allen Rechnungen die Uncompleted sind
            });
        });

        layout.add(addItem, goToBill);
        return layout;
    }

    private void addAllNewShoeBookings() {
        int stock = shoePanel.getShoeSizeAmountMap().get(shoePanel.getShoeSizeField().getValue()) - shoePanel.getShoeAmountField().getValue();
        if (stock < 0) {
            Notifications.showError("Nicht genügend Schuhe in Größe: " + shoePanel.getShoeSizeField().getValue());
            return;
        }

        List<BowlingShoe> bowlingShoeList = bowlingShoeRepository.findAllByClientIsNullAndActiveIsTrue();
        BowlingShoe bowlingShoeForBooking = new BowlingShoe();
        for (BowlingShoe bowlingShoeInList : bowlingShoeList) {
            if (bowlingShoeInList.getSize() == shoePanel.getShoeSizeField().getValue()) {
                bowlingShoeForBooking = bowlingShoeInList;
            }
        }

        bowlingShoeForBooking.setClient(currentBowlingAlleyBooking.getClient());
        bowlingShoeRepository.save(bowlingShoeForBooking);
        BowlingShoeBooking bowlingShoeBooking = new BowlingShoeBooking();
        bowlingShoeBooking.setBowlingAlley(currentBowlingAlleyBooking.getBowlingAlley());
        bowlingShoeBooking.setClient(currentBowlingAlleyBooking.getClient());
        bowlingShoeBooking.setTimeStamp(System.currentTimeMillis());
        bowlingShoeBooking.setBowlingShoe(bowlingShoeForBooking);
        bowlingShoeBookingRepository.save(bowlingShoeBooking);
        shoePanel.updateShoeSizeAmountMap();
        shoePanel.resetIntergerfield();
    }

    private void addAllNewDrinkBookings() {
        List<DrinkBooking> drinkBookingList = drinkBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals
                        (currentBowlingAlleyBooking.getClient(), currentBowlingAlleyBooking.getBowlingAlley(),
                                currentBowlingAlleyBooking.getStartTime());
        Map<String, DrinkBooking> drinkBookingMapFromDB = drinkBookingList.stream()
                .collect(Collectors.toMap(DrinkBooking::getName, Function.identity()));

        drinkBookingMap.forEach((name, booking) -> {
            //Drink über name rausholen und dann vergleichen wie viel noch da ist
            Drink drink = drinkRepository.findByName(booking.getDrinkName());
            int newStock = drink.getStockInMilliliters() - booking.getAmount() * booking.getMl();
            if (newStock < 0) { //Todo Booking kriegt noch ml
                Notifications.showError("Nicht genügend vom Getränk: " + drink.getName());
                return;
            } else {
                drink.setStockInMilliliters(newStock);
                drinkRepository.save(drink);
            }
            if (drinkBookingMapFromDB.containsKey(name)) {
                DrinkBooking drinkBooking = drinkBookingMapFromDB.get(name);
                booking.setAmount(booking.getAmount() + drinkBooking.getAmount());
            }

            drinkBookingRepository.save(booking);
        });
        drinkVerticalLayoutForAddItem.getChildren().forEach(component -> {
            if (component instanceof DrinkPanel drinkPanel) {
                drinkPanel.resetIntegerField();
            }
        });

        drinkBookingMap.clear();
    }

    private void addAllNewFoodBookings() {
        foodFormLayoutForAddItem.getChildren().forEach(component -> {
            if (component instanceof FoodPanel foodPanel) {
                int amount = foodPanel.getFoodAmountField().getValue(); // Get the value from the IntegerField
                if (amount > 0) {
                    String foodName = foodPanel.getFoodLabel().getText();
                    int newStock = foodRepository.findByName(foodName).getStock() - amount;
                    if (newStock < 0) {
                        Notifications.showError("Nicht genügend von der Speise: " + foodName);
                    } else {
                        saveNewFoodBooking(foodName, amount);
                        foodPanel.resetFoodAmountFieldValue();
                    }
                }
            }
        });
    }

    private void saveNewFoodBooking(String foodName, int amount) {
        List<Food> foodList = foodRepository.findAll();
        Food selectedFood = new Food();
        for (Food food : foodList) {
            if (food.getName().equals(foodName)) {
                selectedFood = food;
            }
        }
        FoodBooking foodBooking = new FoodBooking();
        foodBooking.setClient(currentBowlingAlleyBooking.getClient());
        foodBooking.setAmount(amount);
        foodBooking.setTimeStamp(currentBowlingAlleyBooking.getStartTime());
        foodBooking.setBowlingAlley(currentBowlingAlleyBooking.getBowlingAlley());
        foodBooking.setName(selectedFood.getName());
        foodBooking.setPrice(selectedFood.getPrice());
        System.out.println(foodBooking.toString());
        foodBookingRepository.save(foodBooking);
    }

    public void setCurrentBowlingAlleyBooking(BowlingAlleyBooking currentBowlingAlleyBooking) {
        this.currentBowlingAlleyBooking = currentBowlingAlleyBooking;
    }
}
