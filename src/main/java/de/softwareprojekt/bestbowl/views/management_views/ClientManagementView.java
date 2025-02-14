package de.softwareprojekt.bestbowl.views.management_views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.jpa.entities.client.Address;
import de.softwareprojekt.bestbowl.jpa.entities.client.Association;
import de.softwareprojekt.bestbowl.jpa.entities.client.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.client.ClientRepository;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.validators.client.ClientValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringNotEmpty;
import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * Creates a View for the Client Management.
 *
 * @author Marten Voß
 */
@Route(value = "clientManagement", layout = MainView.class)
@PageTitle("Kundenverwaltung")
@RolesAllowed({ UserRole.OWNER })
public class ClientManagementView extends VerticalLayout {
    private final transient ClientRepository clientRepository;
    private final Binder<Client> binder = new Binder<>();
    private Grid<Client> clientGrid;
    private FormLayout editLayout;
    private Label validationErrorLabel;
    private Client selectedClient = null;
    private boolean editingNewClient = false;
    private static final String STREET = "Straße";
    private static final String ASSOCIATION = "Verein";
    private static final String LASTNAME = "Nachname";
    private static final String FIRSTNAME = "Vorname";
    private static final String COMMENT = "Kommentar";
    private static final String EMAIL = "E-Mail";
    private static final String CITY = "Stadt";
    private static final String IBAN = "IBAN";
    private static final String PLZ = "PLZ";
    private static final String HOUSENUMBER = "Hausnummer";

    /**
     * Constructor for the Client Management View.
     *
     * @param clientRepository
     * @see #createNewClientButton()
     * @see #createGridLayout()
     * @see #updateEditLayoutState()
     */
    @Autowired
    public ClientManagementView(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        setSizeFull();
        Button newClientButton = createNewClientButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newClientButton, gridLayout);
        updateEditLayoutState();
    }

    /**
     * Creates a {@code Button} for creating a new Client. The new Client is set to
     * the {@code selectedClient} and the grid is updated.
     *
     * @return {@code Button}
     * @see #updateEditLayoutState()
     */
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
            editingNewClient = true;
            updateEditLayoutState();
            clearNumberFieldChildren(editLayout.getChildren());
        });
        return button;
    }

    /**
     * Creates a {@code HorizontalLayout} for the {@code Grid}.
     *
     * @return {@code Grid}
     * @see #createEditLayout()
     * @see #createGrid()
     */
    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        clientGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(clientGrid, editLayout);
        return layout;
    }

    /**
     * Creates a {@code Grid<Client>} with all the attributes of the client entity.
     * Filters for the columns are also generated.
     *
     * @return {@code Grid<Client>}
     * @see #updateEditLayoutState()
     * @see #resetEditLayout()
     */
    private Grid<Client> createGrid() {
        Grid<Client> grid = new Grid<>(Client.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<Client> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<Client> firstNameColumn = grid.addColumn("firstName").setHeader(FIRSTNAME);
        Grid.Column<Client> lastNameColumn = grid.addColumn("lastName").setHeader(LASTNAME);
        Grid.Column<Client> emailColumn = grid.addColumn("email").setHeader(EMAIL);
        Grid.Column<Client> commentColumn = grid.addColumn(Client::getComment).setHeader(COMMENT);
        Grid.Column<Client> associationColumn = grid
                .addColumn(client -> client.getAssociation() == null ? "" : client.getAssociation().getName())
                .setHeader(ASSOCIATION);
        Grid.Column<Client> streetColumn = grid.addColumn(client -> client.getAddress().getStreet())
                .setHeader(STREET);
        Grid.Column<Client> houseNrColumn = grid.addColumn(client -> client.getAddress().getHouseNr())
                .setHeader(HOUSENUMBER);
        Grid.Column<Client> postCodeColumn = grid.addColumn(client -> client.getAddress().getPostCodeString())
                .setHeader(PLZ);
        Grid.Column<Client> cityColumn = grid.addColumn(client -> client.getAddress().getCity()).setHeader(CITY);
        Grid.Column<Client> ibanColumn = grid.addColumn("iban").setHeader(IBAN);
        Grid.Column<Client> activeColumn = grid.addColumn(client -> client.isActive() ? "Aktiv" : "Inaktiv")
                .setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true).setSortable(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<Client> clientList = clientRepository.findAll();
        GridListDataView<Client> dataView = grid.setItems(clientList);
        ClientFilter clientFilter = new ClientFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", clientFilter::setId));
        headerRow.getCell(firstNameColumn)
                .setComponent(createFilterHeaderString(FIRSTNAME, clientFilter::setFirstName));
        headerRow.getCell(lastNameColumn).setComponent(createFilterHeaderString(LASTNAME, clientFilter::setLastName));
        headerRow.getCell(emailColumn).setComponent(createFilterHeaderString(EMAIL, clientFilter::setEmail));
        headerRow.getCell(commentColumn).setComponent(createFilterHeaderString(COMMENT, clientFilter::setComment));
        headerRow.getCell(associationColumn)
                .setComponent(createFilterHeaderString(ASSOCIATION, clientFilter::setAssociationName));
        headerRow.getCell(streetColumn).setComponent(createFilterHeaderString(STREET, clientFilter::setStreet));
        headerRow.getCell(houseNrColumn)
                .setComponent(createFilterHeaderInteger(HOUSENUMBER, clientFilter::setHouseNr));
        headerRow.getCell(postCodeColumn).setComponent(createFilterHeaderInteger(PLZ, clientFilter::setPostCode));
        headerRow.getCell(cityColumn).setComponent(createFilterHeaderString(CITY, clientFilter::setCity));
        headerRow.getCell(ibanColumn).setComponent(createFilterHeaderString(IBAN, clientFilter::setIban));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", clientFilter::setActive));

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Client> optionalClient = e.getFirstSelectedItem();
                if (optionalClient.isPresent()) {
                    selectedClient = optionalClient.get();
                    binder.readBean(selectedClient);
                    editingNewClient = false;
                    updateEditLayoutState();
                } else {
                    resetEditLayout();
                }
            }
        });
        return grid;
    }

    /**
     * Creates a {@code FormLayout} with {@code TextField}s for the first and last
     * name, the e-mail, full address and the active settings of a client. A save
     * and cancel button is added to the {@code FormLayout}. The {@code Binder}
     * binds the {@code Client} to the {@code TextField}s.
     *
     * @return {@code FormLayout}
     */
    private FormLayout createEditLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidth("25%");

        TextField firstNameField = new TextField(FIRSTNAME);
        firstNameField.setWidthFull();
        firstNameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        firstNameField.setRequiredIndicatorVisible(true);

        TextField lastNameField = new TextField(LASTNAME);
        lastNameField.setWidthFull();
        lastNameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        lastNameField.setRequiredIndicatorVisible(true);

        TextField emailField = new TextField(EMAIL);
        emailField.setWidthFull();
        emailField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        emailField.setRequiredIndicatorVisible(true);

        TextArea commentArea = new TextArea(COMMENT);
        commentArea.setWidthFull();
        commentArea.addThemeVariants(TextAreaVariant.LUMO_SMALL);

        Select<Association> associationSelect = createAssociationSelect(ASSOCIATION);
        associationSelect.setWidthFull();
        associationSelect.addThemeVariants(SelectVariant.LUMO_SMALL);

        TextField streetField = new TextField(STREET);
        streetField.setWidthFull();
        streetField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        streetField.setRequiredIndicatorVisible(true);

        TextField houseNrField = new TextField(HOUSENUMBER + " (ggf. mit Zusatz)");
        houseNrField.setWidthFull();
        houseNrField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        houseNrField.setRequiredIndicatorVisible(true);

        IntegerField postCodeField = new IntegerField(PLZ);
        postCodeField.setWidthFull();
        postCodeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        postCodeField.setRequiredIndicatorVisible(true);

        TextField cityField = new TextField(CITY);
        cityField.setWidthFull();
        cityField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        cityField.setRequiredIndicatorVisible(true);

        TextField ibanField = new TextField(IBAN);
        ibanField.setWidthFull();
        ibanField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        ibanField.setRequiredIndicatorVisible(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();

        Button saveButton = new Button("Speichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_DOWN));

        Button cancelButton = new Button("Abbrechen");
        cancelButton.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));

        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setFlexGrow(1, cancelButton, saveButton);

        layout.add(firstNameField, lastNameField, emailField, commentArea, associationSelect, streetField, houseNrField,
                postCodeField, cityField, ibanField, checkboxLayout, createValidationLabelLayout(), buttonLayout);

        saveButton.addClickListener(clickEvent -> {
            String uneditedEmail = Objects.requireNonNullElse(selectedClient.getEmail(), "");
            Set<String> clientEmailSet = clientRepository.findAllEmails();
            if (!editingNewClient) {
                clientEmailSet.remove(uneditedEmail);
            }
            if (writeBean()) {
                if (clientEmailSet.contains(selectedClient.getEmail())) {
                    validationErrorLabel.setText("Diese E-Mail wird bereits verwendet");
                    selectedClient.setEmail(uneditedEmail);
                    return;
                }
                saveToDb();
            }
        });
        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        binder.withValidator(new ClientValidator());
        binder.bind(firstNameField, Client::getFirstName, Client::setFirstName);
        binder.bind(lastNameField, Client::getLastName, Client::setLastName);
        binder.bind(emailField, Client::getEmail, Client::setEmail);
        binder.bind(commentArea, Client::getComment, Client::setComment);
        binder.bind(associationSelect,
                client -> client.getAssociation() == null ? Association.NO_ASSOCIATION : client.getAssociation(),
                ((client, association) -> {
                    if (association.equals(Association.NO_ASSOCIATION)) {
                        client.setAssociation(null);
                    } else {
                        client.setAssociation(association);
                    }
                }));
        binder.bind(streetField, client -> client.getAddress().getStreet(),
                ((client, s) -> client.getAddress().setStreet(s)));
        binder.bind(houseNrField, client -> client.getAddress().getHouseNr(),
                ((client, s) -> client.getAddress().setHouseNr(s)));
        binder.bind(postCodeField, client -> client.getAddress().getPostCode(),
                ((client, i) -> client.getAddress().setPostCode(Objects.requireNonNullElse(i, 0))));
        binder.bind(cityField, client -> client.getAddress().getCity(),
                ((client, s) -> client.getAddress().setCity(s)));
        binder.bind(ibanField, Client::getIban, Client::setIban);
        binder.bind(activeCheckbox, Client::isActive,
                (client, active) -> client.setActive(Objects.requireNonNullElse(active, false)));
        return layout;
    }

    /**
     * Creates a {@code VerticalLayout} with a {@code Label} for displaying
     * a ValidationError.
     *
     * @return {@code VerticalLayout}
     */
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

    /**
     * Writes the {@code Client} object to the {@code Binder} and validates the
     * fields.
     *
     * @return {@code boolean}
     */
    private boolean writeBean() {
        try {
            binder.writeBean(selectedClient);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    /**
     * Saves the {@code Client} object to the database and updates the grid.
     *
     * @see #resetEditLayout()
     */
    private void saveToDb() {
        clientRepository.save(selectedClient);
        if (editingNewClient) {
            clientGrid.getListDataView().addItem(selectedClient);
        } else {
            clientGrid.getListDataView().refreshItem(selectedClient);
        }
        resetEditLayout();
        Notifications.showInfo("Kunde gespeichert");
    }

    /**
     * Resets the edit layout by setting a new {@code Client} to null and disabling
     * all the children.
     *
     * @see #updateEditLayoutState()
     */
    private void resetEditLayout() {
        clientGrid.deselectAll();
        selectedClient = null;
        editingNewClient = false;

        Client client = new Client();
        client.addAddress(new Address());
        binder.readBean(client);

        updateEditLayoutState();
        clearNumberFieldChildren(editLayout.getChildren());
    }

    /**
     * Updates the state of the children components of the edit layout.
     */
    private void updateEditLayoutState() {
        validationErrorLabel.setText("");
        setChildrenEnabled(editLayout.getChildren(), selectedClient != null);
    }

    /**
     * Creates filters for the {@code Client} objects.
     */
    private static class ClientFilter {
        private final GridListDataView<Client> dataView;
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String comment;
        private String associationName;
        private String street;
        private String houseNr;
        private String postCode;
        private String city;
        private String iban;
        private Boolean active;

        /**
         * Constructor for the {@code ClientFilter}.
         *
         * @param dataView
         */
        public ClientFilter(GridListDataView<Client> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * Tests if the {@code Client} attributes match the filter attributes.
         *
         * @param client
         * @return {@code boolean}
         */
        public boolean test(Client client) {
            boolean matchesId = matches(String.valueOf(client.getId()), id);
            boolean matchesFirstName = matches(client.getFirstName(), firstName);
            boolean matchesLastName = matches(client.getLastName(), lastName);
            boolean matchesEmail = matches(client.getEmail(), email);
            boolean matchesComment = matches(client.getComment(), comment);
            boolean matchesAssociationName = !isStringNotEmpty(associationName) ||
                    client.getAssociation() != null && matches(client.getAssociation().getName(), associationName);
            boolean matchesStreet = matches(client.getAddress().getStreet(), street);
            boolean matchesHouseNr = matches(String.valueOf(client.getAddress().getHouseNr()), houseNr);
            boolean matchesPostCode = matches(client.getAddress().getPostCodeString(), postCode);
            boolean matchesCity = matches(client.getAddress().getCity(), city);
            boolean matchesIBAN = matches(client.getIban(), iban);
            boolean matchesActive = active == null || active == client.isActive();
            return matchesId && matchesFirstName && matchesLastName && matchesEmail && matchesComment
                    && matchesAssociationName && matchesStreet && matchesHouseNr
                    && matchesPostCode && matchesCity && matchesIBAN && matchesActive;
        }

        /**
         * The setId function is used to set the id of a client.
         *
         * @param id Set the id of the object
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * The setFirstName function sets the first name of a client.
         *
         * @param firstName Set the firstname variable
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
            dataView.refreshAll();
        }

        /**
         * The setLastName function sets the last name of a client.
         *
         * @param lastName Set the last name of the person
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
            dataView.refreshAll();
        }

        /**
         * The setEmail function sets the email of a client.
         *
         * @param email
         */
        public void setEmail(String email) {
            this.email = email;
            dataView.refreshAll();
        }

        /**
         * The setComment function sets the comment of a client.
         *
         * @param comment
         */
        public void setComment(String comment) {
            this.comment = comment;
            dataView.refreshAll();
        }

        /**
         * The setAssociationName function sets the associationName variable to the
         * value of its parameter.
         *
         * @param associationName
         */
        public void setAssociationName(String associationName) {
            this.associationName = associationName;
            dataView.refreshAll();
        }

        /**
         * The setStreet function sets the street of a client.
         *
         * @param street
         */
        public void setStreet(String street) {
            this.street = street;
            dataView.refreshAll();
        }

        /**
         * The setHouseNr function sets the house number of a client.
         *
         * @param houseNr
         */
        public void setHouseNr(String houseNr) {
            this.houseNr = houseNr;
            dataView.refreshAll();
        }

        /**
         * The setPostCode function sets the postCode variable to the value of its
         * parameter.
         *
         * @param postCode
         */
        public void setPostCode(String postCode) {
            this.postCode = postCode;
            dataView.refreshAll();
        }

        /**
         * The setCity function sets the city of a client.
         *
         * @param city
         */
        public void setCity(String city) {
            this.city = city;
            dataView.refreshAll();
        }

        /**
         * The setIban function sets the iban of a client.
         *
         * @param iban
         */
        public void setIban(String iban) {
            this.iban = iban;
            dataView.refreshAll();
        }

        /**
         * The setActive function is used to set the active state of a client.
         *
         * @param active
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}