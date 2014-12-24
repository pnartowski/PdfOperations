package pl.krakow.pn;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Pdf {

    private String fileName;

    public Pdf(String fileName) {
        this.fileName = fileName;

    }

    public void create(Content content) throws FileNotFoundException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        divideOnChapters(content, document);
        document.close();
    }

    private void divideOnChapters(Content content, Document document) throws DocumentException {
        int chapterNumber = 1;
        for (String chapterTitle : content.getArticles().keySet()) {
            Chapter chapter = new Chapter(chapterTitle, chapterNumber++);
            System.out.println(chapterTitle);
            createSections(chapter, content.getArticles().get(chapterTitle));
            document.add(chapter);
        }
    }

    private void createSections(Chapter chapter, List<News> newses) {
        for (News news : newses) {
            Section section = chapter.addSection(news.getTitle());
            createParagraphs(section, news.getParagraphs());
        }
    }

    private void createParagraphs(Section section, List<String> paragraphs) {
        for (String paragraph : paragraphs) {
            section.add(new Chunk(paragraph));
        }
    }

    public void createSimple() throws DocumentException, FileNotFoundException {

        Document  document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        Chapter chapter2 = new Chapter("Chapter 2 title", 2);

        Chapter chapter1 = new Chapter("Chapter 1 title", 1);
        Section section1 = chapter1.addSection("Section 01");
        Paragraph elements = new Paragraph();
        section1.addSection(elements);

        elements.add("Line one");
        elements.add("Line two");
        elements.add("Line three");

        Section section2 = chapter1.addSection("Section 02");
        section2.add(new Chunk("Some text"));
        section2.add(new Chunk("Another text"));
        section2.add(new Chunk("\n"));
        section2.add(new Chunk("Very important news"));

        document.add(chapter1);
        document.add(chapter2);
        document.add(new Chapter("Chapter 03",3));
        document.newPage();

        document.add(new Chunk("Short instruction how to do some complex task", new Font(Font.FontFamily.HELVETICA, 12, 0, BaseColor.GREEN)));
        document.close();
    }

    public void readPdf() throws IOException {

        PdfReader reader = new PdfReader(fileName);
        System.out.println("Page number : " + reader.getNumberOfPages());
        Map<String, AcroFields.Item> fields = reader.getAcroFields().getFields();

        System.out.println("Fields : " + fields.size());
        for (Map.Entry<String, AcroFields.Item> entry : fields.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

    }

    public void splitByPages() throws IOException, DocumentException {
        PdfReader reader = new PdfReader(fileName);
        for (int page = 1; page <= reader.getNumberOfPages(); page++) {
            String copyFileName = "splitByPage_" + page + ".pdf";
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(copyFileName));
            document.open();
            copy.addPage(copy.getImportedPage(reader, page));
            document.close();
        }
    }

    public void dontKnowWhatWillDo() throws IOException, DocumentException {
        PdfReader reader = new PdfReader(fileName);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("stamper.pdf"));

    }

}
