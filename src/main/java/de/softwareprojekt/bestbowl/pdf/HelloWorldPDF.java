package de.softwareprojekt.bestbowl.pdf;

import com.aspose.pdf.Document;
import com.aspose.pdf.Page;
import com.aspose.pdf.TextFragment;

public class HelloWorldPDF {

	public static void main(String[] args) {
		// Vollständige Beispiele und Datendateien finden Sie unter https://github.com/aspose-pdf/Aspose.PDF-for-Java
		// Dokumentenobjekt initialisieren
		Document document = new Document();
		 
		//Seite hinzufügen
		Page page = document.getPages().add();
		 
		// Text zu neuer Seite hinzufügen
		page.getParagraphs().add(new TextFragment("Hello World!"));
		 
		// Aktualisiertes PDF speichern
		document.save("C:\\Users\\matij\\Downloads\\HelloWorld_out.pdf");
	}

}
