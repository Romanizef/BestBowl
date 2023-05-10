package de.softwareprojekt.bestbowl.views;

import static de.softwareprojekt.bestbowl.utils.Utils.matchAndRemoveIfContains;
import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createFilterHeaderInteger;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createFilterHeaderString;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.utils.PDFUtils;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.security.RolesAllowed;

/**
 * Creates a view for all bookings to be displayed and downloaded
 * 
 * @author Matija Kopschek
 */
@Route(value = "statistics", layout = MainView.class)
@PageTitle("Statistiken")
@RolesAllowed({ UserRole.OWNER, UserRole.ADMIN })
public class StatisticsView extends VerticalLayout {
    private Grid<Statistic> statisticGrid;
    private Statistic selectedStatistic = null;
    private BowlingAlleyBooking booking;
    private TextField searchField;

    /**
     * 
     */
    public StatisticsView() {
        setSizeFull();
        Component searchComponent = createSearchComponent();
        HorizontalLayout gridLayout = createGridLayout();
        add(searchComponent, gridLayout);
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

    private void updateGridItems() {
        List<Statistic> statisticList = new ArrayList<>();
        String searchFieldValue = searchField.getValue();
        String[] searchTerms;
        if (searchFieldValue != null) {
            searchTerms = searchFieldValue.trim().split(" ");
            Iterator<Statistic> statisticIterator = statisticList.iterator();
            while (statisticIterator.hasNext()) {
                Statistic statistic = statisticIterator.next();
                List<String> searchTermsCopy = new ArrayList<>(Arrays.stream(searchTerms).toList());
                matchAndRemoveIfContains(String.valueOf(statistic.id()), searchTermsCopy);
                matchAndRemoveIfContains(String.valueOf(statistic.clientID()), searchTermsCopy);
                matchAndRemoveIfContains(statistic.clientLastName(), searchTermsCopy);
                matchAndRemoveIfContains(String.valueOf(statistic.date()), searchTermsCopy);
                matchAndRemoveIfContains(String.valueOf(statistic.total()), searchTermsCopy);
                if (!searchTermsCopy.isEmpty()) {
                    statisticIterator.remove();
                }
            }
        }
        GridListDataView<Statistic> statisticGridListDataView = statisticGrid.setItems(statisticList);
        statisticGridListDataView.setSortOrder(Statistic::clientLastName, SortDirection.ASCENDING);
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        statisticGrid = createGrid();
        layout.add(statisticGrid);
        return layout;
    }

    /**
     * @return
     */
    private Grid<Statistic> createGrid() {
        // TODO grid befüllen
        Grid<Statistic> grid = new Grid<>(Statistic.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();

        /* Grid.Column<Statistic> idColumn = grid.addColumn(new ComponentRenderer<>(statistic -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(createDownloadAnchor(), new Label(String.valueOf(statistic.id())));
            return horizontalLayout;
        })).setHeader("Rechnungsnummer");
        Grid.Column<Statistic> clientIDColumn = grid.addColumn("clientID").setHeader("Kundennummer");
        Grid.Column<Statistic> clientLastNameColumn = grid.addColumn("clientLastName").setHeader("Kundennachname");
        Grid.Column<Statistic> dateColumn = grid.addColumn("date").setHeader("Datum");
        Grid.Column<Statistic> totalColumn = grid.addColumn("total").setHeader("Summe");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        List<Statistic> statisticList = new ArrayList<>();
        GridListDataView<Statistic> dataView = grid.setItems(statisticList);
        StatisticFilter statisticFilter = new StatisticFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", statisticFilter::setId));
        headerRow.getCell(clientIDColumn)
                .setComponent(createFilterHeaderInteger("Kundernummer", statisticFilter::setClientID));
        headerRow.getCell(clientLastNameColumn)
                .setComponent(createFilterHeaderString("Nachname", statisticFilter::setClientLastName));
        headerRow.getCell(dateColumn).setComponent(createFilterHeaderString("Datum", statisticFilter::setDate));
        headerRow.getCell(totalColumn)
                .setComponent(createFilterHeaderString("Summe", statisticFilter::setTotal));

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Statistic> optionalStatistic = e.getFirstSelectedItem();
                selectedStatistic = optionalStatistic.orElse(null);
            }
        }); */
        return grid;
    }

    private Component createDownloadAnchor() {
        Button pdfButton = new Button(new Icon(VaadinIcon.DOWNLOAD));

        byte[] pdfContent = PDFUtils.createInvoicePdf(booking);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfContent);

        StreamResource streamResource = new StreamResource("rechnungs.pdf", () -> byteArrayInputStream);
        Anchor anchor = new Anchor(streamResource, "Download PDF");
        anchor.add(pdfButton);
        anchor.onEnabledStateChanged(isAttached());
        anchor.removeAll();
        anchor.getElement().setAttribute("download", "test.pdf");

        return anchor;
    }

    private record Statistic(int id, int clientID, String clientLastName, Date date, double total) {

    }

    /**
     * Creates filters for the {@code Statistic} objects.
     */
    private static class StatisticFilter {
        private final GridListDataView<Statistic> dataView;
        private String id;
        private String clientID;
        private String clientLastName;
        private String date;
        private String total;

        /**
         * Constructor for the {@code StatisticFilter}.
         *
         * @param dataView
         */
        public StatisticFilter(GridListDataView<Statistic> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * Tests if the {@code Statistic} attributes match the filter attributes.
         *
         * @param statistic
         * @return {@code boolean}
         */
        public boolean test(Statistic statistic) {
            boolean matchesId = matches(String.valueOf(statistic.id()), id);
            boolean matchesClientID = matches(String.valueOf(statistic.clientID()), clientID);
            boolean matchesClientLastName = matches(statistic.clientLastName(), clientLastName);
            boolean matchesDate = matches(String.valueOf(statistic.date()), date);
            boolean matchesTotal = matches(String.valueOf(statistic.total()), total);
            return matchesId && matchesClientID && matchesClientLastName && matchesDate
                    && matchesTotal;
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setClientID(String clientID) {
            this.clientID = clientID;
        }

        public void setClientLastName(String clientLastName) {
            this.clientLastName = clientLastName;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setTotal(String total) {
            this.total = total;
        }
    }
}
