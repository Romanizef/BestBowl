package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.security.RolesAllowed;

import java.util.Date;
import java.util.Optional;

/**
 * @author Matija Kopschek
 */
@Route(value = "statistics", layout = MainView.class)
@PageTitle("Statistiken")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class StatisticsView extends VerticalLayout {
    private Grid<Statistic> statisticGrid;
    private Statistic selectedStatistic = null;
    private TextField searchField;

    public StatisticsView() {
        setSizeFull();
        Component searchComponent = createSearchComponent();
        HorizontalLayout gridLayout = createGridLayout();
        add(searchComponent, gridLayout, createDownloadAsPDFButton());
    }

    private Component createSearchComponent() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("70%");
        searchFieldConfig();
        searchButtonConfig(searchLayout);
        return searchLayout;
    }

    private void searchFieldConfig() {
        searchField = new TextField();
        searchField.setPlaceholder("Suche nach Kundennummer, Rechnungsnummer oder Nachname ...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGridItems());
    }

    private void searchButtonConfig(HorizontalLayout searchLayout) {
        Button searchButton = new Button();
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        searchButton.addClickListener(e -> updateGridItems());
        searchLayout.expand(searchField);
        searchLayout.add(searchField, searchButton);
    }

    //TODO Tabelle aktualisieren je nach Suchangabe
    private void updateGridItems() {
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        statisticGrid = createGrid();
        layout.add(statisticGrid);
        return layout;
    }

    //TODO Daten aus record Statistike Klasse
    private Grid<Statistic> createGrid() {
        Grid<Statistic> grid = new Grid<>(Statistic.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        grid.addColumn(Statistic::id).setHeader("ID");
        grid.addColumn(Statistic::clientID).setHeader("KundenID");
        grid.addColumn(Statistic::clientLastName).setHeader("Nachname");
        grid.addColumn(Statistic::date).setHeader("Datum");
        grid.addColumn(Statistic::total).setHeader("Summe");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();
        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Statistic> optionalStatistic = e.getFirstSelectedItem();
                selectedStatistic = optionalStatistic.orElse(null);
            }
        });
        return grid;
    }

    private Button createDownloadAsPDFButton() {
        Button pdfButton = new Button("PDF");
        pdfButton.setIcon(new Icon(VaadinIcon.DOWNLOAD));
        pdfButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        pdfButton.addClickListener(e -> {
            //TODO pdf erstellen und downloaden
        });
        return pdfButton;
    }

    private record Statistic(int id, int clientID, String clientLastName, Date date, double total) {
    }
}
