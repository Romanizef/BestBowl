package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.repositories.ClientRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Locale;


@Route(value = "clients", layout = MainView.class)
@PageTitle("Clients")
@PermitAll
public class ClientTableView extends VerticalLayout {
    private final Grid<Client> grid;
    private final Button refreshButton = new Button();
    private final Button deleteButton = new Button();
    private final Label countLabel = new Label();
    private final ClientRepository clientRepository;
    private Client selectedClient;

    @Autowired
    public ClientTableView(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        setSizeFull();
        refreshButton.setIcon(new Icon(VaadinIcon.REFRESH));
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.setVisible(false);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        layout.add(refreshButton, deleteButton, countLabel);
        grid = new Grid<>(Client.class);
        grid.setColumns("id", "firstName", "lastName", "email", "association", "address");
        grid.getColumns().forEach(c -> c.setSortable(false).setResizable(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        updateGrid();
        add(layout, grid);
        grid.addCellFocusListener(e -> {
            if (e.getItem().isPresent()) {
                deleteButton.setVisible(true);
                selectedClient = e.getItem().get();
            } else {
                deleteButton.setVisible(false);
            }
        });
        refreshButton.addClickListener(e -> {
            updateCountLabel();
            updateGrid();
        });
        deleteButton.addClickListener(e -> {
            if (selectedClient != null) {
                clientRepository.delete(selectedClient);
                selectedClient = null;
                deleteButton.setVisible(false);
                updateCountLabel();
                updateGrid();
            }
        });
    }

    @PostConstruct
    public void init() {
        updateCountLabel();
    }

    private void updateGrid() {
        grid.setItems(q -> clientRepository.findAll(PageRequest.of(q.getPage(), q.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(q))).stream());
    }

    private void updateCountLabel() {
        countLabel.setText("Count: " + String.format(Locale.GERMANY, "%,d", clientRepository.count()));
    }
}
