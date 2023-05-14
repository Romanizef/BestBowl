package de.softwareprojekt.bestbowl.utils.other;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConfirmationDialog extends Div {

    public ConfirmationDialog(String confirmationQuestion, String confirmAnswer, String cancelAnswer) {
        AtomicBoolean dialogAnswer = new AtomicBoolean(false);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Bestätigen");
        dialog.setText(confirmationQuestion);

        dialog.setCancelable(true);
        dialog.setCancelText(cancelAnswer);
        dialog.addCancelListener(event -> dialogAnswer.set(false));

        dialog.setConfirmText(confirmAnswer);
        dialog.addConfirmListener(event -> dialogAnswer.set(true));

        dialog.open();


    }

}
