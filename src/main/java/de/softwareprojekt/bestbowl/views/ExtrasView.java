package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
/*
*   @author Ali Cicek
 */

@Route(value = "extras", layout = MainView.class)
@PageTitle("Extras")
@PermitAll

public class ExtrasView  extends VerticalLayout {

    private final DrinkRepository drinkRepository;

    private final DrinkBookingRepository drinkBookingRepository;

    private final FoodRepository foodRepository;

    private final FoodBookingRepository foodBookingRepository;

    private MenuBar bahnMenubar;

    private ButtonLayout bahnLayout;



    @Autowired
    public ExtrasView(DrinkRepository drinkRepository, DrinkBookingRepository drinkBookingRepository, FoodRepository foodRepository, FoodBookingRepository foodBookingRepository) {
        this.drinkRepository = drinkRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodRepository = foodRepository;
        this.foodBookingRepository = foodBookingRepository;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        bahnMenubar = createMenubar();

        Button b = new Button("TEst");
        add(bahnMenubar, b, bahnLayout);
    }


    /*
    public void createBahnLayout(){
        HorizontalLayout row1 = new HorizontalLayout();
        for (int i = 1; i <= 6; i++) {
            Button button = new Button("Bahn " + i);
            row1.add(button);
        }

        HorizontalLayout row2 = new HorizontalLayout();
        for (int i = 7; i <= 12; i++) {
            Button button = new Button("Button " + i);
            row2.add(button);
        }


        bahnLayout.add(row1,row2);
    }

    *
     */

    public MenuBar createMenubar() {
        MenuBar menuBar = new MenuBar();
        Text selected = new Text("");
        ComponentEventListener<ClickEvent<MenuItem>> listener = e -> selected
                .setText(e.getSource().getText());
        Div message = new Div(new Text("Clicked item: "), selected);

        menuBar.addItem("View", listener);
        menuBar.addItem("Edit", listener);

        MenuItem share = menuBar.addItem("Share");
        SubMenu shareSubMenu = share.getSubMenu();
        MenuItem onSocialMedia = shareSubMenu.addItem("On social media");
        SubMenu socialMediaSubMenu = onSocialMedia.getSubMenu();
        socialMediaSubMenu.addItem("Facebook", listener);
        socialMediaSubMenu.addItem("Twitter", listener);
        socialMediaSubMenu.addItem("Instagram", listener);
        shareSubMenu.addItem("By email", listener);
        shareSubMenu.addItem("Get Link", listener);

        MenuItem move = menuBar.addItem("Move");
        SubMenu moveSubMenu = move.getSubMenu();
        moveSubMenu.addItem("To folder", listener);
        moveSubMenu.addItem("To trash", listener);

        menuBar.addItem("Duplicate", listener);
        return menuBar;
    }





}
