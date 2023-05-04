package de.softwareprojekt.bestbowl.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Matija Kopschek
 * @author Marten Voß
 */
public class PDFUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDFUtils.class);

    private PDFUtils() {
    }

    public static void createDemoPdf() {
        //create "test" folder in working dir
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
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.newLineAtOffset(25, 700);
            //contentStream.showText("Rechnungsnr" + );
            contentStream.newLine();
            contentStream.setFont(PDType1Font.HELVETICA, 15);
            contentStream.showText("test");
            contentStream.endText();
            contentStream.close();

            document.save(pdfFile);
            document.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
