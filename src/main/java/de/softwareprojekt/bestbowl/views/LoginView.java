package de.softwareprojekt.bestbowl.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.beans.UserManager;
import de.softwareprojekt.bestbowl.jpa.entities.User;
import de.softwareprojekt.bestbowl.utils.Utils;
import jakarta.annotation.Resource;

import java.util.Optional;

import static de.softwareprojekt.bestbowl.utils.VaadinUtils.showNotification;

/**
 * @author Marten Voß
 */
@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm loginForm = new LoginForm();
    private final Dialog passwordResetDialog;
    private User selectedUserForPasswordReset = null;
    @Resource
    private UserManager userManager;

    public LoginView() {
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
        add(new H1("BestBowl"), loginForm);
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
        dialog.setHeaderTitle("Passwort zurücksetzen");
        TextField userNameField = new TextField("Nutzername");
        TextField questionField = new TextField("Geburtstag");
        PasswordField passwordField1 = new PasswordField("Neues Passwort");
        PasswordField passwordField2 = new PasswordField("Neues Passwort wiederholen");
        Button cancelButton = new Button("Abbrechen");
        Button continueButton = new Button("Weiter");
        Button saveNewPasswordButton = new Button("Sichern");
        continueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveNewPasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        VerticalLayout layout = new VerticalLayout();
        layout.add(userNameField, questionField, passwordField1, passwordField2);
        dialog.add(layout);
        cancelButton.addClickListener(e -> {
            selectedUserForPasswordReset = null;
            dialog.close();
        });
        continueButton.addClickListener(e -> {
            String userName = userNameField.getValue();
            String answer = questionField.getValue();
            if (Utils.isStringNotEmpty(userName, answer)) {
                Optional<User> optionalUser = Repos.getUserRepository().findByName(userName);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    if (user.getSecurityQuestionAnswer().equals(answer)) {
                        selectedUserForPasswordReset = user;
                    }
                }
            }
            if (selectedUserForPasswordReset == null) {
                showNotification("Benutzername oder Antwort falsch");
            } else {
                userNameField.setValue("");
                questionField.setValue("");
                userNameField.setVisible(false);
                questionField.setVisible(false);
                passwordField1.setVisible(true);
                passwordField2.setVisible(true);
                continueButton.setVisible(false);
                saveNewPasswordButton.setVisible(true);
            }
        });
        saveNewPasswordButton.addClickListener(e -> {
            if (selectedUserForPasswordReset == null) {
                showNotification("Kein Benutzer ausgewählt");
            } else {
                String password1 = passwordField1.getValue();
                String password2 = passwordField2.getValue();
                if (Utils.isStringNotEmpty(password1, password2) && password1.equals(password2)) {
                    userManager.changePassword(selectedUserForPasswordReset, password1);
                    passwordField1.setValue("");
                    passwordField2.setValue("");
                    selectedUserForPasswordReset = null;
                    dialog.close();
                    showNotification("Passwort erfolgreich geändert");
                } else {
                    showNotification("Passwörter müssen übereinstimmen");
                }
            }
        });
        dialog.getFooter().add(cancelButton, continueButton, saveNewPasswordButton);
        dialog.addOpenedChangeListener(e -> {
            selectedUserForPasswordReset = null;
            userNameField.setValue("");
            questionField.setValue("");
            passwordField1.setValue("");
            passwordField2.setValue("");
            userNameField.setVisible(true);
            questionField.setVisible(true);
            passwordField1.setVisible(false);
            passwordField2.setVisible(false);
            continueButton.setVisible(true);
            saveNewPasswordButton.setVisible(false);
        });
        return dialog;
    }
}
