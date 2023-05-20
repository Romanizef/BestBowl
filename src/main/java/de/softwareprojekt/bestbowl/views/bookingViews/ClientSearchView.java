package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.softwareprojekt.bestbowl.jpa.entities.Address;
import de.softwareprojekt.bestbowl.jpa.entities.Association;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.ClientRepository;
import de.softwareprojekt.bestbowl.utils.validators.ClientValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import de.softwareprojekt.bestbowl.views.otherViews.StatisticsView;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.softwareprojekt.bestbowl.utils.Utils.matchAndRemoveIfContains;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;
import static de.softwareprojekt.bestbowl.utils.messages.NotificationSender.showNotification;

/**
 * Creates a View in which the user can search for a Client.
 *
 * @author Marten Voß
 */
@Route(value = "clientSearch", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Kundensuche")
@PermitAll
public class ClientSearchView extends VerticalLayout {
    private final transient ClientRepository clientRepository;
    private final Binder<Client> binder = new Binder<>();
    private final Dialog newClientDialog;
    private final Grid<Client> clientGrid;
    private Label validationErrorLabel;
    private TextField searchField;
    private Button nextStepButton;
    private Button showStatisticButton;
    private Label selectedClientLabel;
    private Client selectedClient = null;
    private Client newClient = null;

    /**
     * Constructor for the ClientSearchView. Creates a new ClientSearchView with
     * the given clientRepository.
     *
     * @param clientRepository
     * @see #createNewClientDialog()
     * @see #createHeader()
     * @see #createSearchComponent()
     * @see #createGrid()
     * @see #createFooterComponent()
     * @see #updateGridItems()
     * @see #resetDialog()
     */
    @Autowired
    public ClientSearchView(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        newClientDialog = createNewClientDialog();
        Component headerComponent = createHeader();
        Component searchComponent = createSearchComponent();
        Component newClientComponent = newClientComponent();
        clientGrid = createGrid();
        Component footerComponent = createFooterComponent();
        add(headerComponent, searchComponent, newClientComponent, clientGrid, footerComponent);
        updateGridItems();
        updateFooterComponents();
        resetDialog();
    }

    /**
     * Creates a {@code Component} for the header. A new Layout filled with the
     * first and last name, the e-mail, full address and the association of the new
     * client is created. In the footer two buttons are created, one for saving the
     * new client to the database and one for canceling the creation. The
     * {@code Binder} binds the fields to the {@code Client} entity and validates
     * it.
     *
     * @return {@code Dialog}
     * @see #resetDialog()
     * @see #createNewClientDialog()
     * @see #createNewClient()
     * @see #createValidationLabelLayout()
     */
    private Dialog createNewClientDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Neuen Kunden anlegen");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);

        VerticalLayout layout = new VerticalLayout();
        TextField firstNameField = new TextField("Vorname");
        firstNameField.setWidthFull();
        firstNameField.setRequiredIndicatorVisible(true);
        TextField lastNameField = new TextField("Nachname");
        lastNameField.setWidthFull();
        lastNameField.setRequiredIndicatorVisible(true);
        TextField emailField = new TextField("E-Mail");
        emailField.setWidthFull();
        emailField.setRequiredIndicatorVisible(true);
        HorizontalLayout streetLayout = new HorizontalLayout();
        streetLayout.setWidthFull();
        TextField streetField = new TextField("Straße");
        streetField.setRequiredIndicatorVisible(true);
        IntegerField houseNrField = new IntegerField("H. NR");
        houseNrField.setWidth("65px");
        houseNrField.setRequiredIndicatorVisible(true);
        streetLayout.add(streetField, houseNrField);
        streetLayout.setFlexGrow(1, streetField);
        HorizontalLayout cityLayout = new HorizontalLayout();
        cityLayout.setWidthFull();
        IntegerField postCodeField = new IntegerField("PLZ");
        postCodeField.setWidth("85px");
        postCodeField.setRequiredIndicatorVisible(true);
        TextField cityField = new TextField("Stadt");
        cityField.setRequiredIndicatorVisible(true);
        cityLayout.add(postCodeField, cityField);
        cityLayout.setFlexGrow(1, cityField);
        ComboBox<Association> associationCB = createAssociationCB("Verein");
        associationCB.setWidthFull();
        layout.add(firstNameField, lastNameField, emailField, streetLayout, cityLayout, associationCB,
                createValidationLabelLayout());
        dialog.add(layout);

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        Button cancelButton = new Button("Abbrechen");
        Button saveButton = new Button("Sichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        footerLayout.add(cancelButton, saveButton);
        footerLayout.setFlexGrow(1, cancelButton, saveButton);
        dialog.getFooter().add(footerLayout);

        cancelButton.addClickListener(e -> {
            newClient = null;
            dialog.close();
        });
        saveButton.addClickListener(e -> {
            if (writeBean(newClient)) {
                Set<String> clientEmailSet = clientRepository.findAllEmails();
                if (clientEmailSet.contains(newClient.getEmail())) {
                    validationErrorLabel.setText("Diese E-Mail wird bereits verwendet");
                    return;
                }
                clientRepository.save(newClient);
                clientGrid.getListDataView().addItem(newClient);
                clientGrid.deselectAll();
                clientGrid.select(newClient);
                selectedClient = newClient;
                showNotification("Kunde angelegt");
                dialog.close();
            }
        });
        dialog.addOpenedChangeListener(e -> {
            if (e.isOpened()) {
                resetDialog();
            } else {
                if (newClient != null) {
                    selectedClient = newClient;
                    newClient = null;
                    updateFooterComponents();
                }
            }
        });

        binder.withValidator(new ClientValidator());
        binder.bind(firstNameField, Client::getFirstName, Client::setFirstName);
        binder.bind(lastNameField, Client::getLastName, Client::setLastName);
        binder.bind(emailField, Client::getEmail, Client::setEmail);
        binder.bind(streetField, client -> client.getAddress().getStreet(),
                ((client, s) -> client.getAddress().setStreet(s)));
        binder.bind(houseNrField, client -> client.getAddress().getHouseNr(),
                ((client, i) -> client.getAddress().setHouseNr(Objects.requireNonNullElse(i, 0))));
        binder.bind(postCodeField, client -> client.getAddress().getPostCode(),
                ((client, i) -> client.getAddress().setPostCode(Objects.requireNonNullElse(i, 0))));
        binder.bind(cityField, client -> client.getAddress().getCity(),
                ((client, s) -> client.getAddress().setCity(s)));
        binder.bind(associationCB,
                client -> client.getAssociation() == null ? Association.NO_ASSOCIATION : client.getAssociation(),
                ((client, association) -> {
                    if (association.equals(Association.NO_ASSOCIATION)) {
                        client.setAssociation(null);
                    } else {
                        client.setAssociation(association);
                    }
                }));
        return dialog;
    }

    /**
     * Creates a {@code VerticalLayout} for the validation label.
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
     * Writes the contents of the bound fields into the {@code Client} object and
     * validates the fields.
     *
     * @param client
     * @return {@code boolean}
     */
    private boolean writeBean(Client client) {
        try {
            binder.writeBean(client);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    /**
     * Resets the dialog by creating a new {@code Client} object and setting the
     * fields on null.
     */
    private void resetDialog() {
        newClient = createNewClient();
        binder.readBean(newClient);
        validationErrorLabel.setText("");
        clearNumberFieldChildren(newClientDialog.getChildren());
    }

    /**
     * Creates a html header
     *
     * @return {@code Component}
     */
    private Component createHeader() {
        return new H1("Kundensuche");
    }

    /**
     * Creates a {@code TextField} for searching inside a HorizontalLayout.
     *
     * @return {@code Component}
     */
    private Component createSearchComponent() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("70%");
        searchField = new TextField();
        searchField.setPlaceholder("Suche nach Kundennummer, Name, E-Mail oder Verein ...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGridItems());
        Button searchButton = new Button();
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        searchButton.addClickListener(e -> updateGridItems());
        searchLayout.expand(searchField);
        searchLayout.add(searchField, searchButton);
        return searchLayout;
    }

    /**
     * Creates a {@code Button} for creating a new {@code Client} object.
     *
     * @return {@code Component}
     */
    private Component newClientComponent() {
        Button createClientButton = new Button("neuen Kunden anlegen");
        createClientButton.setWidth("55%");
        createClientButton.addClickListener(e -> newClientDialog.open());
        return createClientButton;
    }

    /**
     * Creates a {@code Grid} for displaying the {@code Client} objects.
     *
     * @return {@code Grid<Client>}
     */
    private Grid<Client> createGrid() {
        Grid<Client> grid = new Grid<>(Client.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        grid.addColumn("id").setHeader("Kundennummer");
        grid.addColumn("firstName").setHeader("Vorname");
        grid.addColumn("lastName").setHeader("Nachname");
        grid.addColumn("email").setHeader("E-Mail");
        grid.addColumn(client -> client.getAssociation() == null ? "" : client.getAssociation().getName())
                .setHeader("Vereinsname").setSortable(true);
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();
        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<Client> optionalClient = e.getFirstSelectedItem();
                selectedClient = optionalClient.orElse(null);
                updateFooterComponents();
            }
        });
        return grid;
    }

    /**
     * Updates the {@code Grid} with the {@code Client} objects, while searching for
     * a specific client.
     */
    private void updateGridItems() {
        List<Client> clientList = clientRepository.findAllByActiveEqualsOrderByLastName(true);
        String searchFieldValue = searchField.getValue();
        String[] searchTerms;
        if (searchFieldValue != null) {
            searchTerms = searchFieldValue.trim().split(" ");
            Iterator<Client> clientIterator = clientList.iterator();
            while (clientIterator.hasNext()) {
                Client client = clientIterator.next();
                List<String> searchTermsCopy = new ArrayList<>(Arrays.stream(searchTerms).toList());
                matchAndRemoveIfContains(String.valueOf(client.getId()), searchTermsCopy);
                matchAndRemoveIfContains(client.getFirstName(), searchTermsCopy);
                matchAndRemoveIfContains(client.getLastName(), searchTermsCopy);
                matchAndRemoveIfContains(client.getEmail(), searchTermsCopy);
                if (client.getAssociation() != null) {
                    matchAndRemoveIfContains(client.getAssociation().getName(), searchTermsCopy);
                }
                if (!searchTermsCopy.isEmpty()) {
                    clientIterator.remove();
                }
            }
        }
        GridListDataView<Client> clientGridListDataView = clientGrid.setItems(clientList);
        clientGridListDataView.setSortOrder(Client::getLastName, SortDirection.ASCENDING);
    }

    /**
     * Creates a {@code VerticalLayout} for the selected client label and the next
     * step button, which is used to navigate to the next view.
     *
     * @return {@code Component}
     */
    private Component createFooterComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        selectedClientLabel = new Label();
        nextStepButton = new Button("Weiter zum Bahn buchen/reservieren");
        nextStepButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextStepButton.setWidth("55%");
        layout.add(selectedClientLabel, nextStepButton);
        nextStepButton.addClickListener(e -> UI.getCurrent().navigate(BowlingAlleyBookingView.class)
                .ifPresent(bookingView -> bookingView.setSelectedClient(selectedClient)));
               
        showStatisticButton = new Button("Statistik anzeigen");
        showStatisticButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        showStatisticButton.setWidth("55%");
        layout.add(showStatisticButton);
        showStatisticButton.addClickListener(e -> UI.getCurrent().navigate(StatisticsView.class)
                .ifPresent(statisticsView -> statisticsView.setSelectedClient(selectedClient)));
        return layout;
    }

    /**
     * Updates the footer components. Enables the next step button only if a
     * {@code Client} is selected
     */
    private void updateFooterComponents() {
        String template = "Ausgewählter Kunde: ";
        if (selectedClient == null) {
            nextStepButton.setEnabled(false);
            showStatisticButton.setEnabled(false);
            selectedClientLabel.setText(template);
        } else {
            nextStepButton.setEnabled(true);
            showStatisticButton.setEnabled(true);
            selectedClientLabel.setText(template + selectedClient.getFirstName() + " " + selectedClient.getLastName());
        }
    }

    /**
     * Creates a new {@code Client} object.
     *
     * @return {@code Client}
     */
    public Client createNewClient() {
        Client client = new Client();
        client.addAddress(new Address());
        return client;
    }
}