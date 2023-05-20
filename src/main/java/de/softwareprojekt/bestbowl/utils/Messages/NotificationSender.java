package de.softwareprojekt.bestbowl.utils.messages;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class NotificationSender {

    public NotificationSender() {
    }

    /**
     * Displays a message for 3000ms in the screen center.
     *
     * @param text the text to be displayed
     */
    public static void showNotification(String text) {
        showNotification(text, 3000);
    }

    /**
     * Displays a message for the given duration in the screen center.
     *
     * @param text the text to be displayed
     */
    public static void showNotification(String text, int durationInMs) {
        Notification notification = Notification.show(text, durationInMs, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    /**
     * Displays an error message for 3000ms in the screen middle.
     * @return {@code Notification}
     */
    public Notification showErrorNotification(String text) {
        Notification errorNotification = new Notification();
        errorNotification.setPosition(Position.MIDDLE);
        errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div notificationText = new Div(new Text(text));
        Icon icon = VaadinIcon.WARNING.create();

        HorizontalLayout layout = new HorizontalLayout(icon, notificationText);
        layout.setAlignItems(Alignment.CENTER);

        errorNotification.add(layout);
        errorNotification.setDuration(3000);
        errorNotification.open();
        return errorNotification;
    }
}
