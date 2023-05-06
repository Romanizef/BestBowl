package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyRepository;
import de.softwareprojekt.bestbowl.views.managementViews.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * @Author Ali
 */
@Route(value = "verwaltungen", layout = MainView.class)
@PageTitle("Verwaltungen")
@PermitAll
public class ManagementView extends VerticalLayout {

    private static String v = "verwaltung";

    @Autowired
    public ManagementView(){
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addCSS();

        HorizontalLayout firstRowButtonLayout =  createFirstRowButtonLayout();
        HorizontalLayout secondRowButtonLayout = createSecondRowButtonLayout();
        HorizontalLayout thirdRowButtonLayout = createThirdRowButtonLayout();

        add(firstRowButtonLayout, secondRowButtonLayout, thirdRowButtonLayout);
    }

    private HorizontalLayout createFirstRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button alleyBookingViewButton = new Button("Bahn Buchungs" +v);
        alleyBookingViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AlleyBookingView.class));
        Button alleyManagementViewButton = new Button("Bahn"+v);
        alleyManagementViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AlleyManagementView.class));
        Button associationManagmentViewButton = new Button("Vereins"+v);
        associationManagmentViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AssociationManagementView.class));
        Button userManagementViewButton = new Button("Nutzer"+v);
        userManagementViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(UserManagementView.class));
        buttonLayout.add(alleyBookingViewButton,alleyManagementViewButton,associationManagmentViewButton,userManagementViewButton);
        return buttonLayout;
    }

    private HorizontalLayout createSecondRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button clientManagementViewButton = new Button("Kunden"+v);
        clientManagementViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(ClientManagementView.class));
        Button articleManagementViewButton = new Button("Artikel"+v);
        articleManagementViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(ArticleManagementView.class));
        Button bowlingShoeViewButton = new Button("Schuh"+v);
        bowlingShoeViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(BowlingShoeView.class));
        buttonLayout.add(clientManagementViewButton,articleManagementViewButton,bowlingShoeViewButton);
        return buttonLayout;
    }

    private HorizontalLayout createThirdRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button drinkViewButton = new Button("Getränke"+v);
        drinkViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(DrinkView.class));
        Button drinkVariantViewButton = new Button("Getränke Varianten"+v);
        drinkVariantViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(DrinkVariantView.class));
        Button foodViewButton = new Button("Speise"+v);
        foodViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(FoodView.class));
        buttonLayout.add(drinkViewButton,drinkVariantViewButton,foodViewButton);
        return buttonLayout;
    }

    private void addCSS() {
        //getStyle().set("border", "2px solid blue");
    }

}
