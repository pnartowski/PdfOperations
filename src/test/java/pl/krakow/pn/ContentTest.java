package pl.krakow.pn;

import com.itextpdf.text.DocumentException;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class ContentTest {

    private Content underTest = new Content();

    @Test
    public void tdd() throws IOException, ParseException, DocumentException {

        underTest.prepare();
        Pdf pdf = new Pdf("cnn.pdf");
        pdf.create(underTest);

    }
}