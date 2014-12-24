package pl.krakow.pn;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Content {

    private static final String CNN_MAIN_PAGE = "http://www.cnn.com/";
    private static final String CNN_MAIN_PAGE_FILE = "cnn_main_page.html";
    private static final int TEN_MINUTES = 10 * 60 * 1000;
    public static final String UTF_8 = "UTF-8";
    public static final String HREF_TAG = "href";
    public static final String A_TAG = "a";
    public static final int ONE_MINUTE_TIMEOUT = 60000;
    private final Map<String, List<News>> articles = new TreeMap<String, List<News>>();

    public Map<String, List<News>> getArticles() {
        return articles;
    }

    public void prepare() throws IOException, ParseException {

        File mainPage = new File(CNN_MAIN_PAGE_FILE);
        Document document;
        if (!mainPage.exists() || fileOlderThen10Min(mainPage)) {
            document = fetchDocumentFromUrl(CNN_MAIN_PAGE);
            saveSourceToFile(document.outerHtml(), mainPage);
        } else {
            document = loadDocumentFromFile(mainPage);
        }

        List<News> newsPath = extractNews(CNN_MAIN_PAGE, document.select("ul.cnn_bulletbin"));

        prepareNews(newsPath);
        addNews(newsPath);
    }

    private void prepareNews(List<News> news) throws IOException, ParseException {
        for (News one : news) {
            one.fetch();
            one.parse();
        }
        Collections.sort(news);
    }

    private void addNews(List<News> newses) {
        for (News news : newses) {

            List<News> list = articles.get(news.getArticleSection());
            if (list == null) {
                list = new LinkedList<News>();
                articles.put(news.getArticleSection(), list);
            }
            list.add(news);
        }
    }

    private List<News> extractNews(String baseUrl, Elements elements) {
        List<News> newsPath = new LinkedList<News>();
        Iterator<Element> iterator = elements.iterator();
        for (; iterator.hasNext(); ) {
            Elements href = iterator.next().select(A_TAG);
            for (int i = 0; i < href.size(); i++) {
                String path = href.get(i).attr(HREF_TAG);
                if (News.isInteresting(path)) {
                    newsPath.add(new News(baseUrl, path));
                }
            }
        }
        return newsPath;
    }

    private Document loadDocumentFromFile(File file) throws IOException {
        return Jsoup.parse(file, UTF_8);
    }

    private void saveSourceToFile(String source, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(source.getBytes());
        out.close();
    }

    private Document fetchDocumentFromUrl(String url) throws IOException {
        return Jsoup.parse(new URL(url), ONE_MINUTE_TIMEOUT);
    }

    private boolean fileOlderThen10Min(File mainPage) {
        return new Date(mainPage.lastModified() + TEN_MINUTES).before(new Date());
    }
}
