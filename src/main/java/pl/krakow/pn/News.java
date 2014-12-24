package pl.krakow.pn;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class News implements Comparable<News> {

    public static final String PATH_MATCHER = "^/(\\d{4})/(\\d{2})/(\\d{2})/(\\w+)/.*";
    public static final Pattern PATH_PATTERN = Pattern.compile(PATH_MATCHER);
    public static final String META_CONTENT = "content";
    public static final String META_SUBJECT_SELECTOR = "meta[property=og:title]";
    public static final String META_DATE_MODIFIED_SELECT = "meta[itemprop=dateModified]";
    public static final String META_ARTICLE_SECTION_SELECT = "meta[itemprop=articleSection]";
    private static final long FIVE_DAYS = 2 * 24 * 60 * 60 * 1000;
    private String gender;
    private String url;
    private Date date;
    private String title;
    private Document source;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private Date lastModification;
    private String articleSection;
    private final List<String> paragraphs = new LinkedList<String>();
    private final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");

    public News(String baseUrl, String path) {
        this.url = baseUrl + path;

        Matcher matcher = PATH_PATTERN.matcher(path);
        if (matcher.matches()) {
            date = extractDate(matcher);
            gender = matcher.group(3);
        }
    }

    private static Date extractDate(Matcher matcher) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(matcher.group(1)));
        calendar.set(Calendar.MONTH, Integer.parseInt(matcher.group(2)));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(3)));
        return calendar.getTime();
    }

    public static boolean isInteresting(String path) {
        Matcher matcher = PATH_PATTERN.matcher(path);

        if (matcher.matches()) {
            Date dt = extractDate(matcher);
            return dt.getTime() + FIVE_DAYS < new Date().getTime();
        }

        return false;
    }

    public String getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void fetch() throws IOException {
        System.out.println("Fetching " + url);
        File file = prepareNewsFile();
        if (file.exists()) {
            source = Jsoup.parse(file, "UTF-8");
        } else {
            source = Jsoup.connect(url).get();
            new FileOutputStream(file).write(source.outerHtml().getBytes());
        }
    }

    private File prepareNewsFile() {
        return new File(fileFormat.format(date) + gender + url.hashCode() + ".html");
    }

    public String getSource() {
        return source.outerHtml();
    }

    public void parse() throws ParseException {
        parseMetaData();
        parseParagraphs();
    }

    private void parseParagraphs() {
        Elements elements = source.select("p");
        for (int i = 0; i < elements.size(); i++) {
            paragraphs.add(elements.get(i).text());
        }
    }

    private void parseMetaData() throws ParseException {
        parseTitleFromMetaData();
        parseLastModificationDateFromMetaData();
        parseArticleSectionFromMetaData();
    }

    private void parseArticleSectionFromMetaData() {
        articleSection = source.select(META_ARTICLE_SECTION_SELECT).attr(META_CONTENT);
    }

    private void parseLastModificationDateFromMetaData() throws ParseException {
        lastModification = dateFormat.parse(source.select(META_DATE_MODIFIED_SELECT).attr(META_CONTENT));
    }

    private void parseTitleFromMetaData() {
        title = source.select(META_SUBJECT_SELECTOR).attr(META_CONTENT);
    }

    public void setSource(Document source) {
        this.source = source;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public String getGender() {
        return gender;
    }

    public String getArticleSection() {
        return articleSection;
    }

    public Date getLastModification() {
        return lastModification;
    }

    @Override
    public int compareTo(News other) {

        Date d1 = lastModification;
        Date d2 = other.lastModification;
        if (d1 == null || d2 == null) {
            d1 = date;
            d2 = other.date;
        }

        if (d1.equals(d2)) {
            return 0;
        }
        if (d1.before(d2)) {
            return -1;
        }
        return 1;
    }
}
