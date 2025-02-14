package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * @author Marten Voß
 */
@Route(value = "dbRedirect", layout = MainView.class)
@PageTitle("Redirect")
@PermitAll
public class DatabaseRedirectView extends VerticalLayout {

    /**
     * The DatabaseRedirectView function redirects the user to the database page.
     */
    public DatabaseRedirectView() {
        UI.getCurrent().getPage().setLocation("/db");
    }
}