package de.softwareprojekt.bestbowl.utils.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.utils.PDFUtils;

import java.io.ByteArrayInputStream;

/**
 * @author Marten Voß
 */
public class InvoiceDownloadButton extends Button {
    private Anchor anchor = null;

    /**
     * Creates a button that wraps an Anchor which gets created only the first time the button is clicked.
     * For all consecutive clicks the anchor and content is cached
     *
     * @param booking
     */
    public InvoiceDownloadButton(BowlingAlleyBooking booking) {
        super();
        super.setIcon(VaadinIcon.DOWNLOAD.create());
        super.addThemeVariants(ButtonVariant.LUMO_SMALL);
        super.addClickListener(e -> {
            if (e.isFromClient()) {
                getParent().ifPresent(parent -> {
                    if (anchor == null) {
                        String fileName = "Rechnung_" + booking.getId() + ".pdf";
                        byte[] pdfContent = PDFUtils.createInvoicePdf(booking);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfContent);
                        StreamResource streamResource = new StreamResource(fileName, () -> inputStream);
                        anchor = new Anchor();
                        anchor.setHref(streamResource);
                        anchor.getElement().setAttribute("download", fileName);
                        anchor.getStyle().set("display", "none");
                        parent.getElement().appendChild(anchor.getElement());
                    }
                });
                if (anchor != null) {
                    anchor.getElement().callJsFunction("click");
                }
            }
        });
    }
}
