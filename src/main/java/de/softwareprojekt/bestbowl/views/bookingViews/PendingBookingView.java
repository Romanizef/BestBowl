package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.PermitAll;

/**
 * @author Marten Voß
 */
@Route(value = "pendingBookings", layout = MainView.class)
@PageTitle("Offene Buchungen")
@PermitAll
public class PendingBookingView extends VerticalLayout {
}
