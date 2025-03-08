package indus340.tech.freeresearch4j.tools;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
public class GoogleSearchTool {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchTool.class);

    @Value("${google.search.apikey}")
    private String apiKey;

    @Value("${google.search.cxkey}")
    private String cxKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Searches Google using the official Google Custom Search JSON API.
     *
     * @param query the search query string.
     * @return a list of URLs from the search results.
     */
    @Tool("Performs a Google search using the official Google Custom Search JSON API and returns a list of result URLs.")
    public String searchGoogle(String query) {
        logger.info("GOOGLE SEARCH: {}", query);
        try {
            // Build the URI with required parameters.
            URI uri = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/customsearch/v1")
                    .queryParam("key", apiKey)
                    .queryParam("cx", cxKey)
                    .queryParam("q", query)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            // Execute the GET request.
            String response = restTemplate.getForObject(uri, String.class);

            return response;
        } catch (Exception e) {
            // In production, consider using a logging framework instead of printing the stack trace.
            logger.error(e.getMessage(), e);
            return "no result";
        }
    }
}