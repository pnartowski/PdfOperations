package pl.krakow.pn;

import com.itextpdf.text.DocumentException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public class PdfTest {

    private Pdf underTest;
    private final String CNN_PDF_FILE = "cnn.pdf";

    @Test
    public void createSimple() throws FileNotFoundException, DocumentException {

        underTest = new Pdf("first.pdf");
        underTest.createSimple();
    }

    @Test
    public void splitByPages() throws IOException, DocumentException {
        underTest = new Pdf("first.pdf");
        underTest.splitByPages();
    }

    @Test
    public void createCnnPdfAndSplitByPages() throws IOException, ParseException, DocumentException {

        underTest = new Pdf(CNN_PDF_FILE);
        Content content = new Content();
        content.prepare();
        underTest.create(content);

        underTest.splitByPages();
    }


}