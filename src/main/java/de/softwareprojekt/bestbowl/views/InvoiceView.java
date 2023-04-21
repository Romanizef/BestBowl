package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "invoice", layout = MainView.class)
@PageTitle("Rechnung")
@PermitAll
/**
 * @author Matija Kopschek
 */
public class InvoiceView extends VerticalLayout {
    /**
     * 
     */
    public InvoiceView() {
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        layout.add(new H1("Rechnungen werden hier angezeigt"));
        add(layout);
    }
}
