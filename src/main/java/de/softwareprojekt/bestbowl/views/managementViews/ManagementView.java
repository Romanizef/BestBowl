package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Ali
 */
@Route(value = "verwaltungen", layout = MainView.class)
@PageTitle("Verwaltungen")
@RolesAllowed({ UserRole.OWNER, UserRole.ADMIN })
public class ManagementView extends VerticalLayout {

    private static final String VERWALTUNG = "verwaltung";
    private final String width;
    private final String height;

    /**
     * The ManagementView function is the main view for the management of all data
     * in the database.
     * It contains buttons to navigate to other views, which allow you to manage
     * specific parts of your data.
     * 
     * @see #createAlleyRowButtonLayout()
     * @see #createClientAndOrgRowButtonLayout()
     * @see #createDrinkRowButtonLayout()
     * @see #createFoodAndShoeRowButtonLayout()
     * @see #createUserAndCenterRowButtonLayout()
     */
    @Autowired
    public ManagementView() {
        width = "300px";
        height = "55px";
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addCSS();

        HorizontalLayout alleyRowButtonLayout = createAlleyRowButtonLayout();
        HorizontalLayout clientAndOrgRowButtonLayout = createClientAndOrgRowButtonLayout();
        HorizontalLayout drinkRowButtonLayout = createDrinkRowButtonLayout();
        HorizontalLayout foodAndShoeRowButtonLayout = createFoodAndShoeRowButtonLayout();
        HorizontalLayout userAndCenterRowButtonLayout = createUserAndCenterRowButtonLayout();
        // Todo Tabelle mit Namen und Bestand für Getränke und Speisen die unter dem
        // Meldebestand gefallen sind, nur aktive

        add(alleyRowButtonLayout, clientAndOrgRowButtonLayout, userAndCenterRowButtonLayout, drinkRowButtonLayout,
                foodAndShoeRowButtonLayout);
    }

    /**
     * The createAlleyRowButtonLayout function creates a HorizontalLayout containing
     * two buttons.
     * The first button navigates to the AlleyBookingView, while the second button
     * navigates to the AlleyManagementView.
     * 
     * @return A horizontallayout
     */
    private HorizontalLayout createAlleyRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        /*
         * Button alleyBookingViewButton = new Button("Bahn Buchungs" + VERWALTUNG);
         * alleyBookingViewButton.addClickListener(buttonClickEvent ->
         * UI.getCurrent().navigate(AlleyBookingView.class));
         */
        Button alleyManagementViewButton = new Button("Bahn" + VERWALTUNG);
        alleyManagementViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AlleyManagementView.class));
        /*
         * alleyBookingViewButton.setWidth(width);
         * alleyBookingViewButton.setHeight(height);
         */
        alleyManagementViewButton.setWidth(width);
        alleyManagementViewButton.setHeight(height);
        buttonLayout.add(alleyManagementViewButton);
        return buttonLayout;
    }

    /**
     * The createClientAndOrgRowButtonLayout function creates a HorizontalLayout
     * containing two buttons.
     * The first button navigates to the ClientManagementView, while the second one
     * navigates to the AssociationManagementView.
     * 
     * @return A horizontallayout
     */
    private HorizontalLayout createClientAndOrgRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button clientManagementViewButton = new Button("Kunden" + VERWALTUNG);
        clientManagementViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(ClientManagementView.class));
        Button associationManagmentViewButton = new Button("Vereins" + VERWALTUNG);
        associationManagmentViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AssociationManagementView.class));
        clientManagementViewButton.setWidth(width);
        clientManagementViewButton.setHeight(height);
        associationManagmentViewButton.setWidth(width);
        associationManagmentViewButton.setHeight(height);
        buttonLayout.add(clientManagementViewButton, associationManagmentViewButton);
        return buttonLayout;
    }

    /**
     * The createDrinkRowButtonLayout function creates a HorizontalLayout containing
     * two buttons.
     * The first button navigates to the DrinkManagementView, while the second one
     * navigates to the DrinkVariantManagementView.
     * 
     * @return A horizontallayout
     */
    private HorizontalLayout createDrinkRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button drinkViewButton = new Button("Getränke" + VERWALTUNG);
        drinkViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(DrinkManagementView.class));
        Button drinkVariantViewButton = new Button("Getränkevarianten" + VERWALTUNG);
        drinkVariantViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(DrinkVariantManagementView.class));
        drinkViewButton.setWidth(width);
        drinkViewButton.setHeight(height);
        drinkVariantViewButton.setWidth(width);
        drinkVariantViewButton.setHeight(height);
        buttonLayout.add(drinkViewButton, drinkVariantViewButton);
        return buttonLayout;
    }

    /**
     * The createFoodAndShoeRowButtonLayout function creates a HorizontalLayout
     * containing two buttons, one for the FoodManagementView and one for the
     * BowlingShoeManagementView.
     * 
     * @return A horizontallayout
     */
    private HorizontalLayout createFoodAndShoeRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button foodViewButton = new Button("Speise" + VERWALTUNG);
        foodViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(FoodManagementView.class));
        Button bowlingShoeViewButton = new Button("Schuh" + VERWALTUNG);
        bowlingShoeViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(BowlingShoeManagementView.class));
        foodViewButton.setWidth(width);
        foodViewButton.setHeight(height);
        bowlingShoeViewButton.setWidth(width);
        bowlingShoeViewButton.setHeight(height);
        buttonLayout.add(foodViewButton, bowlingShoeViewButton);
        return buttonLayout;
    }

    /**
     * The createUserAndCenterRowButtonLayout function creates a HorizontalLayout
     * containing two buttons.
     * The first button navigates to the UserManagementView, while the second one
     * navigates to the BowlingCenterManagementView.
     * 
     * @return A horizontallayout
     */
    private HorizontalLayout createUserAndCenterRowButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button userManagementViewButton = new Button("Benutzer" + VERWALTUNG);
        userManagementViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(UserManagementView.class));
        Button bowlingCenterButton = new Button("Bowlingcenter Verwaltung");
        bowlingCenterButton.addClickListener(e -> UI.getCurrent().navigate(BowlingCenterManagementView.class));
        buttonLayout.add(userManagementViewButton, bowlingCenterButton);
        userManagementViewButton.setWidth(width);
        userManagementViewButton.setHeight(height);
        bowlingCenterButton.setWidth(width);
        bowlingCenterButton.setHeight(height);
        return buttonLayout;
    }

    /**
     * The addCSS function adds a CSS border to the component.
     */
    private void addCSS() {
        // getStyle().set("border", "2px solid blue");
    }
}