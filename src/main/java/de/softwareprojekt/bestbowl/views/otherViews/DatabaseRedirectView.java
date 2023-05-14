package de.softwareprojekt.bestbowl.views.otherViews;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.PermitAll;

/**
 * @author Marten Voß
 */
@Route(value = "dbRedirect", layout = MainView.class)
@PageTitle("Redirect")
@PermitAll
public class DatabaseRedirectView extends VerticalLayout {
    public DatabaseRedirectView() {
        UI.getCurrent().getPage().setLocation("/db");
    }
}
