package de.softwareprojekt.bestbowl.utils;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.Client;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;

/**
 * @author Matija Kopschek
 * @author Marten Voß
 */
public class PDFUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDFUtils.class);
    private static final int CELL_HEIGT = 20;
    private static final int CELL_WIDTH_POS = 10;
    private static final int CELL_WIDTH_DISCRIPTION = 150;
    private static final int CELL_WIDTH_AMOUNT = 10;
    private static final int CELL_WIDTH_PRICE = 30;
    private static final int CELL_WIDTH_DISCOUNT = 30;
    private static final int CELL_WIDTH_TOTAL = 30;
    private static final int TEXT_OFFSET = 50;
    private static final int POSITIONS_PER_PAGE = 10;

    private PDFUtils() {
    }

    public static byte[] createInvoicePdf(BowlingAlleyBooking booking) {
        // get data from DB
        BowlingCenter bowlingCenter = Repos.getBowlingCenterRepository().getBowlingCenter();
        List<DrinkBooking> drinkBookingList = Repos.getDrinkBookingRepository()
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(booking.getClient(),
                        booking.getBowlingAlley(), booking.getStartTime());
        List<FoodBooking> foodBookingList = Repos.getFoodBookingRepository()
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(booking.getClient(),
                        booking.getBowlingAlley(), booking.getStartTime());
        List<BowlingShoeBooking> shoeBookingList = Repos.getBowlingShoeBookingRepository()
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(booking.getClient(),
                        booking.getBowlingAlley(), booking.getStartTime());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument();) {
            int totalPositions = drinkBookingList.size() + foodBookingList.size() + shoeBookingList.size();
            int pages = (totalPositions / POSITIONS_PER_PAGE) + 1;
            int positionIndex = 0;
            int tableFooterOffset = (totalPositions % POSITIONS_PER_PAGE) * CELL_HEIGT;
            for (int i = 1; i <= pages; i++) {
                PDPage pdPage = createPage(booking, bowlingCenter, document, i);

                if (positionIndex < totalPositions) {
                    List<InvoiceLine> invoiceLineList = createInvoiceLineList(drinkBookingList, foodBookingList,
                            shoeBookingList, positionIndex);
                    createTableText(document, pdPage, invoiceLineList);
                    positionIndex += POSITIONS_PER_PAGE;
                }
                if (positionIndex < totalPositions) {
                    createTableFooter(document, pdPage, tableFooterOffset);
                }
                document.addPage(pdPage);
            }
            document.save(out);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return out.toByteArray();
    }

    private static List<InvoiceLine> createInvoiceLineList(List<DrinkBooking> drinkBookingList,
            List<FoodBooking> foodBookingList, List<BowlingShoeBooking> shoeBookingList, int positionIndex) {
        int offset = 0;
        List<InvoiceLine> invoiceLineList = new ArrayList<>(POSITIONS_PER_PAGE);
        for (int i = 0; i < POSITIONS_PER_PAGE; i++) {
            if (positionIndex + i < drinkBookingList.size()) {
                DrinkBooking drinkBooking = drinkBookingList.get(positionIndex + i);
                InvoiceLine invoiceLine = new InvoiceLine("" + positionIndex + 1, drinkBooking.getName(),
                        "" + drinkBooking.getAmount(), "" + drinkBooking.getPrice());
                invoiceLineList.add(invoiceLine);
            }
            offset += drinkBookingList.size();
            if (positionIndex + i < foodBookingList.size() + offset) {
                FoodBooking foodBooking = foodBookingList.get(positionIndex + i + offset);
                InvoiceLine invoiceLine = new InvoiceLine("" + positionIndex + 1, foodBooking.getName(),
                        "" + foodBooking.getAmount(), "" + foodBooking.getPrice());
                invoiceLineList.add(invoiceLine);
            }
            offset += foodBookingList.size();
            if (positionIndex + i < shoeBookingList.size() + offset) {
                BowlingShoeBooking shoeBooking = shoeBookingList.get(positionIndex + i + offset);
                InvoiceLine invoiceLine = new InvoiceLine("" + positionIndex + 1,
                        "Schuhgröße: " + shoeBooking.getBowlingShoe().getSize(),
                        "1", "bla"); // TODO Schupreis noch ergänzen
                invoiceLineList.add(invoiceLine);
            }
        }
        return invoiceLineList;
    }

    private static void createTableText(PDDocument document, PDPage pdPage, List<InvoiceLine> invoiceLineList) {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, pdPage)) {
            // int pageWidth = (int) page.getTrimBox().getWidth(); // get width of the page
            int pageHeight = (int) pdPage.getTrimBox().getHeight(); // get height of the page

            createTable(contentStream, pageHeight, invoiceLineList.size());

            // Tabelleninhalt
            // Positionsnummer
            contentStream.beginText();
            contentStream.setLeading(20f);
            contentStream.setFont(PDType1Font.TIMES_BOLD, 10);
            // contentStream.newLineAtOffset(initTextCoordinateX + 5, pageHeight - 64);

            // Rechte Spalte
            contentStream.beginText();
            contentStream.newLineAtOffset(TEXT_OFFSET + 160, pageHeight - 65);
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

            contentStream.stroke();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static void createTable(PDPageContentStream contentStream, int pageHeight, int rowCount)
            throws IOException {
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.setLineWidth(1);

        // TabellenHeader
        int offset = TEXT_OFFSET + 5;
        contentStream.beginText();
        contentStream.setLeading(20f);
        contentStream.setFont(PDType1Font.TIMES_BOLD, 10);
        contentStream.newLineAtOffset(offset, pageHeight - 100);
        contentStream.showText("PosNr");

        offset += CELL_WIDTH_POS;
        contentStream.newLineAtOffset(offset, pageHeight - 100);
        contentStream.showText("Beschreibung");

        offset += CELL_WIDTH_DISCRIPTION;
        contentStream.newLineAtOffset(offset, pageHeight - 100);
        contentStream.showText("Anzahl");

        offset += CELL_WIDTH_AMOUNT;
        contentStream.newLineAtOffset(offset, pageHeight - 100);
        contentStream.showText("Preis");
        contentStream.endText();

        int initRectCoordinateX = 50;
        int initRectCoordinateY = pageHeight - 70;

        for (int i = 1; i <= rowCount; i++) {
            contentStream.addRect(initRectCoordinateX, initRectCoordinateY, CELL_WIDTH_POS, CELL_HEIGT);
            initRectCoordinateX += CELL_WIDTH_POS;
            contentStream.addRect(initRectCoordinateX, initRectCoordinateY, CELL_WIDTH_DISCRIPTION, CELL_HEIGT);
            initRectCoordinateX += CELL_WIDTH_DISCRIPTION;
            contentStream.addRect(initRectCoordinateX, initRectCoordinateY, CELL_WIDTH_AMOUNT, CELL_HEIGT);
            initRectCoordinateX += CELL_WIDTH_AMOUNT;
            contentStream.addRect(initRectCoordinateX, initRectCoordinateY, CELL_WIDTH_PRICE, CELL_HEIGT);
            initRectCoordinateX += CELL_WIDTH_PRICE;

            initRectCoordinateX = 50;
            initRectCoordinateY -= CELL_HEIGT;
        }
    }

    private static void createTableFooter(PDDocument document, PDPage pdPage, int tableFooterOffset) {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, pdPage)) {
            // int pageWidth = (int) page.getTrimBox().getWidth(); // get width of the page
            int pageHeight = (int) pdPage.getTrimBox().getHeight(); // get height of the page

            // TextFooter
            // TODO Summe und Rabatt Textfelder und Tabelle
            contentStream.beginText();
            contentStream.setLeading(20f);
            contentStream.setFont(PDType1Font.TIMES_BOLD, 10);
            // contentStream.newLineAtOffset(initTextCoordinateX + 5, pageHeight - 64);
            contentStream.endText();
            contentStream.stroke();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static PDPage createPage(BowlingAlleyBooking booking, BowlingCenter bowlingCenter, PDDocument document,
            int pageNumber) {
        PDPage page = new PDPage();
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // int pageWidth = (int) page.getTrimBox().getWidth(); // get width of the page
            int pageHeight = (int) page.getTrimBox().getHeight(); // get height of the page

            contentStream.setStrokingColor(Color.BLUE);
            contentStream.setLineWidth(1);

            // Header
            // Überschrift
            contentStream.beginText();
            contentStream.newLineAtOffset(TEXT_OFFSET - 20, pageHeight - 30);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.showText("BestBowl Rechnung");
            contentStream.endText();

            // Kundendaten
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(TEXT_OFFSET, pageHeight - 64);
            contentStream.showText("Rechnungsempfänger");
            contentStream.newLine();
            contentStream.showText(booking.getClient().getFullName());
            contentStream.newLine();
            contentStream.showText("" + booking.getClient().getAddress().getStreet());
            contentStream.showText("" + booking.getClient().getAddress().getHouseNr());
            contentStream.newLine();
            contentStream.showText("" + booking.getClient().getAddress().getPostCode());
            contentStream.showText("" + booking.getClient().getAddress().getCity());
            contentStream.endText();

            // Rechnugsnummer etc.
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(TEXT_OFFSET + 400, pageHeight - 64);
            contentStream.showText("" + booking.getId());
            contentStream.newLine();
            contentStream.showText("" + booking.getClient().getId());
            contentStream.newLine();
            contentStream.showText("" + Utils.toDateOnlyString(booking.getStartTime()));
            contentStream.endText();

            // Footer
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 7);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(TEXT_OFFSET - 30, pageHeight - 700);
            contentStream.showText(bowlingCenter.getBusinessName());
            contentStream.newLine();
            contentStream.showText(bowlingCenter.getStreet());
            contentStream.newLine();
            contentStream.showText(bowlingCenter.getPostCode() + " " + bowlingCenter.getCity());
            contentStream.newLine();
            contentStream.showText("Deutschland");
            contentStream.newLine();
            contentStream.showText(bowlingCenter.getEmail());
            contentStream.endText();

            contentStream.stroke();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return page;
    }

    private record InvoiceLine(String posnr, String description, String amount, String price) {
    };

}
