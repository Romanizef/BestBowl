package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.softwareprojekt.bestbowl.beans.UserManager;
import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.jpa.repositories.BowlingCenterRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.UserRepository;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Marten Voß
 */
@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm loginForm = new LoginForm();
    private final Dialog passwordResetDialog;
    private final transient UserManager userManager;
    private final transient UserRepository userRepository;
    private User selectedUserForPasswordReset = null;
    private int passwordResetDialogStage = 1;

    @Autowired
    public LoginView(UserManager userManager, UserRepository userRepository,
                     BowlingCenterRepository bowlingCenterRepository) {
        this.userManager = userManager;
        this.userRepository = userRepository;
        addClassName("login-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        passwordResetDialog = createPasswordResetDialog();
        LoginI18n loginI18n = LoginI18n.createDefault();
        LoginI18n.Form loginI18nForm = loginI18n.getForm();
        loginI18nForm.setTitle("Anmelden");
        loginI18nForm.setUsername("Benutzername");
        loginI18nForm.setPassword("Passwort");
        loginI18nForm.setSubmit("Anmelden");
        loginI18nForm.setForgotPassword("Passwort vergessen?");
        LoginI18n.ErrorMessage loginI18nErrorMessage = loginI18n.getErrorMessage();
        loginI18nErrorMessage.setTitle("Benutzername oder Passwort falsch");
        loginI18nErrorMessage.setMessage("Überprüfe deine eingegebenen Daten und versuche es erneut.");
        loginForm.setI18n(loginI18n);
        loginForm.setAction("login");
        loginForm.addForgotPasswordListener(e -> passwordResetDialog.open());
        add(new H1(bowlingCenterRepository.getBowlingCenter().getDisplayName()), loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }

    private Dialog createPasswordResetDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("350px");
        dialog.setHeaderTitle("Passwort zurücksetzen Dialog");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(true);
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        TextField userNameField = new TextField("Benutzername");
        userNameField.setWidthFull();
        TextField answerField = new TextField("answer");
        answerField.setWidthFull();
        PasswordField passwordField1 = new PasswordField("Neues Passwort");
        passwordField1.setWidthFull();
        PasswordField passwordField2 = new PasswordField("Neues Passwort wiederholen");
        passwordField2.setWidthFull();
        Label errorLabel = new Label();
        errorLabel.getStyle().set("color", "red");
        layout.add(userNameField, answerField, passwordField1, passwordField2, errorLabel);
        dialog.add(layout);
        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        Button cancelButton = new Button("Abbrechen");
        Button continueButton = new Button("Weiter");
        continueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        footerLayout.add(cancelButton, continueButton);
        footerLayout.setFlexGrow(1, cancelButton, continueButton);
        dialog.getFooter().add(footerLayout);

        Runnable stageOneAction = () -> {
            String userName = userNameField.getValue();
            if (Utils.isStringNotEmpty(userName)) {
                Optional<User> optionalUser = userRepository.findByName(userName);
                optionalUser.ifPresent(user -> {
                    selectedUserForPasswordReset = user;
                    passwordResetDialogStage = 2;
                });
            }
            if (selectedUserForPasswordReset == null) {
                errorLabel.setText("Der Benutzer existiert nicht!");
            } else {
                userNameField.setValue("");
                userNameField.setVisible(false);
                answerField.setVisible(true);
                answerField.setLabel(selectedUserForPasswordReset.getSecurityQuestion());
                errorLabel.setText("");
            }
        };
        Runnable stageTwoAction = () -> {
            String answer = answerField.getValue();
            if (Utils.isStringNotEmpty(answer) && answer.equals(selectedUserForPasswordReset.getSecurityQuestionAnswer())) {
                passwordResetDialogStage = 3;
                answerField.setValue("");
                answerField.setVisible(false);
                passwordField1.setVisible(true);
                passwordField2.setVisible(true);
                continueButton.setText("Sichern");
                errorLabel.setText("");
            } else {
                errorLabel.setText("Falsche Antwort!");
            }
        };
        Runnable saveAction = () -> {
            if (selectedUserForPasswordReset == null || passwordResetDialogStage != 3) {
                Notifications.showError("Kein Benutzer ausgewählt, Rufe den Dialog neu auf!");
            } else {
                String password1 = passwordField1.getValue();
                String password2 = passwordField2.getValue();
                if (Utils.isStringNotEmpty(password1, password2) && password1.equals(password2)) {
                    userManager.changePassword(selectedUserForPasswordReset, password1);
                    passwordField1.setValue("");
                    passwordField2.setValue("");
                    selectedUserForPasswordReset = null;
                    passwordResetDialogStage = 1;
                    dialog.close();
                    Notifications.showInfo("Passwort erfolgreich geändert!");
                } else {
                    errorLabel.setText("Passwörter müssen übereinstimmen!");
                }
            }
        };

        continueButton.addClickListener(e -> {
            if (e.isFromClient()) {
                if (passwordResetDialogStage == 1) {
                    stageOneAction.run();
                } else if (passwordResetDialogStage == 2) {
                    stageTwoAction.run();
                } else {
                    saveAction.run();
                }
            }
        });
        userNameField.addValueChangeListener(e -> {
            if (e.isFromClient())
                stageOneAction.run();
        });
        answerField.addValueChangeListener(e -> {
            if (e.isFromClient())
                stageTwoAction.run();
        });
        passwordField2.addValueChangeListener(e -> {
            if (e.isFromClient())
                saveAction.run();
        });

        cancelButton.addClickListener(e -> {
            if (e.isFromClient()) {
                selectedUserForPasswordReset = null;
                passwordResetDialogStage = 1;
                dialog.close();
            }
        });

        dialog.addOpenedChangeListener(e -> {
            selectedUserForPasswordReset = null;
            passwordResetDialogStage = 1;
            userNameField.setValue("");
            answerField.setValue("");
            passwordField1.setValue("");
            passwordField2.setValue("");
            userNameField.setVisible(true);
            answerField.setVisible(false);
            passwordField1.setVisible(false);
            passwordField2.setVisible(false);
            continueButton.setText("Weiter");
            errorLabel.setText("");
        });
        return dialog;
    }
}
