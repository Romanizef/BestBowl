package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.beans.ReorderService;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.records.ReorderEntry;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Ali
 */
@Route(value = "verwaltungen", layout = MainView.class)
@PageTitle("Verwaltungen")
@RolesAllowed({UserRole.OWNER})
public class ManagementView extends VerticalLayout {
    private static final String VERWALTUNG = "verwaltung";
    private static final String WIDTH = "300px";
    private static final String HEIGHT = "55px";
    private final transient ReorderService reorderService;

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
    public ManagementView(ReorderService reorderService) {
        this.reorderService = reorderService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        HorizontalLayout alleyRowButtonLayout = createAlleyRowButtonLayout();
        HorizontalLayout clientAndOrgRowButtonLayout = createClientAndOrgRowButtonLayout();
        HorizontalLayout userAndCenterRowButtonLayout = createUserAndCenterRowButtonLayout();
        HorizontalLayout drinkRowButtonLayout = createDrinkRowButtonLayout();
        HorizontalLayout foodAndShoeRowButtonLayout = createFoodAndShoeRowButtonLayout();

        VerticalLayout reorderLayout = createReorderLayout();

        add(alleyRowButtonLayout, clientAndOrgRowButtonLayout, userAndCenterRowButtonLayout, drinkRowButtonLayout,
                foodAndShoeRowButtonLayout, reorderLayout);
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
        Button alleyManagementViewButton = new Button("Bahn" + VERWALTUNG);
        alleyManagementViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(AlleyManagementView.class));
        alleyManagementViewButton.setWidth(WIDTH);
        alleyManagementViewButton.setHeight(HEIGHT);
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
        clientManagementViewButton.setWidth(WIDTH);
        clientManagementViewButton.setHeight(HEIGHT);
        associationManagmentViewButton.setWidth(WIDTH);
        associationManagmentViewButton.setHeight(HEIGHT);
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
        drinkViewButton.setWidth(WIDTH);
        drinkViewButton.setHeight(HEIGHT);
        drinkVariantViewButton.setWidth(WIDTH);
        drinkVariantViewButton.setHeight(HEIGHT);
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
        Button foodViewButton = new Button("Speisen" + VERWALTUNG);
        foodViewButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(FoodManagementView.class));
        Button bowlingShoeViewButton = new Button("Schuh" + VERWALTUNG);
        bowlingShoeViewButton
                .addClickListener(buttonClickEvent -> UI.getCurrent().navigate(BowlingShoeManagementView.class));
        foodViewButton.setWidth(WIDTH);
        foodViewButton.setHeight(HEIGHT);
        bowlingShoeViewButton.setWidth(WIDTH);
        bowlingShoeViewButton.setHeight(HEIGHT);
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
        Button bowlingCenterButton = new Button("Bowlingcenter" + VERWALTUNG);
        bowlingCenterButton.addClickListener(e -> UI.getCurrent().navigate(BowlingCenterManagementView.class));
        buttonLayout.add(userManagementViewButton, bowlingCenterButton);
        userManagementViewButton.setWidth(WIDTH);
        userManagementViewButton.setHeight(HEIGHT);
        bowlingCenterButton.setWidth(WIDTH);
        bowlingCenterButton.setHeight(HEIGHT);
        return buttonLayout;
    }

    private VerticalLayout createReorderLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.add(new H2("Meldebestand Warnungen"));
        List<ReorderEntry> reorderEntryList = reorderService.getReorderEntryList();
        if (reorderEntryList.isEmpty()) {
            verticalLayout.add(new Text("aktuell keine Warnung"));
        } else {
            Grid<ReorderEntry> grid = new Grid<>(ReorderEntry.class, false);
            grid.setSizeFull();
            grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
            grid.addColumn(ReorderEntry::articleType).setHeader("Artikeltyp").setSortable(true);
            grid.addColumn(ReorderEntry::name).setHeader("Name").setSortable(true);
            grid.addColumn(ReorderEntry::stock).setHeader("Bestand");
            grid.addColumn(ReorderEntry::reorderPoint).setHeader("Meldebestand");
            grid.getColumns().forEach(c -> c.setResizable(true));
            GridListDataView<ReorderEntry> dataView = grid.setItems(reorderEntryList);
            dataView.setSortOrder(reorderEntry -> reorderEntry.articleType() + reorderEntry.name(), SortDirection.ASCENDING);
            verticalLayout.add(grid);
        }
        return verticalLayout;
    }
}
