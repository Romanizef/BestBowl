package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.Association;
import de.softwareprojekt.bestbowl.jpa.repositories.AssociationRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Matija Kopschek
 * @author Marten Voß
 */
@Route(value = "associationManagement", layout = MainView.class)
@PageTitle("Vereinsverwaltung")
@RolesAllowed({ UserRole.OWNER, UserRole.ADMIN })
public class AssociationManagementView extends VerticalLayout {
    private final Binder<Association> binder = new Binder<>();
    private final AssociationRepository associationRepository;
    private Grid<Association> associationGrid;
    private FormLayout editLayout;
    private Association selectedAssociation = null;

    @Autowired
    public AssociationManagementView(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
        setSizeFull();
        Button newAssociationButton = createNewAssociationButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newAssociationButton, gridLayout);
        updateEditLayoutState();
    }

    private Button createNewAssociationButton() {
        Button button = new Button("Neuen Verein hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            associationGrid.deselectAll();
            selectedAssociation = new Association();
            binder.readBean(selectedAssociation);
            updateEditLayoutState();
        });
        return button;
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        associationGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(associationGrid, editLayout);
        return layout;
    }

    private Grid<Association> createGrid() {
        Grid<Association> grid = new Grid<>(Association.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<Association> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<Association> nameColumn = grid.addColumn("name").setHeader("Name");
        Grid.Column<Association> discountColumn = grid.addColumn("discount").setHeader("Rabatt");
        Grid.Column<Association> activeColumn = grid
                .addColumn(association -> association.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<Association> associationList = associationRepository.findAll();
        GridListDataView<Association> dataView = grid.setItems(associationList);
        AssociationFilter associationFilter = new AssociationFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", associationFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", associationFilter::setName));
        headerRow.getCell(discountColumn)
                .setComponent(createFilterHeaderString("Rabatt", associationFilter::setDiscount));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", associationFilter::setActive));
        associationFilter.setActive(true);

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Association> optionalAssociation = e.getFirstSelectedItem();
                if (optionalAssociation.isPresent()) {
                    selectedAssociation = optionalAssociation.get();
                    binder.readBean(selectedAssociation);
                } else {
                    selectedAssociation = null;
                    binder.readBean(new Association());
                }
            }
            updateEditLayoutState();
        });
        return grid;
    }

    private FormLayout createEditLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidth("25%");
        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        TextField discountField = new TextField("Rabatt");
        discountField.setWidthFull();
        discountField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);
        layout.add(nameField, discountField, checkboxLayout, buttonLayoutConfig());

        binder.bind(nameField, Association::getName, Association::setName);
        binder.bind(discountField, a -> String.valueOf(a.getDiscount()),
                (association, s) -> association.setDiscount(Double.parseDouble(s)));
        binder.bind(activeCheckbox, Association::isActive, Association::setActive);
        return layout;
    }

    private HorizontalLayout buttonLayoutConfig() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        Button saveButton = new Button("Speichern");
        Button cancelButton = new Button("Abbrechen");
        buttonLayout.add(cancelButtonConfig(cancelButton), saveButtonConfig(saveButton));
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);
        return buttonLayout;
    }

    private Button saveButtonConfig(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));
        saveButton.addClickListener(clickEvent -> {
            showNotification("Verein gespeichert");
            disableEditLayout();
            // TODO Verein in die Datenbank speichern
        });
        return saveButton;
    }

    private Button cancelButtonConfig(Button cancelButton) {
        cancelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
        cancelButton.addClickListener(clickEvent -> {
            showNotification("Bearbeitung abgebrochen");
            disableEditLayout();
        });
        return cancelButton;
    }

    private void updateEditLayoutState() {
        if (selectedAssociation == null) {
            disableEditLayout();
        } else {
            editLayout.getChildren().forEach(component -> {
                if (component instanceof HasEnabled c) {
                    c.setEnabled(true);
                }
            });
        }
    }

    private void disableEditLayout() {
        editLayout.getChildren().forEach(component -> {
            if (component instanceof HasEnabled c) {
                c.setEnabled(false);
            }
        });
    }

    private static class AssociationFilter {
        private final GridListDataView<Association> dataView;
        private String id;
        private String name;
        private String discount;
        private Boolean active;

        public AssociationFilter(GridListDataView<Association> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(Association association) {
            boolean matchesId = matches(String.valueOf(association.getId()), id);
            boolean matchesName = matches(association.getName(), name);
            boolean matchesDiscount = matches(String.valueOf(association.getDiscount()), discount);
            boolean matchesActive = active == null || active == association.isActive();
            return matchesId && matchesDiscount && matchesName && matchesActive;
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        public void setDiscount(String discount) {
            this.discount = discount;
            dataView.refreshAll();
        }

        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
