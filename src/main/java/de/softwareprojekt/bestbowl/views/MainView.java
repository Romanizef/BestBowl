package de.softwareprojekt.bestbowl.views;

import static de.softwareprojekt.bestbowl.utils.Utils.startThread;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;

import de.softwareprojekt.bestbowl.BestBowlApplication;
import de.softwareprojekt.bestbowl.beans.SecurityService;
import de.softwareprojekt.bestbowl.beans.UserManager;
import de.softwareprojekt.bestbowl.views.bookingViews.AlleyBookingView;
import de.softwareprojekt.bestbowl.views.bookingViews.ClientSearchView;
import de.softwareprojekt.bestbowl.views.bookingViews.ExtrasView;
import de.softwareprojekt.bestbowl.views.managementViews.ManagementView;
import de.softwareprojekt.bestbowl.views.otherViews.DatabaseRedirectView;

/**
 * Is the main template for all the other views and incorporates them as tabs
 *
 * @author Marten Voß
 * @author Matija Kopschek
 * @author Max Ziller
 * @author Ali aus Mali, chillt am Strand in Bali und drinkt ne ganze Bacardi
 */
public class MainView extends AppLayout implements AppShellConfigurator {
    private final transient SecurityService securityService;
    private final transient UserManager userManager;
    private final Tabs menu;
    private final H1 viewTitle;

    /**
     * Constructor for the main view. Creates a new title for the View, Horizontal Layout for the header,
     * in which the two buttons logout and shutdown are placed and a new menu
     *
     * @param securityService
     * @param userManager
     * @see #createHeaderContent()
     * @see #createMenu()
     * @see #createMenuItems()
     * @see #createDrawerContent(Tabs)
     */
    public MainView(@Autowired SecurityService securityService, @Autowired UserManager userManager) {
        this.securityService = securityService;
        this.userManager = userManager;
        setPrimarySection(Section.DRAWER);
        viewTitle = new H1();
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-m) var(--lumo-space-l)");

        HorizontalLayout headerLayout = createHeaderContent();
        headerLayout.add(new Button("Shutdown", e -> startThread(BestBowlApplication::shutdown, "shutdown", true)));
        headerLayout.add(createDarkModeToggle());
        if (securityService.getAuthenticatedUser() != null) {
            Button logoutButton = new Button("Logout", click -> securityService.logout());
            headerLayout.add(logoutButton);
        }
        addToNavbar(headerLayout);
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
        setDrawerOpened(userManager.getDrawerStateForUser(getAuthenticatedUserNameOrDefault()));
        if (userManager.getDarkModeStateForUser(getAuthenticatedUserNameOrDefault())) {
            getElement().getThemeList().add(Lumo.DARK);
        }
    }

    /**
     * Creates a new {@code Tab} Component
     *
     * @param text
     * @param navigationTarget
     * @return {@code tab}
     */
    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    /**
     * All the Views are shown as a tab in the menu
     *
     * @return {@code Tab[]}
     */
    private Tab[] createMenuItems() {
        return new Tab[]{
                createTab("Kundensuche", ClientSearchView.class),
                createTab("Extras", ExtrasView.class),
                createTab("Verwaltungen", ManagementView.class),
                createTab("Datenbank", DatabaseRedirectView.class)
        };
    }

    /**
     * A new Header is created, with the drawer toggle and the title
     *
     * @return {@code layout}
     */
    private HorizontalLayout createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.addClickListener(e -> userManager.toggleDrawerStateForUser(getAuthenticatedUserNameOrDefault()));
        layout.add(drawerToggle);
        layout.add(viewTitle);
        layout.expand(viewTitle);
        return layout;
    }

    private Button createDarkModeToggle() {
        Button button = new Button();
        button.setIcon(VaadinIcon.LIGHTBULB.create());
        button.addClickListener(e -> {
            ThemeList themeList = getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                userManager.setDarkModeStateForUser(getAuthenticatedUserNameOrDefault(), false);
            } else {
                themeList.add(Lumo.DARK);
                userManager.setDarkModeStateForUser(getAuthenticatedUserNameOrDefault(), true);
            }
        });
        return button;
    }

    /**
     * Creates a new {@code VerticalLayout} for the menu
     *
     * @param menu
     * @return {@code layout}
     */
    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        layout.add(menu);
        return layout;
    }

    /**
     * Creates a new {@code Tabs} Component, which embeds all the Views as {@code Tabs}
     *
     * @return {@code Tabs}
     * @see #createMenuItems()
     */
    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    /**
     * Marks the current open tab in the drawer
     */
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        menu.setSelectedTab(getTabForComponent(getContent()).orElse(null));
        viewTitle.setText(getCurrentPageTitle());
    }

    /**
     * Returns the {@code Tab} for the given {@code Component}
     *
     * @param component
     * @return {@code Optional<Tab>}
     */
    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    /**
     * Returns the title of the current page
     *
     * @return {@code String}
     */
    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }

    /**
     * Returns the name of the authenticated user or the default
     *
     * @return {@code String}
     */
    private String getAuthenticatedUserNameOrDefault() {
        UserDetails userDetails = securityService.getAuthenticatedUser();
        return userDetails == null ? "-" : userDetails.getUsername();
    }
}
