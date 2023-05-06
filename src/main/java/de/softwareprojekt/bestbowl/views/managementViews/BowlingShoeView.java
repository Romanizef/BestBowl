package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingShoeRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.form.BowlingShoeForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import static de.softwareprojekt.bestbowl.utils.Utils.toDateString;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

@Route(value = "ShoeManagement", layout = MainView.class)
@PageTitle("Schuhverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class BowlingShoeView extends VerticalLayout {
    private final BowlingShoeRepository bowlingShoeRepository;
    private final Binder<BowlingShoe> bowlingShoeBinder = new Binder<>();
    private Grid<BowlingShoe> bowlingShoeGrid;
    private BowlingShoeForm bowlingShoeForm;
    private BowlingShoe selectedShoe = null;
    @Autowired
    public BowlingShoeView(BowlingShoeRepository bowlingShoeRepository){
        this.bowlingShoeRepository = bowlingShoeRepository;
        setSizeFull();
        Button newBowlingShoeButton = createNewBowlingShoeButton();
        HorizontalLayout bowlingShoeGridFormLayout = createBowlingShoeGridFormLayout();
        add(newBowlingShoeButton, bowlingShoeGridFormLayout);
        updateEditBowlingShoeLayoutState();
    }
    private Button createNewBowlingShoeButton() {
        Button button = new Button("Neue Schuhe hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            bowlingShoeGrid.deselectAll();
            selectedShoe = new BowlingShoe();
            bowlingShoeBinder.readBean(selectedShoe);
            updateEditBowlingShoeLayoutState();
        });
        return button;
    }
    private void updateEditBowlingShoeLayoutState() {
        setChildrenEnabled(bowlingShoeForm.getChildren(), selectedShoe != null);
        if (selectedShoe == null) {
            setValueForIntegerFieldChildren(bowlingShoeForm.getChildren(), null);
        }
    }
    private HorizontalLayout createBowlingShoeGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        bowlingShoeForm = new BowlingShoeForm(bowlingShoeBinder);
        layout.setSizeFull();
        bowlingShoeGrid = createBowlingShoeGrid();
        layout.add(bowlingShoeGrid, bowlingShoeForm);
        return layout;
    }
    private Grid<BowlingShoe> createBowlingShoeGrid() {
        Grid<BowlingShoe> shoeGrid = new Grid<>(BowlingShoe.class);
        shoeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        shoeGrid.removeAllColumns();
        Grid.Column<BowlingShoe> idColumn = shoeGrid.addColumn(BowlingShoe::getId).setHeader("ID");
        shoeGrid.addColumn(e -> toDateString(e.getBoughtAt()),"dd mm yyyy").setHeader("Kaufdatum");
        Grid.Column<BowlingShoe> sizeColumn = shoeGrid.addColumn("size").setHeader("Größe");
        Grid.Column<BowlingShoe> activeColumn = shoeGrid.addColumn("active").setHeader("Aktiv");
        shoeGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        shoeGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        shoeGrid.setWidth("75%");
        shoeGrid.setHeight("100%");

        List<BowlingShoe> shoeList = bowlingShoeRepository.findAll();
        GridListDataView<BowlingShoe> dataView = shoeGrid.setItems(shoeList);

        BowlingShoeView.BowlingShoeFilter shoeFilter = new BowlingShoeView.BowlingShoeFilter(dataView);
        shoeGrid.getHeaderRows().clear();
        HeaderRow headerRow = shoeGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", shoeFilter::setId));
        //headerRow.getCell(boughtColumn).setComponent(createFilterHeaderString("Kaufdatum", shoeFilter::boughtAt));
        headerRow.getCell(sizeColumn).setComponent(createFilterHeaderInteger("Größe", shoeFilter::setSize));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", shoeFilter::setActive));
        shoeGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<BowlingShoe> optionalBowlingShoe = e.getFirstSelectedItem();
                if (optionalBowlingShoe.isPresent()) {
                    selectedShoe = optionalBowlingShoe.get();
                    bowlingShoeBinder.readBean(selectedShoe);
                } else {
                    selectedShoe = null;
                    bowlingShoeBinder.readBean(new BowlingShoe());
                }
            }
            updateEditBowlingShoeLayoutState();
        });
        return shoeGrid;
    }
    private static class BowlingShoeFilter {
        private final GridListDataView<BowlingShoe> dataView;
        private String id;
        private DatePicker boughtAt;
        //private String boughtAt;
        private String size;
        private Boolean active;

        public BowlingShoeFilter(GridListDataView<BowlingShoe> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }
        public boolean test(BowlingShoe shoe) {
            boolean matchesId = matches(String.valueOf(shoe.getId()), id);
            //boolean matchesBoughtAt = matches(boughtAt);
            boolean matchesSize = matches(String.valueOf(shoe.getSize()), size);
            boolean matchesActive = active == null || active == shoe.isActive();
            return matchesId && matchesSize && matchesActive;
        }

        /*
        Filterregel noch nicht geschrieben
        */

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }
        public void setBoughtAt(DatePicker boughtAt) {
            this.boughtAt = boughtAt;
            dataView.refreshAll();
        }
        public void setSize(String size) {
            this.size = size;
            dataView.refreshAll();
        }
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
