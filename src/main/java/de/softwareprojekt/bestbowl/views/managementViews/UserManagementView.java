package de.softwareprojekt.bestbowl.views.managementViews;

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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import de.softwareprojekt.bestbowl.beans.UserManager;
import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.enums.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.validators.UserValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.softwareprojekt.bestbowl.utils.Utils.isStringNotEmpty;
import static de.softwareprojekt.bestbowl.utils.Utils.matches;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.*;
import static de.softwareprojekt.bestbowl.utils.messages.Notifications.showInfo;

/**
 * @author Marten Voß
 */
@Route(value = "userManagement", layout = MainView.class)
@PageTitle("Nutzerverwaltung")
@RolesAllowed({UserRole.OWNER, UserRole.ADMIN})
public class UserManagementView extends VerticalLayout {
    private final transient UserRepository userRepository;
    private final transient AuthenticationContext authenticationContext;
    private final Binder<User> binder = new Binder<>();
    private Grid<User> userGrid;
    private FormLayout editLayout;
    private PasswordField passwordField;
    private Label validationErrorLabel;
    private User selectedUser = null;
    private boolean editingNewUser = false;
    @Resource
    private transient UserManager userManager;

    @Autowired
    public UserManagementView(UserRepository userRepository, AuthenticationContext authenticationContext) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
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
            editingNewUser = true;
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

        grid.addSelectionListener(e -> {
            if (e.isFromClient()) {
                Optional<User> optionalUser = e.getFirstSelectedItem();
                if (optionalUser.isPresent()) {
                    selectedUser = optionalUser.get();
                    binder.readBean(selectedUser);
                    editingNewUser = false;
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
        nameField.setRequiredIndicatorVisible(true);

        TextField emailField = new TextField("E-Mail");
        emailField.setWidthFull();
        emailField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        emailField.setRequiredIndicatorVisible(true);

        passwordField = new PasswordField("Passwort");
        passwordField.setWidthFull();
        passwordField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        TextField securityQuestionAnswerField = new TextField("Sicherheitsfragenantwort");
        securityQuestionAnswerField.setWidthFull();
        securityQuestionAnswerField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        securityQuestionAnswerField.setRequiredIndicatorVisible(true);

        ComboBox<String> roleCB = new ComboBox<>("Nutzerrolle");
        roleCB.setWidthFull();
        roleCB.setAllowCustomValue(false);
        roleCB.setItems(UserRole.getAllValues());
        roleCB.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        roleCB.setRequiredIndicatorVisible(true);

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

        layout.add(nameField, emailField, passwordField, securityQuestionAnswerField, roleCB, checkboxLayout,
                createValidationLabelLayout(), buttonLayout);

        saveButton.addClickListener(clickEvent -> {
            User uneditedUser = new User(selectedUser);
            if (Utils.isStringNotEmpty(passwordField.getValue())) {
                //saving with a password change
                if (!isCurrentUserInRole(authenticationContext, UserRole.ADMIN)) {
                    Notifications.showInfo("Das Passwort eines Nutzers kann nur als Admin geändert werden");
                    return;
                }
                if (writeBean()) {
                    if (validateUserSave()) {
                        saveToDbAndUpdateUserManager();
                    } else {
                        selectedUser.copyValuesOf(uneditedUser);
                    }
                }
            } else {
                //saving without password change
                if (writeBean()) {
                    if (validateUserSave()) {
                        selectedUser.setEncodedPassword(uneditedUser.getEncodedPassword());
                        saveToDbAndUpdateUserManager();
                    } else {
                        selectedUser.copyValuesOf(uneditedUser);
                    }
                }
            }
        });
        cancelButton.addClickListener(clickEvent -> resetEditLayout());

        binder.withValidator(new UserValidator());
        binder.bind(nameField, User::getName, User::setName);
        binder.bind(emailField, User::getEmail, User::setEmail);
        binder.bind(passwordField, user -> "", (user, s) -> user.setEncodedPassword(userManager.encodePassword(s)));
        binder.bind(securityQuestionAnswerField, User::getSecurityQuestionAnswer, User::setSecurityQuestionAnswer);
        binder.bind(roleCB, User::getRole, User::setRole);
        binder.bind(activeCheckbox, User::isActive, User::setActive);
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
            binder.writeBean(selectedUser);
            return true;
        } catch (ValidationException e) {
            if (!e.getValidationErrors().isEmpty()) {
                validationErrorLabel.setText(e.getValidationErrors().get(0).getErrorMessage());
            }
        }
        return false;
    }

    private boolean validateUserSave() {
        //empty password check when creating a new user
        if (editingNewUser && !isStringNotEmpty(passwordField.getValue())) {
            validationErrorLabel.setText("Passwort darf nicht leer sein");
            return false;
        }
        //query the existing data on the db for the current user
        Optional<User> dbUser = userRepository.findById(selectedUser.getId());
        //admin checks
        if ((dbUser.isPresent() && dbUser.get().getRole().equals(UserRole.ADMIN)) || selectedUser.getRole().equals(UserRole.ADMIN)) {
            //admin users can only be updated by admins
            if (!isCurrentUserInRole(authenticationContext, UserRole.ADMIN)) {
                validationErrorLabel.setText("Nur Admins können Admin Nutzer verwalten");
                return false;
            }
            //there must be 1 active admin user in the system
            List<User> adminUserList = userRepository.findAllByRoleEquals(UserRole.ADMIN);
            if (adminUserList.size() == 1 && selectedUser.getId() == adminUserList.get(0).getId() && !selectedUser.isActive()) {
                validationErrorLabel.setText("Es muss 1 aktiver Admin Nutzer im System existieren");
                return false;
            }
        }
        //name and email duplicate checks
        Set<String> userNameSet = userRepository.findAllNames();
        Set<String> userEmailSet = userRepository.findAllEmails();
        //if the user exists in the db, delete the name and email of the unedited user from the sets
        dbUser.ifPresent(user -> {
            userNameSet.remove(user.getName());
            userEmailSet.remove(user.getEmail());
        });
        if (userNameSet.contains(selectedUser.getName())) {
            validationErrorLabel.setText("Ein Nutzer mit diesem Namen existiert bereits");
            return false;
        }
        if (userEmailSet.contains(selectedUser.getEmail())) {
            validationErrorLabel.setText("Diese E-Mail wird bereits verwendet");
            return false;
        }
        return true;
    }

    private void saveToDbAndUpdateUserManager() {
        userRepository.save(selectedUser);
        if (editingNewUser) {
            userGrid.getListDataView().addItem(selectedUser);
        } else {
            userGrid.getListDataView().refreshItem(selectedUser);
        }
        userManager.updateUsersFromDb();
        resetEditLayout();
        Notifications.showInfo("Nutzer gespeichert");
    }

    private void resetEditLayout() {
        userGrid.deselectAll();
        selectedUser = null;
        binder.readBean(new User());
        editingNewUser = false;
        updateEditLayoutState();
    }

    private void updateEditLayoutState() {
        passwordField.setRequiredIndicatorVisible(editingNewUser);
        validationErrorLabel.setText("");
        setChildrenEnabled(editLayout.getChildren(), selectedUser != null);
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
