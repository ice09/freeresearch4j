package indus340.tech.freeresearch4j.tools;

import dev.langchain4j.agent.tool.Tool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * WebCrawl bean uses JSoup to fetch and return the HTML content of a given URL.
 */
@Service
public class WebsiteCrawlTool {

    /**
     * Crawls the specified URL and returns its HTML content.
     *
     * @param url the URL of the web page to crawl.
     * @return the HTML content of the page as a String.
     */
    @Tool("Fetches the HTML content as text from the provided URL")
    public String crawl(String url) {
        System.out.println(("CRAWL: " + url));
        try {
            // Connect to the URL and fetch the document.
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();
            return doc.text();
        } catch (Exception e) {
            // In a production system, consider logging the error rather than printing the stack trace.
            e.printStackTrace();
            return "no result";
        }
    }
}