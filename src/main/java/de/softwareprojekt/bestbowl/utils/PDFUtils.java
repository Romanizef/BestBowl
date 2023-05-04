package de.softwareprojekt.bestbowl.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Color;

import de.softwareprojekt.bestbowl.jpa.entities.Client;

import java.io.File;
import java.io.IOException;

/**
 * @author Matija Kopschek
 * @author Marten Voß
 */
public class PDFUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDFUtils.class);
    private Client client;

    /**
     * 
     */
    public PDFUtils() {
    }


    /**
     * @throws IOException
     */
    public void createInvoicePdf() throws IOException {
        // create "test" folder in working dir
        File testFolder = new File(Utils.getWorkingDirPath() + File.separator + "rechnung");
        if (!Utils.createDirectoryIfMissing(testFolder)) {
            return;
        }

        try {
            File pdfFile = new File(testFolder.getAbsolutePath() + File.separator + "rechnungs.pdf");
            PDDocument document = new PDDocument();

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // int pageWidth = (int) page.getTrimBox().getWidth(); // get width of the page
            int pageHeight = (int) page.getTrimBox().getHeight(); // get height of the page

            contentStream.setStrokingColor(Color.BLUE);
            contentStream.setLineWidth(1);

            int initRectCoordinateX = 50;
            int initRectCoordinateY = pageHeight - 70;

            int cellHeight = 20;
            int cellWidth = 150;

            int colCount = 2;
            int rowCount = 5;

            for (int i = 1; i <= rowCount; i++) {
                for (int j = 1; j <= colCount; j++) {
                    if (j == 2) {
                        contentStream.addRect(initRectCoordinateX, initRectCoordinateY, cellWidth, cellHeight);
                        initRectCoordinateX += cellWidth;
                    } else {
                        contentStream.addRect(initRectCoordinateX, initRectCoordinateY, cellWidth, cellHeight);
                        initRectCoordinateX += cellWidth;
                    }
                }
                initRectCoordinateX = 50;
                initRectCoordinateY -= cellHeight;
            }

            int initTextCoordinateX = 50;

            // Überschrift
            contentStream.beginText();
            contentStream.newLineAtOffset(initTextCoordinateX - 20, pageHeight - 30);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.showText("BestBowl Rechnung");
            contentStream.endText();

            // Tabelleninhalt

            // Linke Spalte
            contentStream.beginText();
            contentStream.setLeading(20f);
            contentStream.setFont(PDType1Font.TIMES_BOLD, 10);
            contentStream.newLineAtOffset(initTextCoordinateX + 5, pageHeight - 64);

            contentStream.showText("Rechnungsnummer:");

            contentStream.newLine();
            contentStream.showText("Kundenid:");

            contentStream.newLine();
            contentStream.showText("Kunden Nachname:");

            contentStream.newLine();
            contentStream.showText("Datum:");

            contentStream.newLine();
            contentStream.showText("Summe:");
            contentStream.endText();

            // Rechte Spalte
            contentStream.beginText();
            contentStream.newLineAtOffset(initTextCoordinateX + 160, pageHeight - 65);
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
            contentStream.showText("!");

            contentStream.newLine();
            contentStream.showText("12345");

            contentStream.newLine();
            contentStream.showText("Max Mustermann");

            contentStream.newLine();
            contentStream.showText("01.01.2023");

            contentStream.newLine();
            contentStream.showText("25,50");
            contentStream.endText();

            // Impressum
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 7);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(initTextCoordinateX - 30, pageHeight - 700);
            contentStream.showText("BestBowl GmbH");
            contentStream.newLine();
            contentStream.showText("Interaktion 1");
            contentStream.newLine();
            contentStream.showText("33619 Bielefeld");
            contentStream.newLine();
            contentStream.showText("Deutschland");
            contentStream.newLine();
            contentStream.showText("E-Mail: bestbowl11@gmail.com");
            contentStream.endText();

            contentStream.stroke();
            contentStream.close();

            document.save(pdfFile);
            document.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
