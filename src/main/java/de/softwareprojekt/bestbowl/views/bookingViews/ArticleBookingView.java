package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkVariant;
import de.softwareprojekt.bestbowl.jpa.entities.food.Food;
import de.softwareprojekt.bestbowl.jpa.entities.food.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingAlley.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodRepository;
import de.softwareprojekt.bestbowl.utils.VaadinUtils;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.articlePanels.DrinkPanel;
import de.softwareprojekt.bestbowl.views.articlePanels.FoodPanel;
import de.softwareprojekt.bestbowl.views.articlePanels.ShoePanel;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.softwareprojekt.bestbowl.utils.Utils.toDateString;
import static de.softwareprojekt.bestbowl.utils.Utils.toHourOnlyString;

/**
 * Creates a View for the extra bookings, like food, drinks and shoes.
 *
 * @author Ali aus Mali
 */
@Route(value = "articleBooking", layout = MainView.class)
@PageTitle("Artikelbuchung")
@PermitAll
public class ArticleBookingView extends VerticalLayout implements HasUrlParameter<Integer> {
    private final transient DrinkRepository drinkRepository;
    private final transient FoodRepository foodRepository;
    private final transient BowlingShoeRepository bowlingShoeRepository;
    private final transient BowlingAlleyRepository bowlingAlleyRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository bowlingShoeBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final Map<String, DrinkBooking> drinkBookingMap = new HashMap<>();
    private final Map<String, FoodBooking> foodBookingMap = new HashMap<>();
    private final Map<Integer, Button> buttonMap;
    private final BowlingCenter bowlingCenter;
    private final H3 header;
    private BowlingAlleyBooking currentBowlingAlleyBooking;
    private Div drinkDiv;
    private Div foodDiv;
    private ShoePanel shoePanel;
    private Div shoeDiv;
    private VerticalLayout foodLayoutForAddItem;
    private VerticalLayout drinkLayoutForAddItem;
    private Button addItemButton;
    private Button goToBillButton;
    private Button deleteChangesButton;
    private boolean panelChanges;
    private List<IntegerField> allIntegerFields;

    /**
     * Creates a new instance of the ArticleBookingView.
     *
     * @param drinkRepository               the repository for drinks
     * @param foodRepository                the repository for food
     * @param bowlingAlleyRepository        the repository for bowling alleys
     * @param bowlingAlleyBookingRepository the repository for bowling alley
     *                                      bookings
     * @param foodBookingRepository         the repository for food bookings
     * @param drinkBookingRepository        the repository for drink bookings
     * @param bowlingShoeRepository         the repository for bowling shoes
     * @param bowlingShoeBookingRepository  the repository for bowling shoe bookings
     */
    @Autowired
    public ArticleBookingView(DrinkRepository drinkRepository,
                              FoodRepository foodRepository,
                              BowlingAlleyRepository bowlingAlleyRepository,
                              BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
                              FoodBookingRepository foodBookingRepository,
                              DrinkBookingRepository drinkBookingRepository,
                              BowlingShoeRepository bowlingShoeRepository,
                              BowlingShoeBookingRepository bowlingShoeBookingRepository,
                              BowlingCenterRepository bowlingCenterRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        this.drinkRepository = drinkRepository;
        this.foodRepository = foodRepository;
        this.bowlingShoeRepository = bowlingShoeRepository;
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.bowlingShoeBookingRepository = bowlingShoeBookingRepository;
        this.bowlingCenter = bowlingCenterRepository.getBowlingCenter();
        this.panelChanges = false;
        buttonMap = new HashMap<>();
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        header = new H3();
        allIntegerFields = new ArrayList<>();
        Component articlePanelComponent = createArticlePanelComponent();
        Component footerButtons = createFooterButtons();
        Component alleyButtonsComponent = createAlleyButtonsComponent();
        goToBillButton.setEnabled(false);
        addItemButton.setEnabled(false);
        deleteChangesButton.setEnabled(false);
        add(header, alleyButtonsComponent, articlePanelComponent, footerButtons);
        updateHeader();
    }

    private void updateHeader() {
        if (currentBowlingAlleyBooking == null) {
            header.setText("Keine Bahn ausgewählt");
        } else {
            String text = "Bahn: " + currentBowlingAlleyBooking.getBowlingAlley().getId() + " , " +
                    currentBowlingAlleyBooking.getClient().getFullName() + " , " +
                    toDateString(currentBowlingAlleyBooking.getStartTime()) + " - " +
                    toHourOnlyString(currentBowlingAlleyBooking.getEndTime() + 1);
            header.setText(text);
        }
    }

    private void updateHeaderCompletedBooking() {
        if (currentBowlingAlleyBooking == null) {
            header.setText("Keine Bahn ausgewählt");
        } else {
            String text = "Bahn: " + currentBowlingAlleyBooking.getBowlingAlley().getId() + " , " +
                    currentBowlingAlleyBooking.getClient().getFullName() + " , " +
                    toDateString(currentBowlingAlleyBooking.getStartTime()) + " - " +
                    toHourOnlyString(currentBowlingAlleyBooking.getEndTime() + 1)+ " wurde bezahlt.";
            header.setText(text);
        }
    }

    /**
     * The createArticlePanelComponent function creates a TabSheet with three tabs,
     * each containing a Div.
     * The Divs are used to display the article panels for drinks, food and shoes.
     *
     * @return The tabs component
     */
    private Component createArticlePanelComponent() {
        TabSheet tabs;
        tabs = new TabSheet();
        tabs.addThemeVariants(
                TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS,
                TabSheetVariant.MATERIAL_BORDERED);
        tabs.setHeight("calc(100vh - 306px)");
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
        return tabs;
    }

    /**
     * The createShoePanel function creates a new ShoePanel object and assigns it to
     * the shoePanel variable.
     *
     * @return A shoePanel, which is a component
     */
    private Component createShoePanel() {
        shoePanel = new ShoePanel(bowlingShoeRepository, currentBowlingAlleyBooking, bowlingCenter);
        allIntegerFields.add(shoePanel.getShoeAmountField());
        shoePanel.getShoeAmountField().addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> {
            for (IntegerField integerField : allIntegerFields) {
                if (integerField.getValue() > 0) {
                    panelChanges = true;
                    break;
                } else {
                    panelChanges = false;
                }
            }
            updateFooterButtons(panelChanges);
        });
        return shoePanel;
    }

    /**
     * The createDrinkPanel function creates a VerticalLayout that contains
     * DrinkPanels for each drink in the database.
     * The function also sets the currentBowlingAlleyBooking and drinkBookingMap
     * fields to their respective parameters.
     *
     * @return A verticallayout
     */
    private Component createDrinkPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        List<Drink> drinkList = drinkRepository.findAll();
        drinkList.sort(Comparator.comparing(Drink::getName));
        for (Drink drink : drinkList) {
            boolean hasAtLeastOneActiveVariant = false;
            for (DrinkVariant drinkVariant : drink.getDrinkVariants()) {
                if (drinkVariant.isActive()) {
                    hasAtLeastOneActiveVariant = true;
                    break;
                }
            }
            if (hasAtLeastOneActiveVariant) {
                DrinkPanel drinkPanel = new DrinkPanel(drink, currentBowlingAlleyBooking, drinkBookingMap);
                drinkPanel.getVariantLayout().getChildren().forEach(component -> {
                    if (component instanceof IntegerField integerField) {
                        allIntegerFields.add(integerField);
                        integerField.addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> {
                            for (IntegerField integerField1 :
                                    allIntegerFields) {
                                if (integerField1.getValue() > 0) {
                                    panelChanges = true;
                                    break;
                                } else {
                                    panelChanges = false;
                                }
                            }
                            updateFooterButtons(panelChanges);
                        });
                    }
                });
                layout.add(drinkPanel);
            }
        }
        drinkLayoutForAddItem = layout;
        return layout;
    }

    /**
     * The createFoodPanel function creates a VerticalLayout that contains all the
     * FoodPanels for each food item.
     * The layout is then returned to be added to the TabSheet.
     *
     * @return A verticallayout
     */
    private Component createFoodPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        List<Food> foodList = foodRepository.findAll();
        foodList.sort(Comparator.comparing(Food::getName));
        for (Food food : foodList) {
            FoodPanel foodPanel = new FoodPanel(food, currentBowlingAlleyBooking, foodBookingMap);
            allIntegerFields.add(foodPanel.getFoodAmountField());
            foodPanel.getFoodAmountField().addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> {
                for (IntegerField integerField : allIntegerFields) {
                    if (integerField.getValue() > 0) {
                        panelChanges = true;
                        break;
                    } else {
                        panelChanges = false;
                    }
                }
                updateFooterButtons(panelChanges);
            });
            layout.add(foodPanel);
        }
        foodLayoutForAddItem = layout;
        return layout;
    }

    private void updateFooterButtons(boolean b) {
        panelChanges = b;
        addItemButton.setEnabled(b);
        deleteChangesButton.setEnabled(b);
    }

    /**
     * The createAlleyButtonsComponent function creates a HorizontalLayout
     * containing buttons for each BowlingAlley in the database.
     * The function also adds click listeners to each button, which change the
     * currentBowlingAlleyId and currentBowlingAlleyBooking variables.
     *
     * @return A component
     */
    private Component createAlleyButtonsComponent() {
        Scroller scroller = new Scroller();
        scroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        scroller.setMaxWidth("calc(100vw - 350px");
        VerticalLayout layout = new VerticalLayout(scroller);
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(false);
        layout.setMargin(false);

        HorizontalLayout alleyLayout = new HorizontalLayout();
        alleyLayout.setPadding(true);
        alleyLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        alleyLayout.getStyle().set("display", "inline-flex");
        scroller.setContent(alleyLayout);

        List<BowlingAlley> bowlingAlleyList = bowlingAlleyRepository.findAllByActiveIsTrue();
        if (bowlingAlleyList.isEmpty()) {
            Notifications.showError("Es sind keine Bahnen im System vorhanden.\nBitte trage Bahnen ins System ein.");
        } else {
            bowlingAlleyList.sort(Comparator.comparingInt(BowlingAlley::getId));
            long currentTime = System.currentTimeMillis();
            List<BowlingAlley> freeBowlingAlleyList = bowlingAlleyRepository
                    .findAllByNoBookingOverlapBetweenTimeStamps(currentTime, currentTime);
            Set<Integer> freeBowlingAlleyHashSet = freeBowlingAlleyList.stream().map(BowlingAlley::getId)
                    .collect(Collectors.toSet());

            List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyBookingRepository
                    .findAllByTimePeriodsOverlapping(System.currentTimeMillis());

            for (BowlingAlley bowlingAlley : bowlingAlleyList) {
                Button alleyButton = new Button("Bahn " + bowlingAlley.getId());
                buttonMap.put(bowlingAlley.getId(), alleyButton);
                alleyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                alleyButton.setEnabled(!freeBowlingAlleyHashSet.contains(bowlingAlley.getId()));
                BowlingAlleyBooking tempBowlingAlleyBooking = bowlingAlleyBookingList.stream().filter(
                        bowlingAlleyBooking -> bowlingAlleyBooking.getBowlingAlley().getId() == bowlingAlley.getId()).findFirst().orElse(null);
                if (tempBowlingAlleyBooking != null && tempBowlingAlleyBooking.isCompleted()) {
                    changeTabsCompletedBooking();
                    updateHeaderCompletedBooking();
                    alleyButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    alleyButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    goToBillButton.setEnabled(false);
                }
                alleyButton.addClickListener(buttonClickEvent -> {
                    if (tempBowlingAlleyBooking != null && tempBowlingAlleyBooking.isCompleted()) {
                        if (panelChanges) {
                            VaadinUtils.showConfirmationDialog("Änderungen sind nicht gespeichert. Trotzdem Bahn wechseln?", "Ja", "Abbrechen", () -> {
                                onCompletedButtonClick(bowlingAlleyBookingList, alleyButton);
                            });
                        } else {
                            onCompletedButtonClick(bowlingAlleyBookingList, alleyButton);
                        }
                        return;
                    }
                    if (currentBowlingAlleyBooking.getBowlingAlley().getId() == Integer.parseInt(alleyButton.getText().replaceAll("\\D+(\\d+)", "$1"))) {
                        //nichts machen, wenn auf schon ausgewählte bahn geklickt wird
                        return;
                    }
                    if (panelChanges) {
                        VaadinUtils.showConfirmationDialog("Änderungen sind nicht gespeichert. Trotzdem Bahn wechseln?", "Ja", "Abbrechen", () -> {
                            alleyButtonOnChange(bowlingAlleyBookingList, alleyButton);
                        });
                    } else {
                        alleyButtonOnChange(bowlingAlleyBookingList, alleyButton);
                    }
                });
                alleyLayout.add(alleyButton);
            }
        }
        return layout;
    }

    private void onCompletedButtonClick(List<BowlingAlleyBooking> bowlingAlleyBookingList, Button alleyButton) {
        changeTabsCompletedBooking();
        changeButtonStyleToUnselected(currentBowlingAlleyBooking.getBowlingAlley().getId());
        int currentBowlingAlleyID = Integer.parseInt(alleyButton.getText().replaceAll("\\D+(\\d+)", "$1"));
        currentBowlingAlleyBooking = bowlingAlleyBookingList.stream().filter(
                        bowlingAlleyBooking -> bowlingAlleyBooking.getBowlingAlley().getId() == currentBowlingAlleyID)
                .findFirst()
                .orElse(null);
        updateHeaderCompletedBooking();
        panelChanges = false;
        goToBillButton.setEnabled(false);
        deleteChangesButton.setEnabled(false);
        addItemButton.setEnabled(false);
    }

    private void changeTabsCompletedBooking() {
        drinkDiv.removeAll();
        foodDiv.removeAll();
        shoeDiv.removeAll();
        drinkDiv.add(new Text("Bahn wurde schon bezahlt"));
        foodDiv.add(new Text("Bahn wurde schon bezahlt"));
        shoeDiv.add(new Text("Bahn wurde schon bezahlt"));
    }

    private void alleyButtonOnChange(List<BowlingAlleyBooking> bowlingAlleyBookingList, Button alleyButton) {
        changeButtonStyleToUnselected(currentBowlingAlleyBooking.getBowlingAlley().getId());
        int currentBowlingAlleyID = Integer.parseInt(alleyButton.getText().replaceAll("\\D+(\\d+)", "$1"));
        changeButtonStyleToSelected(currentBowlingAlleyID);
        currentBowlingAlleyBooking = bowlingAlleyBookingList.stream().filter(
                        bowlingAlleyBooking -> bowlingAlleyBooking.getBowlingAlley().getId() == currentBowlingAlleyID)
                .findFirst()
                .orElse(null);
        allIntegerFields = new ArrayList<>();
        changeTabsPendingBooking();
        updateHeader();
        goToBillButton.setEnabled(true);
        deleteChangesButton.setEnabled(false);
        addItemButton.setEnabled(false);
        panelChanges = false;
    }

    /**
     * Changes the tabs in the article panel based on the current bowling alley
     * selection.
     * Necessary because there is no bowling alley selected at the start
     */
    private void changeTabsPendingBooking() {
        drinkDiv.removeAll();
        foodDiv.removeAll();
        shoeDiv.removeAll();

        drinkDiv.add(createDrinkPanel());
        foodDiv.add(createFoodPanel());
        shoeDiv.add(createShoePanel());
    }

    /**
     * Changes the style of the previous bowling alley button.
     *
     * @param currentBowlAlleyId The ID of the current bowling alley.
     */
    private void changeButtonStyleToUnselected(int currentBowlAlleyId) {
        buttonMap.get(currentBowlAlleyId).removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
    }

    /**
     * Changes the style of the current bowling alley button.
     *
     * @param currentBowlAlleyId The ID of the current bowling alley.
     */
    private void changeButtonStyleToSelected(int currentBowlAlleyId) {
        buttonMap.get(currentBowlAlleyId).addThemeVariants(ButtonVariant.LUMO_SUCCESS);
    }

    /**
     * Creates the footer buttons component.
     *
     * @return The footer buttons component.
     */
    private Component createFooterButtons() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        addItemButton = new Button("Bestellung speichern");
        goToBillButton = new Button("Zur Rechnungserstellung");
        deleteChangesButton = new Button("Änderungen verwerfen");
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteChangesButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        String bHeight = "55px";
        addItemButton.setHeight(bHeight);
        goToBillButton.setHeight(bHeight);
        deleteChangesButton.setHeight(bHeight);
        layout.expand(addItemButton, deleteChangesButton, goToBillButton);

        addItemButton.addClickListener(buttonClickEvent -> {
            addAllNewDrinkBookings();
            addAllNewFoodBookings();
            addAllNewShoeBookings();
            changeTabsPendingBooking();
            panelChanges = false;
            deleteChangesButton.setEnabled(false);
            addItemButton.setEnabled(false);
            Notifications.showInfo("Buchungen wurden gespeichert");
        });

        goToBillButton.addClickListener(buttonClickEvent -> {
            addAllNewDrinkBookings();
            addAllNewFoodBookings();
            addAllNewShoeBookings();
            VaadinUtils.showConfirmationDialog("Rechnung bezahlen?", "Ja", "Abbrechen", () -> {
                UI.getCurrent().navigate(InvoiceView.class, currentBowlingAlleyBooking.getId());
            });
        });

        deleteChangesButton.addClickListener(buttonClickEvent -> {
            drinkLayoutForAddItem.getChildren().forEach(component -> {
                if (component instanceof DrinkPanel drinkPanel) {
                    drinkPanel.resetIntegerField();
                }
            });
            foodLayoutForAddItem.getChildren().forEach(component -> {
                if (component instanceof FoodPanel foodPanel) {
                    foodPanel.resetFoodAmountFieldValue();
                }
            });
            shoePanel.resetIntegerField();
            panelChanges = false;
            deleteChangesButton.setEnabled(false);
            addItemButton.setEnabled(false);
        });

        layout.add(deleteChangesButton, addItemButton, goToBillButton);
        return layout;
    }

    /**
     * Adds all new shoe bookings for the current bowling alley booking.
     */
    private void addAllNewShoeBookings() {
        int shoeSize = shoePanel.getShoeSizeField().getValue();
        int amount = shoePanel.getShoeAmountField().getValue();
        int stock = shoePanel.getShoeSizeAmountMap().get(shoeSize) - amount;
        if (stock < 0) {
            Notifications.showError("Nicht genügend Schuhe in Größe: " + shoeSize);
            return;
        }
        for (int i = 0; i < amount; i++) {
            Optional<BowlingShoe> bowlingShoeOptional = bowlingShoeRepository
                    .findFirstBySizeEqualsAndActiveIsTrueAndClientIsNull(shoeSize);
            if (bowlingShoeOptional.isPresent()) {
                BowlingShoe bowlingShoe = bowlingShoeOptional.get();
                bowlingShoeRepository.updateClientById(bowlingShoe.getId(),
                        currentBowlingAlleyBooking.getClient());
                BowlingShoeBooking bowlingShoeBooking = new BowlingShoeBooking();
                bowlingShoeBooking.setBowlingAlley(currentBowlingAlleyBooking.getBowlingAlley());
                bowlingShoeBooking.setClient(currentBowlingAlleyBooking.getClient());
                bowlingShoeBooking.setTimeStamp(currentBowlingAlleyBooking.getStartTime());
                bowlingShoeBooking.setBowlingShoe(bowlingShoe);
                bowlingShoeBooking.setPrice(bowlingCenter.getBowlingShoePrice());
                bowlingShoeBookingRepository.save(bowlingShoeBooking);
            } else {
                Notifications.showError("Fehler: Keine Schuhe mit Größe " + shoeSize + " frei!");
                break;
            }
        }
        shoePanel.updateShoeSizeAmountMap();
        shoePanel.resetIntegerField();
    }

    /**
     * The addAllNewDrinkBookings function adds all new drink bookings to the
     * database.
     * It does this by first getting a list of all drink bookings from the database,
     * and then mapping them into a map.
     * Then it iterates over each entry in the local drinkBookingMap, which contains
     * all of our current DrinkBooking objects.
     * For each entry in that map, we get its corresponding Drink object from the
     * database using its name as an identifier. We then subtract
     * booking's amount times booking's ml (the amount of milliliters) from that
     * Drink object's stockInMilliliters field to determine how much of that Drink
     * is booked.
     */
    private void addAllNewDrinkBookings() {
        List<DrinkBooking> drinkBookingList = drinkBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(currentBowlingAlleyBooking.getClient(),
                        currentBowlingAlleyBooking.getBowlingAlley(),
                        currentBowlingAlleyBooking.getStartTime());
        Map<String, DrinkBooking> drinkBookingMapFromDB = drinkBookingList.stream()
                .collect(Collectors.toMap(DrinkBooking::getName, Function.identity()));

        drinkBookingMap.forEach((name, booking) -> {
            // Drink über name rausholen und dann vergleichen wie viel noch da ist
            Drink drink = drinkRepository.findByName(booking.getDrinkName());
            int newStock = drink.getStockInMilliliters() - booking.getAmount() * booking.getMl();
            if (newStock < 0) {
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
        drinkLayoutForAddItem.getChildren().forEach(component -> {
            if (component instanceof DrinkPanel drinkPanel) {
                drinkPanel.resetIntegerField();
            }
        });

        drinkBookingMap.clear();
    }

    /**
     * The addAllNewFoodBookings function adds all new food bookings to the
     * database.
     * It does this by first getting a list of all food bookings from the database
     * that match the current bowling alley booking's client, bowling alley, and
     * start time.
     * Then it creates a map of those food bookings using their names as keys and
     * themselves as values.
     * Next it iterates over each entry in its own map (foodBookingMap) which
     * contains FoodBooking objects with information about how many items were
     * ordered for each type of food item. For each entry in this map:
     * 1) The function gets the selectedFood object from the database using its
     * name as an identifier.
     * 2) The function subtracts the amount of the FoodBooking object from the
     * selectedFood object's stock field.
     * 3) If the selectedFood object's stock field is less than 0, the function
     * shows an error message and returns.
     * 4) If the selectedFood object's stock field is greater than 0, the
     * function updates the selectedFood object's stock field and saves it to the
     * database.
     * 5) If the selectedFood object's stock field is 0, the function saves the
     * FoodBooking object to the database.
     * 6) If the selectedFood object's stock field is greater than 0, the function
     * updates the FoodBooking object's amount field and saves it to the database.
     * 7) If the selectedFood object's stock field is 0, the function saves the
     * FoodBooking object to the database.
     * 8) The function clears the local foodBookingMap.
     */
    private void addAllNewFoodBookings() {
        List<FoodBooking> foodBookingList = foodBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(currentBowlingAlleyBooking.getClient(),
                        currentBowlingAlleyBooking.getBowlingAlley(),
                        currentBowlingAlleyBooking.getStartTime());
        Map<String, FoodBooking> foodBookingMapFromDB = foodBookingList.stream()
                .collect(Collectors.toMap(FoodBooking::getName, Function.identity()));
        foodBookingMap.forEach((name, booking) -> {
            // Food über name rausholen und dann vergleichen wie viel noch da ist
            Food selectedFood = foodRepository.findByName(booking.getName());
            int newStock = selectedFood.getStock() - booking.getAmount();
            if (newStock < 0) {
                Notifications.showError("Nicht genügend von der Speise: " + selectedFood.getName());
                return;
            } else {
                selectedFood.setStock(newStock);
                foodRepository.updateStockById(selectedFood.getId(), newStock);
            }
            if (foodBookingMapFromDB.containsKey(name)) {
                FoodBooking foodBooking = foodBookingMapFromDB.get(name);
                booking.setAmount(booking.getAmount() + foodBooking.getAmount());
            }
            foodBookingRepository.save(booking);
        });
        foodLayoutForAddItem.getChildren().forEach(component -> {
            if (component instanceof FoodPanel foodPanel) {
                foodPanel.resetFoodAmountFieldValue();
            }
        });
        foodBookingMap.clear();
    }

    /**
     * The setParameter function is called by the framework when a new instance of
     * this view is created.
     * It receives an event and an optional parameter, which in our case will be the
     * booking ID.
     * If there's no parameter, we simply return from the function without doing
     * anything else.
     * Otherwise, we try to find a booking with that ID in our database and if it
     * exists and is active (not expired),
     * then we set it as our currentBowlingAlleyBooking variable so that other
     * functions can access it later on.&lt;/code&gt;
     *
     * @param event     Get the current url
     * @param parameter Pass the parameter from the url to this function
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer parameter) {
        if (parameter == null) {
            List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyBookingRepository
                    .findAllByTimePeriodsOverlapping(System.currentTimeMillis());

            if (bowlingAlleyBookingList.isEmpty()) {
                Notifications.showError("Es sind zurzeit keine Bahnen gebucht");
            }

            for (Button button : buttonMap.values()) {
                if (button.isEnabled()) {
                    button.setAutofocus(true);
                    int currentBowlingAlleyId = Integer.parseInt(button.getText().replaceAll("\\D+(\\d+)", "$1"));
                    changeButtonStyleToSelected(currentBowlingAlleyId);
                    currentBowlingAlleyBooking = bowlingAlleyBookingList.stream().filter(
                                    bowlingAlleyBooking -> bowlingAlleyBooking.getBowlingAlley().getId() == currentBowlingAlleyId)
                            .findFirst()
                            .orElse(null);
                    changeTabsPendingBooking();
                    updateHeader();
                    if (currentBowlingAlleyBooking != null && currentBowlingAlleyBooking.isCompleted()) {
                        button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
                        goToBillButton.setEnabled(false);
                        changeTabsCompletedBooking();
                        updateHeaderCompletedBooking();
                    } else {
                        goToBillButton.setEnabled(true);
                    }
                    break;
                }
            }
        } else {
            Optional<BowlingAlleyBooking> bowlingAlleyBookingOptional = bowlingAlleyBookingRepository.findById(parameter);
            bowlingAlleyBookingOptional.ifPresent(booking -> {
                if (booking.isActive() && !booking.isCompleted()) {
                    this.currentBowlingAlleyBooking = booking;
                    int currentBowlingAlleyId = booking.getBowlingAlley().getId();
                    buttonMap.get(currentBowlingAlleyId).setAutofocus(true);
                    changeButtonStyleToUnselected(currentBowlingAlleyId);
                    changeButtonStyleToSelected(currentBowlingAlleyId);
                    changeTabsPendingBooking();
                    updateHeader();
                    goToBillButton.setEnabled(true);
                }
            });
        }
    }
}