package de.softwareprojekt.bestbowl.views;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import de.softwareprojekt.bestbowl.jpa.entities.Statistic;
import de.softwareprojekt.bestbowl.jpa.repositories.StatisticsRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;

/**
 * @author Matija Kopschek
 */
@Route(value = "statistics", layout = MainView.class)
@PageTitle("Statistiken")
@RolesAllowed({ UserRole.OWNER, UserRole.ADMIN })
public class StatisticsView extends VerticalLayout {
    private Grid<Statistic> statisticGrid;
    private Statistic selectedStatistic = null;
    private final Binder<Statistic> binder = new Binder<>();
    private TextField searchField;
    private final StatisticsRepository statisticsRepository;

    @Autowired
    public StatisticsView(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
        setSizeFull();
        Component searchComponent = createSearchComponent();
        HorizontalLayout gridLayout = createGridLayout();
        add(searchComponent, gridLayout);
    }

    private Component createSearchComponent() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("70%");
        searchField = new TextField();
        searchField.setPlaceholder("Suche nach Kundennummer, Rechnungsnummer oder Nachname ...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGridItems());
        Button searchButton = new Button();
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        searchButton.addClickListener(e -> updateGridItems());
        searchLayout.expand(searchField);
        searchLayout.add(searchField, searchButton);
        return searchLayout;
    }

    private void updateGridItems() {
        String searchString = searchField.getValue();
        if (Utils.isStringNotEmpty(searchString)) {
            statisticGrid.setItems(statisticsRepository.findAllByAnyFieldContainingStringAndActive(searchString, true));
        } else {
            statisticGrid.setItems(statisticsRepository.findAllByActiveEquals(true));
        }
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        statisticGrid = createGrid();
        layout.add(statisticGrid);
        return layout;
    }

    private Grid<Statistic> createGrid() {
        Grid<Statistic> grid = new Grid<>(Statistic.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        grid.addColumn("id").setHeader("RechnungsID");
        grid.addColumn("cID").setHeader("KundenID");
        grid.addColumn("cLastName").setHeader("Kundenname");
        grid.addColumn("date").setHeader("Datum");
        grid.addColumn("total").setHeader("Summe");
        grid.addColumn(statistic -> statistic.isActive() ? "Aktiv" : "Inaktiv")
                .setHeader("Aktiv");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();
        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Statistic> optionalClient = e.getFirstSelectedItem();
                selectedStatistic = optionalClient.orElse(null);
            }
        });
        return grid;
    }

    private Button createDownloadAsPDFButton() {
        Button pdfButton = new Button("PDF");
        pdfButton.setIcon(new Icon(VaadinIcon.DOWNLOAD));
        pdfButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pdfButton.addClickListener(e -> {
            statisticGrid.deselectAll();
            selectedStatistic = new Statistic();
            binder.readBean(selectedStatistic);
        });
        return pdfButton;
    }
}
