package de.softwareprojekt.bestbowl.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class VaadinUtils {
    private VaadinUtils() {
    }

    /**
     * Displays a message for 3000ms in the screen center.
     *
     * @param text the text to be displayed
     */
    public static void showNotification(String text) {
        Notification notification = Notification.show(text, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
