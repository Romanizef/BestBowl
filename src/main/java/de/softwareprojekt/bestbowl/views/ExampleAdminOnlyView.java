package de.softwareprojekt.bestbowl.views;


import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = MainView.class)
@PageTitle("Admin")
@RolesAllowed(UserRole.ADMIN)
public class ExampleAdminOnlyView extends VerticalLayout {
    public ExampleAdminOnlyView() {
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        layout.add(new H1("ONLY FOR ADMINS"));
        add(layout);
    }
}
