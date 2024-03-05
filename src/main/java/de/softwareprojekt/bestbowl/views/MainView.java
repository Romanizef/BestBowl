package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
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
import de.softwareprojekt.bestbowl.beans.ReorderService;
import de.softwareprojekt.bestbowl.beans.SecurityService;
import de.softwareprojekt.bestbowl.beans.UserManager;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.views.booking_views.ArticleBookingView;
import de.softwareprojekt.bestbowl.views.booking_views.ClientSearchView;
import de.softwareprojekt.bestbowl.views.booking_views.PendingBookingView;
import de.softwareprojekt.bestbowl.views.management_views.ManagementView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.startThread;

/**
 * Is the main template for all the other views and incorporates them as tabs
 *
 * @author Max Ziller
 */
public class MainView extends AppLayout implements AppShellConfigurator {
    private final transient SecurityService securityService;
    private final transient UserManager userManager;
    private final transient ReorderService reorderService;
    private final Tabs menu;
    private final H1 viewTitle;
    private boolean reorderEventFired = false;

    /**
     * Constructor for the main view. Creates a new title for the View, Horizontal Layout for the header,
     * in which the two buttons logout and shutdown are placed and a new menu
     *
     * @param securityService
     * @param userManager
     * @param reorderService
     * @see #createHeaderContent()
     * @see #createMenu()
     * @see #createMenuItems()
     * @see #createDrawerContent(Tabs)
     */
    public MainView(@Autowired SecurityService securityService,
                    @Autowired UserManager userManager,
                    @Autowired ReorderService reorderService) {
        this.securityService = securityService;
        this.userManager = userManager;
        this.reorderService = reorderService;
        setPrimarySection(Section.DRAWER);
        viewTitle = new H1();
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-m) var(--lumo-space-l)");
        addToNavbar(createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
        setDrawerOpened(userManager.getDrawerStateForUser(getAuthenticatedUserNameOrDefault()));
        if (userManager.getDarkModeStateForUser(getAuthenticatedUserNameOrDefault())) {
            getElement().getThemeList().add(Lumo.DARK);
        }
    }

    /**
     * @param navigationTarget target view
     * @param text             link text
     * @param vaadinIcon       icon of the link
     * @return menu entry
     */
    private static Tab createTab(Class<? extends Component> navigationTarget, String text, VaadinIcon vaadinIcon) {
        final Tab tab = new Tab();
        final RouterLink routerLink = new RouterLink();
        final Icon icon = vaadinIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");
        final Span label = new Span(text);
        routerLink.add(icon, label);
        routerLink.setRoute(navigationTarget);
        tab.add(routerLink);
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    /**
     * All the Views are shown as a tab in the menu
     *
     * @return {@code Tab[]}
     */
    private Tab[] createMenuItems() {
        List<Tab> tabList = new ArrayList<>();
        tabList.add(createTab(ClientSearchView.class, "Kundensuche", VaadinIcon.USERS));
        tabList.add(createTab(ArticleBookingView.class, "Artikelbuchung", VaadinIcon.FORM));
        tabList.add(createTab(PendingBookingView.class, "Offene Buchungen", VaadinIcon.CREDIT_CARD));
        if (securityService.isCurrentUserInRole(UserRole.OWNER)) {
            tabList.add(createTab(ManagementView.class, "Verwaltungen", VaadinIcon.DESKTOP));
        }
        if (securityService.isCurrentUserInRole(UserRole.ADMIN)) {
            tabList.add(createTab(DatabaseRedirectView.class, "Datenbank", VaadinIcon.DATABASE));
        }
        return tabList.toArray(new Tab[0]);
    }

    /**
     * creates the header layout with the drawer toggle, title, shutdown button, darkmode button and logout button
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
        //use the focus listener and focus as a page loaded event
        drawerToggle.addFocusListener(e -> onPageLoaded());
        drawerToggle.focus();
        layout.add(drawerToggle, viewTitle);
        layout.expand(viewTitle);

        Button shutdownButton = new Button(VaadinIcon.POWER_OFF.create());
        shutdownButton.getStyle().set("margin-right", "10px");
        shutdownButton.addClickListener(e -> startThread(BestBowlApplication::shutdown, "shutdown", true));
        Button darkModeButton = createDarkModeToggle();
        darkModeButton.getStyle().set("margin-right", "10px");
        layout.add(shutdownButton, darkModeButton);

        if (securityService.getAuthenticatedUser() != null) {
            Button logoutButton = new Button("Logout", click -> securityService.logout());
            logoutButton.getStyle().set("margin-right", "10px");
            layout.add(logoutButton);
        }

        return layout;
    }

    private void onPageLoaded() {
        if (!reorderEventFired) {
            reorderEventFired = true;
            if (securityService.isCurrentUserInRole(UserRole.OWNER)) {
                reorderService.checkForReorderThreshold();
            }
        }
    }

    private Button createDarkModeToggle() {
        Button button = new Button();
        button.setIcon(userManager.getDarkModeStateForUser(getAuthenticatedUserNameOrDefault()) ?
                VaadinIcon.SUN_O.create() : VaadinIcon.MOON_O.create());
        button.addClickListener(e -> {
            ThemeList themeList = getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                button.setIcon(VaadinIcon.MOON_O.create());
                userManager.setDarkModeStateForUser(getAuthenticatedUserNameOrDefault(), false);
            } else {
                themeList.add(Lumo.DARK);
                button.setIcon(VaadinIcon.SUN_O.create());
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
