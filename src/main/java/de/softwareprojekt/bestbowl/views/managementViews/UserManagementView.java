package de.softwareprojekt.bestbowl.views.managementViews;

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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.softwareprojekt.bestbowl.beans.SecurityService;
import de.softwareprojekt.bestbowl.beans.UserManager;
import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.constants.UserRole;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.utils.validators.UserValidator;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.Resource;
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
 * @author Marten Voß
 */
@Route(value = "userManagement", layout = MainView.class)
@PageTitle("Benutzerverwaltung")
@RolesAllowed({UserRole.OWNER})
public class UserManagementView extends VerticalLayout {
    private final transient UserRepository userRepository;
    private final transient SecurityService securityService;
    private final Binder<User> binder = new Binder<>();
    private Grid<User> userGrid;
    private FormLayout editLayout;
    private PasswordField passwordField;
    private Label validationErrorLabel;
    private User selectedUser = null;
    private boolean editingNewUser = false;
    @Resource
    private transient UserManager userManager;

    /**
     * The UserManagementView function is responsible for creating the user
     * management view.
     * It creates a new user button, a grid layout and adds them to the view.
     * The updateEditLayoutState function is called to update the edit layout state.
     *
     * @param userRepository
     * @param securityService
     */
    @Autowired
    public UserManagementView(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        setSizeFull();
        Button newUserButton = createNewUserButton();
        HorizontalLayout gridLayout = createGridLayout();
        add(newUserButton, gridLayout);
        updateEditLayoutState();
    }

    /**
     * The createNewUserButton function creates a new Button object with the text
     * &quot;Neuen Benutzer hinzufügen&quot; and adds it to the userGrid.
     * The button is set to be full width, has a primary theme variant and when
     * clicked will deselect all users in the grid, create a new User object called
     * selectedUser
     * which is then bound by binder.readBean(selectedUser) and sets editingNewUser
     * to true so that updateEditLayoutState() can be called on it.
     *
     * @return A button
     * @see #updateEditLayoutState()
     */
    private Button createNewUserButton() {
        Button button = new Button("Neuen Benutzer hinzufügen");
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

    /**
     * The createGridLayout function creates a HorizontalLayout, which is then
     * populated with the userGrid and editLayout.
     * The userGrid is created by calling the createGrid function, while the
     * editLayout is created by calling the createEditLayout function.
     *
     * @return A horizontallayout
     * @see #createGrid()
     * @see #createEditLayout()
     */
    private HorizontalLayout createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        userGrid = createGrid();
        editLayout = createEditLayout();
        layout.add(userGrid, editLayout);
        return layout;
    }

    /**
     * The createGrid function creates a grid that displays all users in the
     * database.
     * The grid is filterable by ID, name, email address, security question and
     * answer as well as user role and activity status.
     * The function also adds a selection listener to the grid which updates the
     * edit layout with information about the selected user when one is chosen from
     * within it.
     *
     * @return A grid, which is added to a vertical layout
     * @see #updateEditLayoutState()
     * @see #resetEditLayout()
     */
    private Grid<User> createGrid() {
        Grid<User> grid = new Grid<>(User.class);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.removeAllColumns();
        Grid.Column<User> idColumn = grid.addColumn("id").setHeader("ID");
        Grid.Column<User> nameColumn = grid.addColumn("name").setHeader("Name");
        Grid.Column<User> emailColumn = grid.addColumn("email").setHeader("E-Mail");
        Grid.Column<User> questionColumn = grid.addColumn("securityQuestion").setHeader("Sicherheitsfrage");
        Grid.Column<User> answerColumn = grid.addColumn("securityQuestionAnswer").setHeader("Sicherheitsfragenantwort");
        Grid.Column<User> roleColumn = grid.addColumn("role").setHeader("Benutzerrolle");
        Grid.Column<User> activeColumn = grid.addColumn(user -> user.isActive() ? "Aktiv" : "Inaktiv")
                .setHeader("Aktiv");
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
        headerRow.getCell(questionColumn)
                .setComponent(createFilterHeaderString("Sicherheitsfrage", userFilter::setSecurityQuestion));
        headerRow.getCell(answerColumn).setComponent(
                createFilterHeaderString("Sicherheitsfragenantwort", userFilter::setSecurityQuestionAnswer));
        headerRow.getCell(roleColumn).setComponent(createFilterHeaderString("Rolle", userFilter::setRole));
        headerRow.getCell(activeColumn)
                .setComponent(createFilterHeaderBoolean("Aktiv", "Inaktiv", userFilter::setActive));

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

    /**
     * The createEditLayout function creates a FormLayout that is used to edit the
     * selected user.
     * The layout contains all fields of the User class, except for the id and
     * encodedPassword field.
     * The save button saves changes made in this layout to the database and updates
     * them in UserManager.
     *
     * @return A formlayout
     * @see #writeBean()
     * @see #validateUserSave()
     * @see #resetEditLayout()
     * @see #saveToDbAndUpdateUserManager()
     */
    private FormLayout createEditLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidth("25%");

        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        nameField.setRequiredIndicatorVisible(true);
        nameField.setRequired(true);

        TextField emailField = new TextField("E-Mail");
        emailField.setWidthFull();
        emailField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        emailField.setRequiredIndicatorVisible(true);
        emailField.setRequired(true);

        passwordField = new PasswordField("Passwort");
        passwordField.setWidthFull();
        passwordField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        passwordField.setRequired(true);

        TextField securityQuestionField = new TextField("Sicherheitsfrage");
        securityQuestionField.setWidthFull();
        securityQuestionField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        securityQuestionField.setRequiredIndicatorVisible(true);
        securityQuestionField.setRequired(true);

        TextField securityQuestionAnswerField = new TextField("Sicherheitsfragenantwort");
        securityQuestionAnswerField.setWidthFull();
        securityQuestionAnswerField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        securityQuestionAnswerField.setRequiredIndicatorVisible(true);
        securityQuestionAnswerField.setRequired(true);

        Select<String> roleSelect = new Select<>();
        roleSelect.setLabel("Benutzerrolle");
        roleSelect.setWidthFull();
        roleSelect.setItems(UserRole.getAllValues());
        roleSelect.addThemeVariants(SelectVariant.LUMO_SMALL);
        roleSelect.setRequiredIndicatorVisible(true);

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

        layout.add(nameField, emailField, passwordField, securityQuestionField, securityQuestionAnswerField, roleSelect,
                checkboxLayout, createValidationLabelLayout(), buttonLayout);

        saveButton.addClickListener(clickEvent -> {
            User uneditedUser = new User(selectedUser);
            if (Utils.isStringNotEmpty(passwordField.getValue())) {
                // saving with a password change
                if (!securityService.isCurrentUserInRole(UserRole.ADMIN)) {
                    Notifications.showInfo("Das Passwort eines Benutzers kann nur als Admin geändert werden");
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
                // saving without password change
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
        binder.bind(securityQuestionField, User::getSecurityQuestion, User::setSecurityQuestion);
        binder.bind(securityQuestionAnswerField, User::getSecurityQuestionAnswer, User::setSecurityQuestionAnswer);
        binder.bind(roleSelect, User::getRole, User::setRole);
        binder.bind(activeCheckbox, User::isActive,
                (user, active) -> user.setActive(Objects.requireNonNullElse(active, false)));
        return layout;
    }

    /**
     * The createValidationLabelLayout function creates a VerticalLayout that
     * contains the validationErrorLabel.
     * The validationErrorLabel is used to display error messages when the user
     * tries to create an account with invalid input.
     *
     * @return A verticallayout
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
     * The writeBean function is used to write the values of a bean into the fields.
     * This function is called when a user clicks on &quot;Save&quot; in order to
     * save his changes.
     *
     * @return True if the user is valid, and false otherwise
     */
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

    /**
     * The validateUserSave function is used to validate the user data before saving
     * it.
     * It checks for duplicate names and emails, as well as if there is at least one
     * active admin in the system.
     *
     * @return True if the user is valid, and false otherwise
     */
    private boolean validateUserSave() {
        // empty password check when creating a new user
        if (editingNewUser && !isStringNotEmpty(passwordField.getValue())) {
            validationErrorLabel.setText("Passwort darf nicht leer sein");
            return false;
        }
        // query the existing data on the db for the current user
        Optional<User> dbUser = userRepository.findById(selectedUser.getId());
        // admin checks
        if ((dbUser.isPresent() && dbUser.get().getRole().equals(UserRole.ADMIN))
                || selectedUser.getRole().equals(UserRole.ADMIN)) {
            // admin users can only be updated by admins
            if (!securityService.isCurrentUserInRole(UserRole.ADMIN)) {
                validationErrorLabel.setText("Nur Admins können Admin Benutzer verwalten");
                return false;
            }
            // there must be 1 active admin user in the system
            List<User> adminUserList = userRepository.findAllByRoleEquals(UserRole.ADMIN);
            if (adminUserList.size() == 1 && selectedUser.getId() == adminUserList.get(0).getId()
                    && !selectedUser.isActive()) {
                validationErrorLabel.setText("Es muss 1 aktiver Admin Benutzer im System existieren");
                return false;
            }
        }
        // name and email duplicate checks
        Set<String> userNameSet = userRepository.findAllNames();
        Set<String> userEmailSet = userRepository.findAllEmails();
        // if the user exists in the db, delete the name and email of the unedited user
        // from the sets
        dbUser.ifPresent(user -> {
            userNameSet.remove(user.getName());
            userEmailSet.remove(user.getEmail());
        });
        if (userNameSet.contains(selectedUser.getName())) {
            validationErrorLabel.setText("Ein Benutzer mit diesem Namen existiert bereits");
            return false;
        }
        if (userEmailSet.contains(selectedUser.getEmail())) {
            validationErrorLabel.setText("Diese E-Mail wird bereits verwendet");
            return false;
        }
        return true;
    }

    /**
     * The saveToDbAndUpdateUserManager function saves the selected user to the
     * database and updates
     * the UserManager with all users from the database. It also resets editLayout,
     * which is used for
     * editing a user's information. Finally, it shows an info notification that
     * says &quot;Benutzer gespeichert&quot; (User saved).
     */
    private void saveToDbAndUpdateUserManager() {
        userRepository.save(selectedUser);
        if (editingNewUser) {
            userGrid.getListDataView().addItem(selectedUser);
        } else {
            userGrid.getListDataView().refreshItem(selectedUser);
        }
        userManager.updateUsersFromDb();
        resetEditLayout();
        Notifications.showInfo("Benutzer gespeichert");
    }

    /**
     * The resetEditLayout function resets the edit layout to its default state.
     * This means that all fields are cleared and the user grid is deselected.
     *
     * @see #updateEditLayoutState()
     */
    private void resetEditLayout() {
        userGrid.deselectAll();
        selectedUser = null;
        binder.readBean(new User());
        editingNewUser = false;
        updateEditLayoutState();
    }

    /**
     * The updateEditLayoutState function is used to update the state of the edit
     * layout.
     * It sets the password field as required if a new user is being edited, and it
     * enables or disables all children of
     * editLayout depending on whether a user has been selected.
     */
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
        private String securityQuestion;
        private String securityQuestionAnswer;
        private String role;
        private Boolean active;

        /**
         * The UserFilter function is used to filter the GridListDataView of Users.
         * It filters by username, firstname, lastname and role.
         *
         * @param dataView
         */
        public UserFilter(GridListDataView<User> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        /**
         * The test function is used to filter the users in the grid.
         * It checks if a user matches all of the given filters.
         * If no filter is set, it will return true for every user.
         *
         * @param user Compare the user object with the search criteria
         * @return True if all the fields match
         */
        public boolean test(User user) {
            boolean matchesId = matches(String.valueOf(user.getId()), id);
            boolean matchesName = matches(user.getName(), name);
            boolean matchesEmail = matches(user.getEmail(), email);
            boolean matchesQuestion = matches(user.getSecurityQuestion(), securityQuestion);
            boolean matchesAnswer = matches(user.getSecurityQuestionAnswer(), securityQuestionAnswer);
            boolean matchesRole = matches(user.getRole(), role);
            boolean matchesActive = active == null || active == user.isActive();
            return matchesId && matchesName && matchesEmail && matchesQuestion && matchesAnswer
                    && matchesRole && matchesActive;
        }

        /**
         * The setId function sets the id of a user.
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
            dataView.refreshAll();
        }

        /**
         * The setName function sets the name of a user.
         *
         * @param name
         */
        public void setName(String name) {
            this.name = name;
            dataView.refreshAll();
        }

        /**
         * The setEmail function sets the email of a user.
         *
         * @param email
         */
        public void setEmail(String email) {
            this.email = email;
            dataView.refreshAll();
        }

        /**
         * The setSecurityQuestion function sets the security question of a user.
         *
         * @param securityQuestion
         */
        public void setSecurityQuestion(String securityQuestion) {
            this.securityQuestion = securityQuestion;
            dataView.refreshAll();
        }

        /**
         * The setSecurityQuestionAnswer function sets the securityQuestionAnswer
         * variable to the value of its parameter.
         *
         * @param securityQuestionAnswer
         */
        public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
            this.securityQuestionAnswer = securityQuestionAnswer;
            dataView.refreshAll();
        }

        /**
         * The setRole function is used to set the role of a user.
         *
         * @param role
         */
        public void setRole(String role) {
            this.role = role;
            dataView.refreshAll();
        }

        /**
         * The setActive function is used to set the active status of a user.
         *
         * @param active
         */
        public void setActive(Boolean active) {
            this.active = active;
            dataView.refreshAll();
        }
    }
}