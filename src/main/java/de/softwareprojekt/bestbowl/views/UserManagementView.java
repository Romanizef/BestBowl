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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.beans.UserManager;
import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;

/**
 * @author Marten Voß
 */
@Route(value = "userManagement", layout = MainView.class)
@PageTitle("Nutzerverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class UserManagementView extends VerticalLayout {
    private final UserRepository userRepository;
    private final Binder<User> binder = new Binder<>();
    private Grid<User> userGrid;
    private FormLayout editLayout;
    private User selectedUser = null;
    @Resource
    private UserManager userManager;

    @Autowired
    public UserManagementView(UserRepository userRepository) {
        this.userRepository = userRepository;
        setSizeFull();
        Button newUserButton = createNewUserButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newUserButton, gridLayout);
        updateEditLayoutState();
    }

    private Button createNewUserButton() {
        Button button = new Button("Neuen Nutzer hinzufügen");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            userGrid.deselectAll();
            selectedUser = new User();
            binder.readBean(selectedUser);
            updateEditLayoutState();
        });
        return button;
    }

    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        userGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(userGrid, editLayout);
        return layout;
    }

    private Grid<User> createGrid() {
        Grid<User> grid = new Grid<>(User.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<User> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<User> nameColumn = grid.addColumn("name").setHeader("Name");
        Grid.Column<User> emailColumn = grid.addColumn("email").setHeader("E-Mail");
        Grid.Column<User> answerColumn = grid.addColumn("securityQuestionAnswer").setHeader("Sicherheitsfragenantwort");
        Grid.Column<User> roleColumn = grid.addColumn("role").setHeader("Nutzerrolle");
        Grid.Column<User> activeColumn = grid.addColumn(user -> user.isActive() ? "Aktiv" : "Inaktiv").setHeader("Aktiv");
        grid.getColumns().forEach(c -> c.setResizable(true).setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("75%");
        grid.setHeight("100%");

        List<User> userList = userRepository.findAll();
        GridListDataView<User> dataView = grid.setItems(userList);
        UserFilter userFilter = new UserFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(idColumn).setComponent(createFilterHeaderInteger("ID", userFilter::setId));
        headerRow.getCell(nameColumn).setComponent(createFilterHeaderString("Name", userFilter::setName));
        headerRow.getCell(emailColumn).setComponent(createFilterHeaderString("E-Mail", userFilter::setEmail));
        headerRow.getCell(answerColumn).setComponent(createFilterHeaderString("Sicherheitsfragenantwort", userFilter::setSecurityQuestionAnswer));
        headerRow.getCell(roleColumn).setComponent(createFilterHeaderString("Rolle", userFilter::setRole));
        headerRow.getCell(activeColumn).setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", userFilter::setActive));
        userFilter.setActive(true);

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<User> optionalUser = e.getFirstSelectedItem();
                if (optionalUser.isPresent()) {
                    selectedUser = optionalUser.get();
                    binder.readBean(selectedUser);
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

        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        TextField emailField = new TextField("E-Mail");
        emailField.setWidthFull();
        emailField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        PasswordField passwordField = new PasswordField("Passwort");
        passwordField.setWidthFull();
        passwordField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        TextField securityQuestionAnswerField = new TextField("Sicherheitsfragenantwort");
        securityQuestionAnswerField.setWidthFull();
        securityQuestionAnswerField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        ComboBox<String> roleCB = new ComboBox<>("Nutzerrolle");
        roleCB.setWidthFull();
        roleCB.setAllowCustomValue(false);
        roleCB.setItems(UserRole.getAllValues());
        roleCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");

        Checkbox activeCheckbox = new Checkbox("Aktiv");
        checkboxLayout.add(activeCheckbox);

        layout.add(nameField, emailField, passwordField, securityQuestionAnswerField, roleCB, checkboxLayout, createSaveAndCancelButtonLayout());
        binder.bind(nameField, User::getName, User::setName);
        binder.bind(emailField, User::getEmail, User::setEmail);
        binder.bind(passwordField, user -> "", (user, s) -> user.setEncodedPassword(userManager.encodePassword(s)));
        binder.bind(securityQuestionAnswerField, User::getSecurityQuestionAnswer, User::setSecurityQuestionAnswer);
        binder.bind(roleCB, User::getRole, User::setRole);
        binder.bind(activeCheckbox, User::isActive, User::setActive);
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
            // TODO Nutzer in die Datenbank speichern
            resetEditLayout();
            showNotification("Nutzer gespeichert");
        });
        cancelButton.addClickListener(clickEvent -> {
            resetEditLayout();
            showNotification("Bearbeitung abgebrochen");
        });
        return buttonLayout;
    }

    private void resetEditLayout() {
        userGrid.deselectAll();
        selectedUser = null;
        binder.readBean(new User());
        updateEditLayoutState();
    }

    private void updateEditLayoutState() {
        setEditLayoutEnabled(selectedUser != null);
    }

    private void setEditLayoutEnabled(boolean enabled) {
        editLayout.getChildren().forEach(component -> {
            if (component instanceof HasEnabled c) {
                c.setEnabled(enabled);
            }
        });
    }

    private static class UserFilter {
        private final GridListDataView<User> dataView;
        private String id;
        private String name;
        private String email;
        private String securityQuestionAnswer;
        private String role;
        private Boolean active;

        public UserFilter(GridListDataView<User> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public boolean test(User user) {
            boolean matchesId = matches(String.valueOf(user.getId()), id);
            boolean matchesName = matches(user.getName(), name);
            boolean matchesEmail = matches(user.getEmail(), email);
            boolean matchesAnswer = matches(user.getSecurityQuestionAnswer(), securityQuestionAnswer);
            boolean matchesRole = matches(user.getRole(), role);
            boolean matchesActive = active == null || active == user.isActive();
            return matchesId && matchesName && matchesEmail && matchesAnswer && matchesRole && matchesActive;
        }

        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        public void setEmail(String email) {
            this.email = email;
            dataView.refreshAll();
        }

        public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
            this.securityQuestionAnswer = securityQuestionAnswer;
            dataView.refreshAll();
        }

        public void setRole(String role) {
            this.role = role;
            dataView.refreshAll();
        }

        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}