package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.bookingViews.AlleyBookingView;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Ali
 */
@Route(value = "verwaltungen", layout = MainView.class)
@PageTitle("Verwaltungen")
@PermitAll
public class ManagementView extends VerticalLayout {

    private static final String VERWALTUNG = "verwaltung";
    private String width;

    private String heigth;

    @Autowired
    public ManagementView() {
        width = "300px";
        heigth = "55px";
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addCSS();

        HorizontalLayout alleyRowButtonLayout = createAlleyRowButtonLayout();
        HorizontalLayout clientAndOrgRowButtonLayout = createClientAndOrgRowButtonLayout();
        HorizontalLayout drinkRowButtonLayout = createDrinkRowButtonLayout();
        HorizontalLayout foodAndShoeRowButtonLayout = createFoodAndShoeRowButtonLayout();
        HorizontalLayout userAndCenterRowButtonLayout = createUserAndCenterRowButtonLayout();

        add(alleyRowButtonLayout, clientAndOrgRowButtonLayout,userAndCenterRowButtonLayout, drinkRowButtonLayout, foodAndShoeRowButtonLayout);
    }

    private HorizontalLayout createAlleyRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button alleyBookingViewButton = new Button("Bahn Buchungs" + VERWALTUNG);
        alleyBookingViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AlleyBookingView.class));
        Button alleyManagementViewButton = new Button("Bahn" + VERWALTUNG);
        alleyManagementViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AlleyManagementView.class));
        alleyBookingViewButton.setWidth(width);
        alleyBookingViewButton.setHeight(heigth);
        alleyManagementViewButton.setWidth(width);
        alleyManagementViewButton.setHeight(heigth);
        buttonLayout.add(alleyBookingViewButton, alleyManagementViewButton);
        return buttonLayout;
    }

    private HorizontalLayout createClientAndOrgRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button clientManagementViewButton = new Button("Kunden" + VERWALTUNG);
        clientManagementViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(ClientManagementView.class));
        Button associationManagmentViewButton = new Button("Vereins" + VERWALTUNG);
        associationManagmentViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AssociationManagementView.class));
        clientManagementViewButton.setWidth(width);
        clientManagementViewButton.setHeight(heigth);
        associationManagmentViewButton.setWidth(width);
        associationManagmentViewButton.setHeight(heigth);
        buttonLayout.add(clientManagementViewButton, associationManagmentViewButton);
        return buttonLayout;
    }

    private HorizontalLayout createDrinkRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button drinkViewButton = new Button("Getränke" + VERWALTUNG);
        drinkViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(DrinkManagementView.class));
        Button drinkVariantViewButton = new Button("Getränkevarianten" + VERWALTUNG);
        drinkVariantViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(DrinkVariantManagementView.class));
        drinkViewButton.setWidth(width);
        drinkViewButton.setHeight(heigth);
        drinkVariantViewButton.setWidth(width);
        drinkVariantViewButton.setHeight(heigth);
        buttonLayout.add(drinkViewButton, drinkVariantViewButton);
        return buttonLayout;
    }

    private HorizontalLayout createFoodAndShoeRowButtonLayout(){
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button foodViewButton = new Button("Speise" + VERWALTUNG);
        foodViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(FoodManagementView.class));
        Button bowlingShoeViewButton = new Button("Schuh" + VERWALTUNG);
        bowlingShoeViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(BowlingShoeManagementView.class));
        foodViewButton.setWidth(width);
        foodViewButton.setHeight(heigth);
        bowlingShoeViewButton.setWidth(width);
        bowlingShoeViewButton.setHeight(heigth);
        buttonLayout.add(foodViewButton,bowlingShoeViewButton);
        return buttonLayout;
    }

    private HorizontalLayout createUserAndCenterRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button userManagementViewButton = new Button("Nutzer" + VERWALTUNG);
        userManagementViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(UserManagementView.class));
        Button bowlingCenterButton = new Button("Bowlingcenter Verwaltung");
        bowlingCenterButton.addClickListener(e -> UI.getCurrent().navigate(BowlingCenterManagementView.class));
        buttonLayout.add(userManagementViewButton, bowlingCenterButton);
        userManagementViewButton.setWidth(width);
        userManagementViewButton.setHeight(heigth);
        bowlingCenterButton.setWidth(width);
        bowlingCenterButton.setHeight(heigth);
        return buttonLayout;
    }



    private void addCSS() {
        //getStyle().set("border", "2px solid blue");
    }
}
