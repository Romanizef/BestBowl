package de.softwareprojekt.bestbowl.views.otherViews;

import static de.softwareprojekt.bestbowl.utils.Utils.matchAndRemoveIfContains;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
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
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingAlleyBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.DrinkBookingRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.FoodBookingRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.utils.pdf.PDFUtils;
import de.softwareprojekt.bestbowl.views.MainView;
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
    private Grid<BowlingAlleyBooking> bookingGrid;
    private BowlingAlleyBooking selectedBowlingAlleyBooking = null;
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository shoeBookingRepository;

    private TextField searchField;

    @Autowired
    public StatisticsView(BowlingAlleyBookingRepository bookingRepository,
            DrinkBookingRepository drinkBookingRepository, FoodBookingRepository foodBookingRepository,
            BowlingShoeBookingRepository shoeBookingRepository) {
        this.bowlingAlleyBookingRepository = bookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.shoeBookingRepository = shoeBookingRepository;
        setSizeFull();
        Component headerComponent = createHeader();
        Component searchComponent = createSearchComponent();
        HorizontalLayout gridLayout = createGridLayout();
        add(headerComponent, searchComponent, gridLayout);
        updateGridItems();
    }

    /**
     * Creates a html header
     *
     * @return {@code Component}
     */
    private Component createHeader() {
        return new H1("Statistiken");
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
        List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyBookingRepository.findAll();
        String searchFieldValue = searchField.getValue();
        String[] searchTerms;
        if (searchFieldValue != null) {
            searchTerms = searchFieldValue.trim().split(" ");
            Iterator<BowlingAlleyBooking> bowlingAlleyBookingIterator = bowlingAlleyBookingList.iterator();
            while (bowlingAlleyBookingIterator.hasNext()) {
                BowlingAlleyBooking bowlingAlleyBooking = bowlingAlleyBookingIterator.next();
                List<String> searchTermsCopy = new ArrayList<>(Arrays.stream(searchTerms).toList());
                matchAndRemoveIfContains(String.valueOf(bowlingAlleyBooking.getId()), searchTermsCopy);
                matchAndRemoveIfContains(String.valueOf(bowlingAlleyBooking.getClient().getId()), searchTermsCopy);
                matchAndRemoveIfContains(bowlingAlleyBooking.getClient().getLastName(), searchTermsCopy);
                matchAndRemoveIfContains(String.valueOf(bowlingAlleyBooking.getStartTime()), searchTermsCopy);
                if (!searchTermsCopy.isEmpty()) {
                    bowlingAlleyBookingIterator.remove();
                }
            }
        }
        GridListDataView<BowlingAlleyBooking> bookingGridListDataView = bookingGrid
                .setItems(bowlingAlleyBookingList);
        bookingGridListDataView.setSortOrder(BowlingAlleyBooking::getId, SortDirection.ASCENDING);
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        bookingGrid = createGrid();
        layout.add(bookingGrid);
        return layout;
    }

    /**
     * @return
     */
    private Grid<BowlingAlleyBooking> createGrid() {
        Grid<BowlingAlleyBooking> grid = new Grid<>(BowlingAlleyBooking.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();

        grid.addColumn(new ComponentRenderer<>(statistic -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(createDownloadAnchor(), new Label(String.valueOf(statistic.getId())));
            return horizontalLayout;
        })).setHeader("Rechnungsnummer");
        grid.addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getId())
                .setHeader("Kundennummer").setSortable(true);
        grid.addColumn(booking -> booking.getClient() == null ? "" : booking.getClient().getLastName())
                .setHeader("Kundennachname").setSortable(true);
        grid.addColumn("startTime").setHeader("Datum");
        //grid.addColumn(String.valueOf(calculateTotal())).setHeader("Summe");

        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addSelectionListener(e ->

        {
            if (e.isFromClient()) {
                Optional<BowlingAlleyBooking> optionalStatistic = e.getFirstSelectedItem();
                selectedBowlingAlleyBooking = optionalStatistic.orElse(null);
            }
        });
        return grid;
    }

/*     private double calculateTotal() {
        List<BowlingAlleyBooking> bowlingAlleyBookingList = bowlingAlleyBookingRepository.findAll();
        List<DrinkBooking> drinkBookingList = drinkBookingRepository.findAll();
        List<FoodBooking> foodBookingList = foodBookingRepository.findAll();
        List<BowlingShoeBooking> shoeBookingList = shoeBookingRepository.findAll();

        double total = 0;
        for (BowlingAlleyBooking alleyBooking : bowlingAlleyBookingList) {
            total += alleyBooking.getPrice();
        }
        for (DrinkBooking drinkBooking : drinkBookingList) {
            total += drinkBooking.getPrice();
        }
        for (FoodBooking foodBooking : foodBookingList) {
            total += foodBooking.getPrice();
        }
        for (BowlingShoeBooking shoeBooking : shoeBookingList) {
            total += shoeBooking.getPrice();
        }

        return total;
    } */

    private Component createDownloadAnchor() {
        Button pdfButton = new Button(new Icon(VaadinIcon.DOWNLOAD));

        byte[] pdfContent = PDFUtils.createInvoicePdf(selectedBowlingAlleyBooking);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfContent);

        StreamResource streamResource = new StreamResource("rechnungs.pdf", () -> byteArrayInputStream);
        Anchor anchor = new Anchor(streamResource, "Download PDF");
        anchor.add(pdfButton);
        anchor.onEnabledStateChanged(isAttached());
        anchor.removeAll();
        anchor.getElement().setAttribute("download", "test.pdf");

        return anchor;
    }
}
