package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import de.softwareprojekt.bestbowl.beans.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Marten Voß
 * @author Matija Kopschek
 * @author Max Ziller
 * @author Ali aus Mali
 */
public class MainView extends AppLayout implements AppShellConfigurator {
    private final Tabs menu;
    private final H1 viewTitle;

    public MainView(@Autowired SecurityService securityService) {
        setPrimarySection(Section.DRAWER);
        viewTitle = new H1();
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-m) var(--lumo-space-l)");

        HorizontalLayout headerLayout = createHeaderContent();
        if (securityService.getAuthenticatedUser() != null) {
            Button logoutButton = new Button("Logout", click -> securityService.logout());
            headerLayout.add(logoutButton);
        }
        addToNavbar(headerLayout);
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
        setDrawerOpened(false);
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    /**
     * Add all views here to be added to the main side drawer
     *
     * @return an array of tabs
     */
    private Tab[] createMenuItems() {
        return new Tab[]{
                createTab("Demo", DemoView.class),
                createTab("Rechnung", InvoiceView.class),
                createTab("Nutzerverwaltung", UserManagementView.class),
                createTab("Datenbank", DatabaseRedirectView.class)
        };
    }

    private HorizontalLayout createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        layout.add(viewTitle);
        layout.expand(viewTitle);
        return layout;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        layout.add(menu);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
