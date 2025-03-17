package indus340.tech.freeresearch4j.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import dev.langchain4j.agent.tool.Tool;
import indus340.tech.freeresearch4j.engine.WebsiteCrawlAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DynamicBrowserAgent {

    private final WebsiteCrawlAssistant websiteCrawlAssistant;

    private static final Logger logger = LoggerFactory.getLogger(DynamicBrowserAgent.class);

    public DynamicBrowserAgent(WebsiteCrawlAssistant websiteCrawlAssistant) {
        this.websiteCrawlAssistant = websiteCrawlAssistant;
    }

    /**
     * Crawls the specified URL and returns its HTML content.
     *
     * @param url the URL of the web page to browse.
     * @return the HTML content of the page as a String.
     */
    @Tool("Fetches the HTML content as text from the provided URL. Provide the original user message as second parameter.")
    public String browseSite(String url, String userMessage) {
        // Create a Playwright instance and launch a browser in headless mode
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
            );
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Step 1: Navigate to the initial site
            String initialUrl = url;
            page.navigate(initialUrl);
            logger.info("Opened page: " + page.title());

            // Step 2: Read the page content (HTML/text)
            String pageContent = page.innerText("body").substring(0, Math.min(60000, page.innerText("body").length()));
            // logger.info("Page content (excerpt): " + pageContent);

            String content = websiteCrawlAssistant.chat("USERMESSAGE: " + userMessage + " ### WEBSITECONTENT: " + pageContent);
            JSONResult jsonResult = convertResult(content);
            StringBuilder result = new StringBuilder(jsonResult.content);
            if (!jsonResult.user_message_answered.equals("true")) {
                for (String innerUrl : jsonResult.links) {
                    page.navigate(innerUrl);
                    logger.info("Opened page: " + page.title());
                    String innerPageContent = page.innerText("body").substring(0, Math.min(60000, page.innerText("body").length()));
                    JSONResult innerResult = convertResult(websiteCrawlAssistant.chat("USERMESSAGE: " + userMessage + " ### WEBSITECONTENT: " + innerPageContent));
                    result.append(innerResult.content);
                    if (innerResult.user_message_answered.equals("true")) {
                        break;
                    }
                }
            }
            // Cleanup
            browser.close();
            String resString = result.toString();
            return resString.isBlank() ? "{'result':'no result'}" : resString;
        } catch (Exception ex) {
            return "{'result':'no result'}";
        }
    }

    private static JSONResult convertResult(String result) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(result, Map.class);

            String content = (String) map.get("content");
            List<String> links = (List<String>) map.get("links");

            System.out.println("Content: " + content);
            System.out.println("Links: " + links);
            return new JSONResult(content, links, map.get("user_message_answered").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    record JSONResult (String content, List<String> links, String user_message_answered) {

    }
}
