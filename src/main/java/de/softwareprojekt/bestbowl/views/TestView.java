package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@Route(value = "test", layout = MainView.class)
@PageTitle("Test")
@PermitAll
public class TestView {

}
