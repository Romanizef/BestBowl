package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlley;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.Food;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodRepository;
import de.softwareprojekt.bestbowl.utils.VaadinUtils;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.extrasElements.DrinkPanel;
import de.softwareprojekt.bestbowl.views.extrasElements.FoodPanel;
import de.softwareprojekt.bestbowl.views.extrasElements.ShoePanel;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
@Route(value = "extras", layout = MainView.class)
@PageTitle("Extras")
@PermitAll
public class ExtrasView extends VerticalLayout {
    private final DrinkRepository drinkRepository;
    private final FoodRepository foodRepository;

    private BowlingAlley bowlingAlley;

    private BowlingAlleyRepository bowlingAlleyRepository;
    private BowlingAlleyBooking bowlingAlleyBooking;
    private HorizontalLayout alleyLayout;
    private HorizontalLayout tabLayout;
    private TabSheet tabs;


    @Autowired
    public ExtrasView(DrinkRepository drinkRepository, FoodRepository foodRepository, BowlingAlleyRepository bowlingAlleyRepository) {
        this.bowlingAlleyRepository = bowlingAlleyRepository;
        this.drinkRepository = drinkRepository;
        this.foodRepository = foodRepository;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        Component alleyButtonsComponent = createAlleyButtonsComponent();
        Component articlePanelComponent = createArticlePanelComponent();
        Component footerButtons = createFooterButtons();
        add(alleyButtonsComponent, articlePanelComponent, footerButtons);
    }


    private Component createArticlePanelComponent() {
        tabs = new TabSheet();
        tabLayout = new HorizontalLayout();
        Tab drink = new Tab(VaadinIcon.COFFEE.create(), new Span("Getränke"));
        Tab food = new Tab(VaadinIcon.CROSS_CUTLERY.create(), new Span("Speisen"));
        Tab shoe = new Tab(VaadinIcon.RETWEET.create(), new Span("Schuhe"));

        tabs.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabs.add(drink, createDrinkPanel());
        tabs.add(food, createFoodPanel());
        tabs.add(shoe, createShoePanel());
        tabLayout.add(tabs);
        return tabLayout;
    }


    private Component createShoePanel() {
        ShoePanel shoePanel = new ShoePanel();

        return shoePanel;
    }


    private Component createDrinkPanel() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setWidthFull();
        List<Drink> drinkList = drinkRepository.findAll();

        for (Drink drink : drinkList) {
            verticalLayout.add(new DrinkPanel(drink));
        }
        verticalLayout.setMaxHeight("400px");
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
        return formLayout;
    }

    private final Component createAlleyButtonsComponent() {
        alleyLayout = new HorizontalLayout();
        alleyLayout.setPadding(true);
        alleyLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        // ToDo Buttons Einzeln erzeugen

        List<BowlingAlley> bowlingAlleyList = bowlingAlleyRepository.findAll();
        bowlingAlleyList.sort(Comparator.comparingInt(BowlingAlley::getId));
        long currentTime = System.currentTimeMillis();
        List<BowlingAlley> freeBowlingAlleyList = bowlingAlleyRepository.findAllByNoBookingOverlapBetweenTimeStamps(currentTime, currentTime);
        Set<Integer> freeBowlingAlleyHashSet = freeBowlingAlleyList.stream().map(BowlingAlley::getId).collect(Collectors.toSet());

        for (BowlingAlley bowlingAlley : bowlingAlleyList) {
            Button bahn = new Button("Bahn " + bowlingAlley.getId());
            bahn.setEnabled(!freeBowlingAlleyHashSet.contains(bowlingAlley.getId()));
            alleyLayout.add(bahn);

        }

        return alleyLayout;
    }

    private Component createFooterButtons() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        Button addItem = new Button("Hinzufügen");
        Button goToBill = new Button("Bezahlen");
        addItem.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItem.setWidth("55%");
        String bHeight = "55px";
        addItem.setHeight(bHeight);
        goToBill.setWidth("40%");
        goToBill.setHeight(bHeight);

        //ToDo Dialog Fenster "Sind Sie sicher das sie Bezahlen wollen?"

        addItem.addClickListener(buttonClickEvent -> {
        });

        goToBill.addClickListener(buttonClickEvent -> {
            VaadinUtils.showConfirmationDialog("Alles Gut?", "Ja Suppi", "Bruda nein", () -> {
                UI.getCurrent().navigate(InvoiceView.class).ifPresent(view -> view.setBowlingAlleyBooking(bowlingAlleyBooking));
            });
        });

        layout.add(addItem, goToBill);
        return layout;
    }

    public void setBowlingAlleyBooking(BowlingAlleyBooking bowlingAlleyBooking) {
        //ToDo hier noch angeben, welche Bahn die mit Bahn Button übergeben wird
        this.bowlingAlleyBooking = bowlingAlleyBooking;
    }
}
