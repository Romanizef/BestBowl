package de.softwareprojekt.bestbowl.utils.pdf;

import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.WHITE;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD_OBLIQUE;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_OBLIQUE;
import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.HorizontalAlignment.LEFT;
import static org.vandeseer.easytable.settings.HorizontalAlignment.RIGHT;
import static org.vandeseer.easytable.settings.VerticalAlignment.TOP;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.FoodBooking;
import de.softwareprojekt.bestbowl.utils.Utils;

/**
 * @author Matija Kopschek
 * @author Marten Voß
 */
public class PDFUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDFUtils.class);
    private static final int CELL_HEIGT = 20;
    private static final int TEXT_OFFSET = 50;
    private static final int POSITIONS_PER_PAGE = 10;

    private final static Color BLUE_DARK = new Color(76, 129, 190);
    private final static Color BLUE_LIGHT_1 = new Color(186, 206, 230);
    private final static Color BLUE_LIGHT_2 = new Color(218, 230, 242);

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
                // PDPage pdPage = createPage(booking, bowlingCenter, document, i);
                if (positionIndex < totalPositions) {
                    List<InvoiceLine> invoiceLineList = createInvoiceLineList(drinkBookingList,
                            foodBookingList,
                            shoeBookingList, positionIndex);
                    PDPage pdPage = createPage(booking, bowlingCenter, document, i, tableFooterOffset, invoiceLineList);
                    positionIndex += POSITIONS_PER_PAGE;
                    document.addPage(pdPage);
                }
            }
            document.save(out);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return out.toByteArray();
    }

    private static List<InvoiceLine> createInvoiceLineList(List<DrinkBooking> drinkBookingList,
            List<FoodBooking> foodBookingList, List<BowlingShoeBooking> shoeBookingList, int positionIndex) {
        List<InvoiceLine> invoiceLineList = new ArrayList<>(POSITIONS_PER_PAGE);
        int i = 0;
        do {
            if (positionIndex + i < drinkBookingList.size()) {
                DrinkBooking drinkBooking = drinkBookingList.get(positionIndex + i);
                InvoiceLine invoiceLine = new InvoiceLine("" + positionIndex + 1, drinkBooking.getName(),
                        "" + drinkBooking.getAmount(), "" + drinkBooking.getPrice());
                invoiceLineList.add(invoiceLine);
            }
            if (positionIndex + i < foodBookingList.size()) {
                FoodBooking foodBooking = foodBookingList.get(positionIndex + i);
                InvoiceLine invoiceLine = new InvoiceLine("" + positionIndex + 1, foodBooking.getName(),
                        "" + foodBooking.getAmount(), "" + foodBooking.getPrice());
                invoiceLineList.add(invoiceLine);
            }
            if (positionIndex + i < shoeBookingList.size()) {
                BowlingShoeBooking shoeBooking = shoeBookingList.get(positionIndex + i);
                InvoiceLine invoiceLine = new InvoiceLine("" + positionIndex + 1,
                        "Schuhgröße: " + shoeBooking.getBowlingShoe().getSize(),
                        "" + shoeBooking.getBowlingShoe().getId(), "" + shoeBooking.getPrice());
                invoiceLineList.add(invoiceLine);
            }
            i++;
        } while (i < POSITIONS_PER_PAGE);
        return invoiceLineList;
    }

    private static PDPage createPage(BowlingAlleyBooking booking, BowlingCenter bowlingCenter, PDDocument document,
            int pageNumber, int tableFooterOffset, List<InvoiceLine> invoiceLineList) {
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
            contentStream.newLineAtOffset(TEXT_OFFSET, pageHeight - 65);
            contentStream.showText("Rechnungsempfänger");
            contentStream.newLine();
            contentStream.showText(booking.getClient().getFullName());
            contentStream.newLine();
            contentStream.showText("" + booking.getClient().getAddress().getStreet());
            contentStream.showText(" " + booking.getClient().getAddress().getHouseNr());
            contentStream.newLine();
            contentStream.showText("" + booking.getClient().getAddress().getPostCode());
            contentStream.showText(" " + booking.getClient().getAddress().getCity());
            contentStream.endText();

            // Rechnugsnummer etc.
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(TEXT_OFFSET + 350, pageHeight - 65);
            contentStream.showText("Rechnungsnummer: " + booking.getId());
            contentStream.newLine();
            contentStream.showText("Kundennummer: " + booking.getClient().getId());
            contentStream.newLine();
            contentStream.showText("Datum: " + Utils.toDateOnlyString(booking.getStartTime()));
            contentStream.endText();

            // Tabelle

            // Set up the drawer
            TableDrawer tableDrawer = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(50f)
                    .startY(page.getMediaBox().getUpperRightY() - 140f)
                    .table(createSimpleExampleTable())
                    .build();
            tableDrawer.draw();
            // Ende Tabelle

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

            // Seitennummer
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 7);
            contentStream.newLineAtOffset(TEXT_OFFSET + 500, pageHeight - 750);
            contentStream.showText("Seite: " + pageNumber);
            contentStream.endText();

            contentStream.stroke();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return page;
    }

    private record InvoiceLine(String posnr, String description, String amount, String price) {
    };

    private static Table createSimpleExampleTable() {
        final TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(100, 200, 100, 100)
                .fontSize(10)
                .font(HELVETICA)
                .borderColor(Color.WHITE);

        // header row
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("Posnr").borderWidth(1).build())
                .add(TextCell.builder().text("Beschreibung").borderWidth(1).build())
                .add(TextCell.builder().text("Menge").borderWidth(1).build())
                .add(TextCell.builder().text("Preis").borderWidth(1).build())
                .backgroundColor(BLUE_DARK)
                .textColor(Color.WHITE)
                .font(HELVETICA_BOLD)
                .fontSize(12)
                .horizontalAlignment(CENTER)
                .build());

        // data rows
        Object[][] DATA = new Object[][] {
                { "1", 134.0, 145.0 },
                { "2", 768.0, 677.0 },
                { "3", 456.2, 612.0 },
                { "4", 302.3, 467.0 }
        };
        double grandTotal = 0;
        for (int i = 0; i < DATA.length; i++) {
            final Object[] dataRow = DATA[i];
            final double total = (double) dataRow[1] + (double) dataRow[2];
            grandTotal += total;

            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(String.valueOf(dataRow[0])).borderWidth(1)
                            .build())
                    .add(TextCell.builder().text(dataRow[1] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[2] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(total + " €").borderWidth(1).build())
                    .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_1 : BLUE_LIGHT_2)
                    .horizontalAlignment(CENTER)
                    .build());
        }

        // Rabattzeile
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder()
                        .text("Summe").horizontalAlignment(RIGHT)
                        .colSpan(3)
                        .lineSpacing(1f)
                        .borderWidthTop(1)
                        .textColor(WHITE)
                        .backgroundColor(BLUE_DARK)
                        .fontSize(7)
                        .font(HELVETICA_OBLIQUE)
                        .borderWidth(1)
                        .build())
                .add(TextCell.builder().text(grandTotal + " €").backgroundColor(LIGHT_GRAY)
                        .font(HELVETICA_BOLD_OBLIQUE)
                        .verticalAlignment(TOP)
                        .borderWidth(1)
                        .build())
                .horizontalAlignment(CENTER)
                .build());

        // Summenzeile
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder()
                        .text("Rabatt").horizontalAlignment(RIGHT)
                        .colSpan(3)
                        .lineSpacing(1f)
                        .borderWidthTop(1)
                        .textColor(WHITE)
                        .backgroundColor(BLUE_DARK)
                        .fontSize(7)
                        .font(HELVETICA_OBLIQUE)
                        .borderWidth(1)
                        .build())
                .add(TextCell.builder().text("10%").backgroundColor(LIGHT_GRAY)
                        .font(HELVETICA_BOLD_OBLIQUE)
                        .verticalAlignment(TOP)
                        .borderWidth(1)
                        .build())
                .horizontalAlignment(CENTER)
                .build());

        return tableBuilder.build();
    }

}
