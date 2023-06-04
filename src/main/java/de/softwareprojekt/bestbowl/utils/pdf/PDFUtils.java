package de.softwareprojekt.bestbowl.utils.pdf;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.WHITE;
import static java.lang.Double.parseDouble;
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
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoeBooking;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.drink.DrinkBooking;
import de.softwareprojekt.bestbowl.jpa.entities.food.FoodBooking;
import de.softwareprojekt.bestbowl.utils.Utils;

/**
 * Class for creating PDFs.
 *
 * @author Matija Kopschek
 */
public class PDFUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDFUtils.class);
    private static final int TEXT_OFFSET = 50;
    private static final int POSITIONS_PER_PAGE = 20;

    private static final Color BLUE_DARK = new Color(76, 129, 190);
    private static final Color BLUE_LIGHT_1 = new Color(186, 206, 230);
    private static final Color BLUE_LIGHT_2 = new Color(218, 230, 242);

    /**
     * Creates a PDF document with the given {@code BowlingAlleyBooking} object.
     *
     * @param booking
     * @return {@code byte[]}
     * @see #createInvoiceLineList(List, List, List, BowlingAlleyBooking, int)
     * @see #createPage(BowlingAlleyBooking, BowlingCenter, PDDocument, int, List,
     * double)
     */
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
            int totalPositions = drinkBookingList.size() + foodBookingList.size() + shoeBookingList.size()
                    + 1;
            int pages = (totalPositions / POSITIONS_PER_PAGE) + 1;
            int positionIndex = 0;
            double discount = booking.getDiscount();
            for (int i = 1; i <= pages; i++) {
                if (positionIndex < totalPositions) {
                    List<InvoiceLine> invoiceLineList = createInvoiceLineList(drinkBookingList,
                            foodBookingList,
                            shoeBookingList, booking, positionIndex);
                    PDPage pdPage = createPage(booking, bowlingCenter, document, i,
                            invoiceLineList, discount);
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

    /**
     * Creates a {@code List<InvoiceLine>} out of all the bookings in the given
     * bookinglists.
     *
     * @param drinkBookingList
     * @param foodBookingList
     * @param shoeBookingList
     * @param booking
     * @param positionIndex
     * @return {@code List<InvoiceLine>}
     */
    private static List<InvoiceLine> createInvoiceLineList(List<DrinkBooking> drinkBookingList,
                                                           List<FoodBooking> foodBookingList, List<BowlingShoeBooking> shoeBookingList,
                                                           BowlingAlleyBooking booking, int positionIndex) {
        List<InvoiceLine> invoiceLineList = new ArrayList<>(POSITIONS_PER_PAGE);

        int posNrCounter = 1;
        InvoiceLine bookingInvoiceLine = new InvoiceLine(posNrCounter + " ",
                " Bahn: " + booking.getBowlingAlley().getId(), "1", booking.getPriceWithDiscount() + "");
        invoiceLineList.add(bookingInvoiceLine);
        posNrCounter++;

        int i = 0;
        do {
            if (positionIndex + i < drinkBookingList.size()) {
                DrinkBooking drinkBooking = drinkBookingList.get(positionIndex + i);
                InvoiceLine invoiceLine = new InvoiceLine(positionIndex + posNrCounter + " ",
                        " " + drinkBooking.getName(),
                        "" + drinkBooking.getAmount(), "" + drinkBooking.getPrice());
                invoiceLineList.add(invoiceLine);
                posNrCounter++;
            }
            i++;
        } while (i < POSITIONS_PER_PAGE);

        int j = 0;
        do {
            if (positionIndex + j < foodBookingList.size()) {
                FoodBooking foodBooking = foodBookingList.get(positionIndex + j);
                InvoiceLine invoiceLine = new InvoiceLine(positionIndex + posNrCounter + " ",
                        " " + foodBooking.getName(),
                        "" + foodBooking.getAmount(), "" + foodBooking.getPrice());
                invoiceLineList.add(invoiceLine);
                posNrCounter++;
            }
            j++;
        } while (j < POSITIONS_PER_PAGE);

        int k = 0;
        do {
            if (positionIndex + k < shoeBookingList.size()) {
                BowlingShoeBooking shoeBooking = shoeBookingList.get(positionIndex + k);
                InvoiceLine invoiceLine = new InvoiceLine(positionIndex + posNrCounter + " ",
                        " Bowlingschuhe, Größe: " + shoeBooking.getBowlingShoe().getSize(),
                        "1", "" + shoeBooking.getPrice());
                invoiceLineList.add(invoiceLine);
                posNrCounter++;
            }
            k++;
        } while (k < POSITIONS_PER_PAGE);
        return invoiceLineList;
    }

    /**
     * Creates a {@code PDPage} for the PDF-document.
     *
     * @param booking
     * @param bowlingCenter
     * @param document
     * @param pageNumber
     * @param invoiceLineList
     * @param discount
     * @return {@code PDPage}
     * @see #createTable(List, double)
     */
    private static PDPage createPage(BowlingAlleyBooking booking, BowlingCenter bowlingCenter, PDDocument document,
                                     int pageNumber, List<InvoiceLine> invoiceLineList, double discount) {
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
            contentStream.showText(bowlingCenter.getDisplayName() + "Rechnung");
            contentStream.endText();

            // Kundendaten
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
            contentStream.newLineAtOffset(TEXT_OFFSET, pageHeight - 65);
            contentStream.showText("Rechnungsempfänger");
            contentStream.newLine();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
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
            contentStream.newLineAtOffset(TEXT_OFFSET + 350, pageHeight - 80);
            contentStream.showText("Rechnungsnummer: " + booking.getId());
            contentStream.newLine();
            contentStream.showText("Datum: " + Utils.toDateOnlyString(booking.getStartTime()));
            contentStream.newLine();
            contentStream.showText("Kundennummer: " + booking.getClient().getId());
            contentStream.endText();
            // Ende Header

            // Main
            // Tabelle
            TableDrawer tableDrawer = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(50f)
                    .startY(page.getMediaBox().getUpperRightY() - 140f)
                    .table(createTable(invoiceLineList, discount))
                    .build();
            tableDrawer.draw();
            // Ende Main

            // Footer
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 7);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(TEXT_OFFSET - 30, pageHeight - 700);
            contentStream.showText(bowlingCenter.getBusinessName());
            contentStream.newLine();
            contentStream.showText(bowlingCenter.getStreet());
            contentStream.newLine();
            contentStream.showText(bowlingCenter.getPostCodeString() + " " + bowlingCenter.getCity());
            contentStream.newLine();
            contentStream.showText("Deutschland");
            contentStream.newLine();
            if (bowlingCenter.getSenderEmail() != null) {
                contentStream.showText(bowlingCenter.getSenderEmail());
            }
            contentStream.endText();
            // Ende Footer

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

    /**
     * Creates a {@code Table} for the PDF-document with the given
     * {@code List<InvoiceLine>}.
     *
     * @param invoiceLineList
     * @param discount
     * @return {@code Table}
     * @see #createTableHeaderRow(TableBuilder)
     * @see #createTableDiscountRow(double, TableBuilder)
     * @see #createTableSumRow(double, TableBuilder, double)
     */
    private static Table createTable(List<InvoiceLine> invoiceLineList, double discount) {
        // TableBuilder
        final TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(100, 200, 100, 100)
                .fontSize(10)
                .font(HELVETICA)
                .borderColor(Color.WHITE);

        createTableHeaderRow(tableBuilder);

        // data rows
        double grandTotal = 0;
        for (int i = 0; i < invoiceLineList.size(); i++) {
            InvoiceLine invoiceLine = invoiceLineList.get(i);

            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(invoiceLine.posnr).borderWidth(1)
                            .horizontalAlignment(RIGHT).build())
                    .add(TextCell.builder().text(invoiceLine.description).borderWidth(1)
                            .horizontalAlignment(LEFT).build())
                    .add(TextCell.builder().text(invoiceLine.amount).borderWidth(1)
                            .horizontalAlignment(RIGHT).build())
                    .add(TextCell.builder()
                            .text(formatDouble((parseDouble(invoiceLine.price)
                                    * parseDouble(invoiceLine.amount))) + " €")
                            .borderWidth(1).horizontalAlignment(RIGHT).build())
                    .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_1 : BLUE_LIGHT_2)
                    .build());
            grandTotal += parseDouble(invoiceLine.amount) * parseDouble(invoiceLine.price);
        }

        createTableDiscountRow(discount, tableBuilder);
        createTableSumRow(discount, tableBuilder, grandTotal);

        return tableBuilder.build();
    }

    /**
     * Creates a {@code Row} with the discount of the {@code Table}.
     *
     * @param discount
     * @param tableBuilder
     */
    private static void createTableDiscountRow(double discount, final TableBuilder tableBuilder) {
        // Rabattzeile
        if (discount != 0) {
            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder()
                            .text("Rabatt")
                            .horizontalAlignment(RIGHT)
                            .colSpan(3)
                            .lineSpacing(1f)
                            .borderWidthTop(1)
                            .textColor(WHITE)
                            .backgroundColor(BLUE_DARK)
                            .fontSize(7)
                            .font(HELVETICA_OBLIQUE)
                            .borderWidth(1)
                            .build())
                    .add(TextCell.builder().text(formatDouble(discount) + " %")
                            .backgroundColor(LIGHT_GRAY)
                            .font(HELVETICA_BOLD_OBLIQUE)
                            .verticalAlignment(TOP)
                            .horizontalAlignment(RIGHT)
                            .borderWidth(1)
                            .build())
                    .horizontalAlignment(CENTER)
                    .build());
        } else {
            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder()
                            .text("Rabatt")
                            .horizontalAlignment(RIGHT)
                            .colSpan(3)
                            .lineSpacing(1f)
                            .borderWidthTop(1)
                            .textColor(WHITE)
                            .backgroundColor(BLUE_DARK)
                            .fontSize(7)
                            .font(HELVETICA_OBLIQUE)
                            .borderWidth(1)
                            .build())
                    .add(TextCell.builder().text("-").backgroundColor(LIGHT_GRAY)
                            .font(HELVETICA_BOLD_OBLIQUE)
                            .verticalAlignment(TOP)
                            .borderWidth(1)
                            .horizontalAlignment(RIGHT)
                            .build())
                    .horizontalAlignment(CENTER)
                    .build());
        }
    }

    /**
     * Creates a {@code Row} with the header of the {@code Table}.
     *
     * @param tableBuilder
     */
    private static void createTableHeaderRow(final TableBuilder tableBuilder) {
        // header row
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("Pos.").borderWidth(1).build())
                .add(TextCell.builder().text("Beschreibung").borderWidth(1).build())
                .add(TextCell.builder().text("Menge").borderWidth(1).build())
                .add(TextCell.builder().text("Preis").borderWidth(1).build())
                .backgroundColor(BLUE_DARK)
                .textColor(Color.WHITE)
                .font(HELVETICA_BOLD)
                .fontSize(12)
                .horizontalAlignment(LEFT)
                .build());
    }

    /**
     * Creates a {@code Row} with the total sum of the {@code Table}.
     *
     * @param discount
     * @param tableBuilder
     * @param grandTotal
     */
    private static void createTableSumRow(double discount, final TableBuilder tableBuilder, double grandTotal) {
        // Summenzeile
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder()
                        .text("Summe")
                        .horizontalAlignment(RIGHT)
                        .colSpan(3)
                        .lineSpacing(1f)
                        .borderWidthTop(1)
                        .textColor(WHITE)
                        .backgroundColor(BLUE_DARK)
                        .fontSize(7)
                        .font(HELVETICA_OBLIQUE)
                        .borderWidth(1)
                        .build())
                .add(TextCell.builder()
                        .text(formatDouble(grandTotal - (grandTotal * (discount / 100))) + " €")
                        .backgroundColor(LIGHT_GRAY)
                        .font(HELVETICA_BOLD_OBLIQUE)
                        .verticalAlignment(TOP)
                        .borderWidth(1)
                        .horizontalAlignment(RIGHT)
                        .build())
                .horizontalAlignment(CENTER)
                .build());
    }

    /*
     * Record class for the InvoiceLines
     *
     * @param posnr
     * @param description
     * @param amount
     * @param price
     */
    private record InvoiceLine(String posnr, String description, String amount, String price) {
    }
}