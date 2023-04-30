package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.views.extrasElements.FoodPanel;
import de.softwareprojekt.bestbowl.views.extrasElements.ShoePanel;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * @author Matija Kopschek
 * @author Ali aus Mali
 */
@Route(value = "extras", layout = MainView.class)
@PageTitle("Extras")
@PermitAll
public class ExtrasView extends VerticalLayout {
    private HorizontalLayout alleyLayout;
    private HorizontalLayout tabLayout;
    private TabSheet tabs;
    private ShoePanel shoePanel;
    private FoodPanel foodPanel;

    @Autowired
    public ExtrasView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        Component alleyComponent = createBahnComponent();
        shoePanel = new ShoePanel();
        foodPanel = new FoodPanel();
        Component articlePanelComponent = createArticlePanelComponent();
        add(alleyComponent, articlePanelComponent);
    }

    private Component createArticlePanelComponent() {
        tabs = new TabSheet();
        tabLayout = new HorizontalLayout();
        tabLayout.setMaxWidth("100%");
        Tab drink = new Tab(VaadinIcon.COFFEE.create(), new Span("Getränke"));
        Tab food = new Tab(VaadinIcon.CROSS_CUTLERY.create(), new Span("Speisen"));
        Tab shoe = new Tab(VaadinIcon.BELL.create(), new Span("Schuhe"));

        tabs.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED,
                TabSheetVariant.MATERIAL_BORDERED,
                TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabs.add(drink, new Div(new Text("This is the Getränke tab content")));
        tabs.add(food, foodPanel.addPanelComponent());
        tabs.add(shoe, shoePanel.addPanelComponent());
        tabLayout.add(tabs);
        return tabLayout;
    }

    private final Component createBahnComponent() {
        alleyLayout = new HorizontalLayout();
        alleyLayout.setPadding(true);
        alleyLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        // ToDo Buttons Einzeln erzeugen
        alleyLayout.add(new Button("Bahn 1"));
        alleyLayout.add(new Button("Bahn 2"));
        alleyLayout.add(new Button("Bahn 3"));
        return alleyLayout;
    }

    /*
     * DrinkRepository drinkRepository, DrinkBookingRepository
     * drinkBookingRepository, FoodRepository foodRepository, FoodBookingRepository
     * foodBookingRepository
     * this.drinkRepository = drinkRepository;
     * this.drinkBookingRepository = drinkBookingRepository;
     * this.foodRepository = foodRepository;
     * this.foodBookingRepository = foodBookingRepository;
     *
     * private final DrinkRepository drinkRepository;
     * 
     * private final DrinkBookingRepository drinkBookingRepository;
     * 
     * private final FoodRepository foodRepository;
     * 
     * private final FoodBookingRepository foodBookingRepository;
     *
     *
     *
     */
}