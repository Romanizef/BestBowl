package de.softwareprojekt.bestbowl.utils.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;

import de.softwareprojekt.bestbowl.jpa.entities.bowling_alley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.utils.pdf.PDFUtils;

import java.io.ByteArrayInputStream;

/**
 * The InvoiceDownloadButton class creates a download button.
 *
 * @author Marten Voß
 */
public class InvoiceDownloadButton extends Button {
    private byte[] pdfContent;

    /**
     * Creates a button that wraps an Anchor for a file download
     * The content only gets created after the first button press and is cached for consecutive clicks
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
                    String fileName = "Rechnung_" + booking.getId() + ".pdf";
                    if (pdfContent == null) {
                        pdfContent = PDFUtils.createInvoicePdf(booking);
                    }
                    ByteArrayInputStream pdfContentStream = new ByteArrayInputStream(pdfContent);
                    StreamResource streamResource = new StreamResource(fileName, () -> pdfContentStream);
                    Anchor anchor = new Anchor();
                    anchor.setHref(streamResource);
                    anchor.getElement().setAttribute("download", fileName);
                    parent.getElement().appendChild(anchor.getElement());
                    anchor.getElement().callJsFunction("click");
                });
            }
        });
    }
}