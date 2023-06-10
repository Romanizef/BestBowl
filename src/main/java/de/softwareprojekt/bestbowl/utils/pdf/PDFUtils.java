package de.softwareprojekt.bestbowl.utils.pdf;

import static de.softwareprojekt.bestbowl.utils.Utils.formatDouble;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.WHITE;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD_OBLIQUE;
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
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingAlley.BowlingAlleyBooking;
import de.softwareprojekt.bestbowl.jpa.entities.bowlingShoe.BowlingShoeBooking;
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
        private static final int POSITIONS_PER_PAGE = 25;

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
         *      double)
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
                        double discount = booking.getDiscount();
                        if (discount > 0)
                                totalPositions++;
                        int pages = (totalPositions / POSITIONS_PER_PAGE) + 1;
                        List<InvoiceLine> invoiceLineList = createInvoiceLineList(drinkBookingList,
                                        foodBookingList,
                                        shoeBookingList, booking);
                        for (int i = 1; i <= pages; i++) {
                                int endIndex = i * POSITIONS_PER_PAGE;
                                if (endIndex > totalPositions)
                                        endIndex = totalPositions;
                                List<InvoiceLine> subInvoiceLineList = invoiceLineList
                                                .subList((i - 1) * POSITIONS_PER_PAGE, endIndex);
                                PDPage pdPage = createPage(booking, bowlingCenter, document, i,
                                                subInvoiceLineList, pages, invoiceLineList);
                                document.addPage(pdPage);

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
                        BowlingAlleyBooking booking) {
                List<InvoiceLine> invoiceLineList = new ArrayList<>(POSITIONS_PER_PAGE);

                int posNrCounter = 1;
                // Bahn
                InvoiceLine bookingInvoiceLine = new InvoiceLine(posNrCounter + " ",
                                " Bahn: " + booking.getBowlingAlley().getId(), 1, booking.getPrice());
                invoiceLineList.add(bookingInvoiceLine);
                posNrCounter++;

                // Rabatt
                double disc = booking.getDiscount();
                if (disc != 0) {
                        InvoiceLine discountInvoiceLine = new InvoiceLine(" ",
                                        " Rabatt: " + formatDouble(disc) + " % ", 1,
                                        -1 * (booking.getPrice() - booking.getPriceWithDiscount()));
                        invoiceLineList.add(discountInvoiceLine);
                }

                for (DrinkBooking drinkBooking : drinkBookingList) {
                        InvoiceLine invoiceLine = new InvoiceLine(posNrCounter + " ",
                                        " " + drinkBooking.getName(),
                                        drinkBooking.getAmount(), drinkBooking.getPrice());
                        invoiceLineList.add(invoiceLine);
                        posNrCounter++;
                }
                for (FoodBooking foodBooking : foodBookingList) {
                        InvoiceLine invoiceLine = new InvoiceLine(posNrCounter + " ",
                                        " " + foodBooking.getName(),
                                        foodBooking.getAmount(), foodBooking.getPrice());
                        invoiceLineList.add(invoiceLine);
                        posNrCounter++;
                }
                for (BowlingShoeBooking shoeBooking : shoeBookingList) {
                        InvoiceLine invoiceLine = new InvoiceLine(posNrCounter + " ",
                                        " Bowlingschuhe, Größe: " + shoeBooking.getBowlingShoe().getSize(),
                                        1, shoeBooking.getPrice());
                        invoiceLineList.add(invoiceLine);
                        posNrCounter++;
                }
                return invoiceLineList;
        }

        /**
         * Creates a {@code PDPage} for the PDF-document.
         *
         * @param booking
         * @param bowlingCenter
         * @param document
         * @param pageNumber
         * @param subInvoiceLineList
         * @param invoiceLineList
         * @param b
         * @return {@code PDPage}
         * @see #createTable(List, double)
         */
        private static PDPage createPage(BowlingAlleyBooking booking, BowlingCenter bowlingCenter, PDDocument document,
                        int pageNumber, List<InvoiceLine> subInvoiceLineList, int pages,
                        List<InvoiceLine> invoiceLineList) {
                PDPage page = new PDPage();
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        // int pageWidth = (int) page.getTrimBox().getWidth(); // get width of the page
                        int pageHeight = (int) page.getTrimBox().getHeight(); // get height of the page
                        contentStream.setStrokingColor(Color.BLUE);
                        contentStream.setLineWidth(1);

                        // Header
                        // Überschrift
                        contentStream.beginText();
                        contentStream.newLineAtOffset(TEXT_OFFSET, pageHeight - 50);
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                        contentStream.showText(bowlingCenter.getDisplayName() + " Rechnung");
                        contentStream.endText();

                        // Kundendaten
                        contentStream.beginText();
                        contentStream.setLeading(14.5f);
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
                        contentStream.newLineAtOffset(TEXT_OFFSET + 20, pageHeight - 75);
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
                        contentStream.newLineAtOffset(TEXT_OFFSET + 350, pageHeight - 90);
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
                                        .startX(95f)
                                        .startY(page.getMediaBox().getUpperRightY() - 165f)
                                        .table(createTable(subInvoiceLineList, invoiceLineList, pageNumber == pages))
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
                        contentStream.newLineAtOffset(TEXT_OFFSET + 480, pageHeight - 750);
                        contentStream.showText("Seite: " + pageNumber + " von " + pages);
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
         * @param subInvoiceLineList
         * @param invoiceLineList
         * @param lastPage
         * @param pageNumber
         * @return {@code Table}
         * @see #createTableHeaderRow(TableBuilder)
         * @see #createTableDiscountRow(double, TableBuilder)
         * @see #createTableSumRow(double, TableBuilder, double)
         */
        private static Table createTable(List<InvoiceLine> subInvoiceLineList, List<InvoiceLine> invoiceLineList,
                        boolean lastPage) {
                // TableBuilder
                final TableBuilder tableBuilder = Table.builder()
                                .addColumnsOfWidth(40, 240, 55, 80)
                                .fontSize(10)
                                .font(HELVETICA)
                                .borderColor(Color.WHITE);

                createTableHeaderRow(tableBuilder);

                // data rows
                for (int i = 0; i < subInvoiceLineList.size(); i++) {
                        InvoiceLine invoiceLine = subInvoiceLineList.get(i);

                        if (invoiceLine.description.contains("Rabatt")) {
                                tableBuilder.addRow(Row.builder()
                                                .add(TextCell.builder()
                                                                .text(invoiceLine.description)
                                                                .horizontalAlignment(RIGHT)
                                                                .colSpan(2)
                                                                .lineSpacing(1f)
                                                                .fontSize(8)
                                                                .borderWidthTop(1)
                                                                .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_1
                                                                                : BLUE_LIGHT_2)
                                                                .font(HELVETICA_BOLD_OBLIQUE)
                                                                .borderWidth(1)
                                                                .build())
                                                .add(TextCell.builder().text(String.valueOf(invoiceLine.amount))
                                                                .borderWidth(1)
                                                                .horizontalAlignment(RIGHT).build())
                                                .add(TextCell.builder()
                                                                .text(formatDouble((invoiceLine.price
                                                                                * invoiceLine.amount)) + " €")
                                                                .borderWidth(1).horizontalAlignment(RIGHT).build())
                                                .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_1 : BLUE_LIGHT_2)
                                                .build());
                        } else {
                                tableBuilder.addRow(Row.builder()
                                                .add(TextCell.builder().text(invoiceLine.posnr).borderWidth(1)
                                                                .horizontalAlignment(RIGHT).build())
                                                .add(TextCell.builder().text(invoiceLine.description).borderWidth(1)
                                                                .horizontalAlignment(LEFT).build())
                                                .add(TextCell.builder().text(String.valueOf(invoiceLine.amount))
                                                                .borderWidth(1)
                                                                .horizontalAlignment(RIGHT).build())
                                                .add(TextCell.builder()
                                                                .text(formatDouble((invoiceLine.price
                                                                                * invoiceLine.amount)) + " €")
                                                                .borderWidth(1).horizontalAlignment(RIGHT).build())
                                                .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_1 : BLUE_LIGHT_2)
                                                .build());
                        }
                }
                if (lastPage == false)
                        createTableTotalRow(tableBuilder, subInvoiceLineList, "Zwischensumme");
                if (lastPage)
                        createTableTotalRow(tableBuilder, invoiceLineList, "Gesamtsumme");
                return tableBuilder.build();
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
         * @param tableBuilder
         * @param invoiceLineList
         * @param sumDescriptor
         */
        private static void createTableTotalRow(final TableBuilder tableBuilder,
                        List<InvoiceLine> invoiceLineList, String sumDescriptor) {
                double total = 0;
                for (InvoiceLine invoiceLine : invoiceLineList) {
                        total += invoiceLine.price * invoiceLine.amount;
                }

                // Summenzeile
                tableBuilder.addRow(Row.builder()
                                .add(TextCell.builder()
                                                .text(sumDescriptor)
                                                .horizontalAlignment(RIGHT)
                                                .colSpan(3)
                                                .lineSpacing(1f)
                                                .borderWidthTop(1)
                                                .textColor(WHITE)
                                                .backgroundColor(BLUE_DARK)
                                                .fontSize(7)
                                                .font(HELVETICA_BOLD_OBLIQUE)
                                                .borderWidth(1)
                                                .build())
                                .add(TextCell.builder()
                                                .text(formatDouble(total) + " €")
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
         * 
         * @param description
         * 
         * @param amount
         * 
         * @param price
         */
        private record InvoiceLine(String posnr, String description, int amount, double price) {
        }
}