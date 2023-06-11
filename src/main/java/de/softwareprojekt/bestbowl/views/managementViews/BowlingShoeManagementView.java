package de.softwareprojekt.bestbowl.views.managementViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoe;
import de.softwareprojekt.bestbowl.jpa.repositories.bowlingShoe.BowlingShoeRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.articleForms.BowlingShoeForm;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.toDateString;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Max Ziller
 */
@Route(value = "shoeManagement", layout = MainView.class)
@PageTitle("Schuhverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class BowlingShoeManagementView extends VerticalLayout {
    private final BowlingShoeRepository bowlingShoeRepository;
    private final Binder<BowlingShoe> bowlingShoeBinder = new Binder<>();
    private final Button saveButton = new Button("Sichern");
    private final Button cancelButton = new Button("Abbrechen");
    private Grid<BowlingShoe> bowlingShoeGrid;
    private BowlingShoeForm bowlingShoeForm;
    private BowlingShoe selectedShoe = null;
    private Label validationErrorLabel;
    private boolean editingNewBowlingShoe = false;

    @Autowired
    public BowlingShoeManagementView(BowlingShoeRepository bowlingShoeRepository) {
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
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            editingNewBowlingShoe = false;
            updateEditBowlingShoeLayoutState();
            clearNumberFieldChildren(bowlingShoeForm.getChildren());
        });
        return button;
    }

    private void updateEditBowlingShoeLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(bowlingShoeForm.getChildren(), selectedShoe != null);
    }

    private HorizontalLayout createBowlingShoeGridFormLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        bowlingShoeGrid = createBowlingShoeGrid();
        layout.add(bowlingShoeGrid, createBowlingShoeFormLayout());
        return layout;
    }

    private VerticalLayout createBowlingShoeFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("25%");
        bowlingShoeForm = new BowlingShoeForm(bowlingShoeBinder);
        layout.add(bowlingShoeForm, createValidationLabelLayout(), createButton());
        return layout;
    }

    private VerticalLayout createValidationLabelLayout() {
        VerticalLayout validationLabelLayout = new VerticalLayout();
        validationLabelLayout.setWidthFull();
        validationLabelLayout.setPadding(false);
        validationLabelLayout.setMargin(false);
        validationLabelLayout.setAlignItems(Alignment.CENTER);

        validationErrorLabel = new Label();
        validationErrorLabel.getStyle().set("color", "red");

        validationLabelLayout.add(validationErrorLabel);
        return validationLabelLayout;
    }

    private boolean writeBean() {
        try {
            bowlingShoeBinder.writeBean(selectedShoe);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    public Component createButton() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        saveButton.setEnabled(false);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        cancelButton.setEnabled(false);
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        saveButton.addClickListener(clickEvent -> {
            BowlingShoe undeditedBowlingShoe = new BowlingShoe();
            if (writeBean()) {
                saveToDnAndUpdateBowlingShoe();
            } else {
                selectedShoe.copyValueOf(undeditedBowlingShoe);
            }
        });
        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        return buttonLayout;
    }

    private void saveToDnAndUpdateBowlingShoe() {
        bowlingShoeRepository.save(selectedShoe);
        if (editingNewBowlingShoe) {
            bowlingShoeGrid.getListDataView().addItem(selectedShoe);
        } else {
            bowlingShoeGrid.getListDataView().refreshItem(selectedShoe);
        }
        resetEditLayout();
        Notifications.showInfo("Schuh gespeichert");
    }

    private void resetEditLayout() {
        bowlingShoeGrid.deselectAll();
        selectedShoe = null;
        editingNewBowlingShoe = false;
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);

        updateEditBowlingShoeLayoutState();
        clearNumberFieldChildren(bowlingShoeForm.getChildren());
    }


    private Grid<BowlingShoe> createBowlingShoeGrid() {
        Grid<BowlingShoe> shoeGrid = new Grid<>(BowlingShoe.class);
        shoeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        shoeGrid.removeAllColumns();
        Grid.Column<BowlingShoe> idColumn = shoeGrid.addColumn(BowlingShoe::getId).setHeader("ID");
        Grid.Column<BowlingShoe> boughtAtColumn = shoeGrid.addColumn(s -> toDateString(s.getBoughtAt()), "dd mm yyyy").setHeader("Kaufdatum");
        Grid.Column<BowlingShoe> sizeColumn = shoeGrid.addColumn("size").setHeader("Größe");
        Grid.Column<BowlingShoe> activeColumn = shoeGrid.addColumn(s -> s.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        shoeGrid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        shoeGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        shoeGrid.setWidth("75%");
        shoeGrid.setHeight("100%");

        List<BowlingShoe> shoeList = bowlingShoeRepository.findAll();
        GridListDataView<BowlingShoe> dataView = shoeGrid.setItems(shoeList);

        BowlingShoeManagementView.BowlingShoeFilter shoeFilter = new BowlingShoeManagementView.BowlingShoeFilter(dataView);
        shoeGrid.getHeaderRows().clear();
        HeaderRow headerRow = shoeGrid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", shoeFilter::setId));
        headerRow.getCell(boughtAtColumn).setComponent(createFilterHeaderString("Kaufdatum", shoeFilter::setBoughtAt));
        headerRow.getCell(sizeColumn).setComponent(createFilterHeaderInteger("Größe", shoeFilter::setSize));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", shoeFilter::setActive));
        shoeGrid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<BowlingShoe> optionalBowlingShoe = e.getFirstSelectedItem();
                saveButton.setEnabled(true);
                cancelButton.setEnabled(true);
                if (optionalBowlingShoe.isPresent()) {
                    selectedShoe = optionalBowlingShoe.get();
                    bowlingShoeBinder.readBean(selectedShoe);
                    editingNewBowlingShoe = false;
                } else {
                    selectedShoe = null;
                    bowlingShoeBinder.readBean(null);
                    saveButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                }
            }
            updateEditBowlingShoeLayoutState();
        });
        return shoeGrid;
    }

    private static class BowlingShoeFilter {
        private final GridListDataView<BowlingShoe> dataView;
        private String id;
        private String boughtAt;
        private String size;
        private Boolean active;

        public BowlingShoeFilter(GridListDataView<BowlingShoe> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(BowlingShoe shoe) {
            boolean matchesId = matches(String.valueOf(shoe.getId()), id);
            boolean matchesBoughtAt = matches(Utils.toDateString(shoe.getBoughtAt()), boughtAt);
            boolean matchesSize = matches(String.valueOf(shoe.getSize()), size);
            boolean matchesActive = active == null || active == shoe.isActive();
            return matchesId && matchesBoughtAt && matchesSize && matchesActive;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setBoughtAt(String boughtAt) {
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
