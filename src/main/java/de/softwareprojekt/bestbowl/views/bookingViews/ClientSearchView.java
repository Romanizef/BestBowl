package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
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
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.softwareprojekt.bestbowl.utils.Utils.matchAndRemoveIfContains;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
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
    private Label selectedClientLabel;
    private Client selectedClient = null;
    private boolean newClientSaved = false;

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

    private Dialog createNewClientDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Neuen Kunden anlegen");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(true);

        VerticalLayout layout = new VerticalLayout();
        TextField firstNameField = new TextField("Vorname");
        firstNameField.setWidthFull();
        TextField lastNameField = new TextField("Nachname");
        lastNameField.setWidthFull();
        TextField emailField = new TextField("E-Mail");
        emailField.setWidthFull();
        HorizontalLayout streetLayout = new HorizontalLayout();
        streetLayout.setWidthFull();
        TextField streetField = new TextField("Straße");
        IntegerField houseNrField = new IntegerField("H. NR");
        houseNrField.setWidth("65px");
        streetLayout.add(streetField, houseNrField);
        streetLayout.setFlexGrow(1, streetField);
        HorizontalLayout cityLayout = new HorizontalLayout();
        cityLayout.setWidthFull();
        IntegerField postCodeField = new IntegerField("PLZ");
        postCodeField.setWidth("85px");
        TextField cityField = new TextField("Stadt");
        cityLayout.add(postCodeField, cityField);
        cityLayout.setFlexGrow(1, cityField);
        ComboBox<Association> associationCB = createAssociationCB("Verein");
        associationCB.setWidthFull();
        layout.add(firstNameField, lastNameField, emailField, streetLayout, cityLayout, associationCB, createValidationLabelLayout());
        dialog.add(layout);

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        Button cancelButton = new Button("Abbrechen");
        Button saveButton = new Button("Sichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        footerLayout.add(cancelButton, saveButton);
        footerLayout.setFlexGrow(1, cancelButton, saveButton);
        dialog.getFooter().add(footerLayout);

        cancelButton.addClickListener(e -> dialog.close());
        saveButton.addClickListener(e -> {
            if (writeBean()) {
                clientRepository.save(selectedClient);
                dialog.close();
                clientGrid.getListDataView().addItem(selectedClient);
                clientGrid.deselectAll();
                clientGrid.select(selectedClient);
                updateFooterComponents();
                showNotification("Kunde angelegt");
            }
        });
        dialog.addOpenedChangeListener(e -> {
            if (e.isOpened()) {
                resetDialog();
            } else {
                if (!newClientSaved) {
                    selectedClient = null;
                }
            }
        });

        binder.withValidator(new ClientValidator());
        binder.bind(firstNameField, Client::getFirstName, Client::setFirstName);
        binder.bind(lastNameField, Client::getLastName, Client::setLastName);
        binder.bind(emailField, Client::getEmail, Client::setEmail);
        binder.bind(streetField, client -> client.getAddress().getStreet(), ((client, s) -> client.getAddress().setStreet(s)));
        binder.bind(houseNrField, client -> client.getAddress().getHouseNr(),
                ((client, i) -> client.getAddress().setHouseNr(Objects.requireNonNullElse(i, 0))));
        binder.bind(postCodeField, client -> client.getAddress().getPostCode(),
                ((client, i) -> client.getAddress().setPostCode(Objects.requireNonNullElse(i, 0))));
        binder.bind(cityField, client -> client.getAddress().getCity(), ((client, s) -> client.getAddress().setCity(s)));
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
            binder.writeBean(selectedClient);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    private void resetDialog() {
        clientGrid.deselectAll();
        updateFooterComponents();

        selectedClient = createNewClient();
        binder.readBean(selectedClient);

        validationErrorLabel.setText("");

        setValueForIntegerFieldChildren(newClientDialog.getChildren(), null);
    }

    private Component createHeader() {
        return new H1("Kundensuche");
    }

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

    private Component newClientComponent() {
        Button createClientButton = new Button("neuen Kunden anlegen");
        createClientButton.setWidth("55%");
        createClientButton.addClickListener(e -> newClientDialog.open());
        return createClientButton;
    }

    private Grid<Client> createGrid() {
        Grid<Client> grid = new Grid<>(Client.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        grid.addColumn("id").setHeader("Kundennummer");
        grid.addColumn("firstName").setHeader("Vorname");
        grid.addColumn("lastName").setHeader("Nachname");
        grid.addColumn("email").setHeader("E-Mail");
        grid.addColumn(client -> client.getAssociation() == null ? "" : client.getAssociation().getName()).setHeader("Vereinsname").setSortable(true);
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
        clientGrid.setItems(clientList);
    }

    private Component createFooterComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        selectedClientLabel = new Label();
        nextStepButton = new Button("Weiter zum Bahn buchen/reservieren");
        nextStepButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextStepButton.setWidth("55%");
        layout.add(selectedClientLabel, nextStepButton);
        nextStepButton.addClickListener(e -> UI.getCurrent().navigate(BowlingAlleyBookingView.class).ifPresent(bookingView -> bookingView.setSelectedClient(selectedClient)));
        return layout;
    }

    private void updateFooterComponents() {
        String template = "Ausgewählter Kunde: ";
        if (selectedClient == null) {
            nextStepButton.setEnabled(false);
            selectedClientLabel.setText(template);
        } else {
            nextStepButton.setEnabled(true);
            selectedClientLabel.setText(template + selectedClient.getFirstName() + " " + selectedClient.getLastName());
        }
    }

    public Client createNewClient() {
        Client client = new Client();
        client.addAddress(new Address());
        return client;
    }
}
