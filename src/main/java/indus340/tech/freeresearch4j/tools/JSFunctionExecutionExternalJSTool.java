package indus340.tech.freeresearch4j.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class JSFunctionExecutionExternalJSTool {

    // URL of the Node.js execution server
    private static final String NODE_SERVER_URL = "http://localhost:3000/execute";

    private final RestTemplate restTemplate;

    // Inject RestTemplate (make sure to define it as a Bean in your configuration)
    public JSFunctionExecutionExternalJSTool(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Tool("Executes arbitrary JavaScript code without dependencies. Can use a Map<Object, Object> kvStore to store data for other steps and read data from other steps. You always have to provide the chatId you received as the first method argument, the JS code is the second argument.")
    public String executeJs(String chatId, String jsCode) {
        // Construct the payload as a Map
        Map<String, String> payload = new HashMap<>();
        payload.put("code", jsCode);
        payload.put("chatId", chatId);

        // Prepare HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request entity with headers and payload
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            // Send the POST request to the Node.js server
            ResponseEntity<String> response = restTemplate.postForEntity(NODE_SERVER_URL, requestEntity, String.class);

            // Check if the response status is 2xx successful
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to execute JS, received status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing JS", e);
        }
    }
}
