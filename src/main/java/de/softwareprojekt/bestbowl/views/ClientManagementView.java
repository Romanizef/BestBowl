package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.Address;
import de.softwareprojekt.bestbowl.jpa.entities.Association;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.ClientRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

@Route(value = "clientManagement", layout = MainView.class)
@PageTitle("Kundenverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class ClientManagementView extends VerticalLayout {
    private final ClientRepository clientRepository;
    private final Binder<Client> binder = new Binder<>();
    private Grid<Client> clientGrid;
    private FormLayout editLayout;
    private Client selectedClient = null;

    @Autowired
    public ClientManagementView(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        setSizeFull();
        Button newClientButton = createNewClientButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newClientButton, gridLayout);
        updateEditLayoutState();
    }

    private Button createNewClientButton() {
        Button button = new Button("Neuen Kunden hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            clientGrid.deselectAll();
            Client client = new Client();
            client.addAddress(new Address());
            selectedClient = client;
            binder.readBean(selectedClient);
            updateEditLayoutState();
        });
        return button;
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        clientGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(clientGrid, editLayout);
        return layout;
    }

    private Grid<Client> createGrid() {
        Grid<Client> grid = new Grid<>(Client.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<Client> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<Client> firstNameColumn = grid.addColumn("firstName").setHeader("Vorname");
        Grid.Column<Client> lastNameColumn = grid.addColumn("lastName").setHeader("Nachname");
        Grid.Column<Client> emailColumn = grid.addColumn("email").setHeader("E-Mail");
        Grid.Column<Client> associationColumn = grid.addColumn(client -> client.getAssociation() == null ? "" : client.getAssociation().getName()).setHeader("Verein");
        Grid.Column<Client> streetColumn = grid.addColumn(client -> client.getAddress().getStreet()).setHeader("Straße");
        Grid.Column<Client> houseNrColumn = grid.addColumn(client -> client.getAddress().getHouseNr()).setHeader("Hausnummer");
        Grid.Column<Client> postCodeColumn = grid.addColumn(client -> client.getAddress().getPostCode()).setHeader("PLZ");
        Grid.Column<Client> cityColumn = grid.addColumn(client -> client.getAddress().getCity()).setHeader("Stadt");
        Grid.Column<Client> activeColumn = grid.addColumn(client -> client.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<Client> clientList = clientRepository.findAll();
        GridListDataView<Client> dataView = grid.setItems(clientList);
        ClientFilter clientFilter = new ClientFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", clientFilter::setId));
        headerRow.getCell(firstNameColumn).setComponent(createFilterHeaderString("Vorname", clientFilter::setFirstName));
        headerRow.getCell(lastNameColumn).setComponent(createFilterHeaderString("Nachname", clientFilter::setLastName));
        headerRow.getCell(emailColumn).setComponent(createFilterHeaderString("E-Mail", clientFilter::setEmail));
        headerRow.getCell(associationColumn).setComponent(createFilterHeaderString("Verein", clientFilter::setAssociationName));
        headerRow.getCell(streetColumn).setComponent(createFilterHeaderString("Straße", clientFilter::setStreet));
        headerRow.getCell(houseNrColumn).setComponent(createFilterHeaderInteger("Hausnummer", clientFilter::setHouseNr));
        headerRow.getCell(postCodeColumn).setComponent(createFilterHeaderInteger("PLZ", clientFilter::setPostCode));
        headerRow.getCell(cityColumn).setComponent(createFilterHeaderString("Stadt", clientFilter::setCity));
        headerRow.getCell(activeColumn).setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", clientFilter::setActive));
        clientFilter.setActive(true);

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Client> optionalClient = e.getFirstSelectedItem();
                if (optionalClient.isPresent()) {
                    selectedClient = optionalClient.get();
                    binder.readBean(selectedClient);
                    updateEditLayoutState();
                } else {
                    resetEditLayout();
                }
            }
        });
        return grid;
    }

    private FormLayout createEditLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidth("25%");

        TextField firstNameField = new TextField("Vorname");
        firstNameField.setWidthFull();
        firstNameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        TextField lastNameField = new TextField("Nachname");
        lastNameField.setWidthFull();
        lastNameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        TextField emailField = new TextField("E-Mail");
        emailField.setWidthFull();
        emailField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        ComboBox<Association> associationCB = createAssociationCB("Verein");
        associationCB.setWidthFull();
        associationCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        TextField streetField = new TextField("Straße");
        streetField.setWidthFull();
        streetField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        IntegerField houseNrField = new IntegerField("Hausnummer");
        houseNrField.setWidthFull();
        houseNrField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        IntegerField postCodeField = new IntegerField("PLZ");
        postCodeField.setWidthFull();
        postCodeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        TextField cityField = new TextField("Stadt");
        cityField.setWidthFull();
        cityField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");

        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);

        layout.add(firstNameField, lastNameField, emailField, associationCB, streetField, houseNrField,
                postCodeField, cityField, checkboxLayout, createSaveAndCancelButtonLayout());

        binder.bind(firstNameField, Client::getFirstName, Client::setFirstName);
        binder.bind(lastNameField, Client::getLastName, Client::setLastName);
        binder.bind(emailField, Client::getEmail, Client::setEmail);
        binder.bind(associationCB,
                client -> client.getAssociation() == null ? Association.NO_ASSOCIATION : client.getAssociation(),
                ((client, association) -> {
                    if (association.equals(Association.NO_ASSOCIATION)) {
                        client.setAssociation(null);
                    } else {
                        client.setAssociation(association);
                    }
                }));
        binder.bind(streetField, client -> client.getAddress().getStreet(), ((client, s) -> client.getAddress().setStreet(s)));
        binder.bind(houseNrField, client -> client.getAddress().getHouseNr(),
                ((client, i) -> client.getAddress().setHouseNr(Objects.requireNonNullElse(i, 0))));
        binder.bind(postCodeField, client -> client.getAddress().getPostCode(),
                ((client, i) -> client.getAddress().setPostCode(Objects.requireNonNullElse(i, 0))));
        binder.bind(cityField, client -> client.getAddress().getCity(), ((client, s) -> client.getAddress().setCity(s)));
        binder.bind(activeCheckbox, Client::isActive, Client::setActive);
        return layout;
    }

    private HorizontalLayout createSaveAndCancelButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();

        Button saveButton = new Button("Speichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));

        Button cancelButton = new Button("Abbrechen");
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);

        saveButton.addClickListener(clickEvent -> {
            // TODO Kunde in die Datenbank speichern
            resetEditLayout();
            showNotification("Kunde gespeichert");
        });
        cancelButton.addClickListener(clickEvent -> {
            resetEditLayout();
            showNotification("Bearbeitung abgebrochen");
        });
        return buttonLayout;
    }

    private void resetEditLayout() {
        clientGrid.deselectAll();
        selectedClient = null;

        Client client = new Client();
        client.addAddress(new Address());
        binder.readBean(client);

        updateEditLayoutState();
        editLayout.getChildren().forEach(component -> {
            if (component instanceof IntegerField i) {
                i.setValue(null);
            }
        });
    }

    private void updateEditLayoutState() {
        setEditLayoutEnabled(selectedClient != null);
    }

    private void setEditLayoutEnabled(boolean enabled) {
        editLayout.getChildren().forEach(component -> {
            if (component instanceof HasEnabled c) {
                c.setEnabled(enabled);
            }
        });
    }

    private static class ClientFilter {
        private final GridListDataView<Client> dataView;
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String associationName;
        private String street;
        private String houseNr;
        private String postCode;
        private String city;
        private Boolean active;

        public ClientFilter(GridListDataView<Client> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(Client client) {
            boolean matchesId = matches(String.valueOf(client.getId()), id);
            boolean matchesFirstName = matches(client.getFirstName(), firstName);
            boolean matchesLastName = matches(client.getLastName(), lastName);
            boolean matchesEmail = matches(client.getEmail(), email);
            boolean matchesAssociationName = client.getAssociation() != null && matches(client.getAssociation().getName(), associationName);
            boolean matchesStreet = matches(client.getAddress().getStreet(), street);
            boolean matchesHouseNr = matches(String.valueOf(client.getAddress().getHouseNr()), houseNr);
            boolean matchesPostCode = matches(String.valueOf(client.getAddress().getPostCode()), postCode);
            boolean matchesCity = matches(client.getAddress().getCity(), city);
            boolean matchesActive = active == null || active == client.isActive();
            return matchesId && matchesFirstName && matchesLastName && matchesEmail
                    && matchesAssociationName && matchesStreet && matchesHouseNr
                    && matchesPostCode && matchesCity && matchesActive;
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
            dataView.refreshAll();
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
            dataView.refreshAll();
        }

        public void setEmail(String email) {
            this.email = email;
            dataView.refreshAll();
        }

        public void setAssociationName(String associationName) {
            this.associationName = associationName;
            dataView.refreshAll();
        }

        public void setStreet(String street) {
            this.street = street;
            dataView.refreshAll();
        }

        public void setHouseNr(String houseNr) {
            this.houseNr = houseNr;
            dataView.refreshAll();
        }

        public void setPostCode(String postCode) {
            this.postCode = postCode;
            dataView.refreshAll();
        }

        public void setCity(String city) {
            this.city = city;
            dataView.refreshAll();
        }

        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}
