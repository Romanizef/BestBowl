package de.softwareprojekt.bestbowl.utils.Messages;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ErrorNotification extends Notification {
    private String text;
    private Notification errorNotification;

    public ErrorNotification() {
    }

    public final Notification showErrorNotification() {
        errorNotification = new Notification();
        errorNotification.setPosition(Position.MIDDLE);
        errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(getText()));
        Icon icon = VaadinIcon.WARNING.create();

        HorizontalLayout layout = new HorizontalLayout(icon, text);
        layout.setAlignItems(Alignment.CENTER);

        errorNotification.add(layout);
        errorNotification.setDuration(3000);
        errorNotification.open();
        return errorNotification;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
