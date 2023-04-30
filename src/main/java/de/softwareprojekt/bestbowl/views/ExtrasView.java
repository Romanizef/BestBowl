package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodRepository;
import de.softwareprojekt.bestbowl.views.extrasElements.ButtonLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.SubMenu;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

/*
*   @author Ali Cicek
 */

@Route(value = "extras", layout = MainView.class)
@PageTitle("Extras")
@PermitAll

public class ExtrasView  extends VerticalLayout {


    private MenuBar bahnMenubar;

    private ButtonLayout bahnLayout;



    @Autowired
    public ExtrasView() {

        setSizeFull();
        setAlignItems(Alignment.CENTER);



        Button b = new Button("TEst");
        add( b, layout);
    }

    public Layout createBahnLayout(){
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(new Button("Button 1"));
        layout.add(new Button("Button 2"));
        layout.add(new Button("Button 3"));
    }


/*
* DrinkRepository drinkRepository, DrinkBookingRepository drinkBookingRepository, FoodRepository foodRepository, FoodBookingRepository foodBookingRepository
*   this.drinkRepository = drinkRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodRepository = foodRepository;
        this.foodBookingRepository = foodBookingRepository;
*
*     private final DrinkRepository drinkRepository;

    private final DrinkBookingRepository drinkBookingRepository;

    private final FoodRepository foodRepository;

    private final FoodBookingRepository foodBookingRepository;
*
*
*
* */





}
