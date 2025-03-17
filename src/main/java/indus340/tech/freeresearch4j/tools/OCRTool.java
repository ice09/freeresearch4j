package indus340.tech.freeresearch4j.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OCRTool {

    private final RestTemplate restTemplate;
    private final String mistralApiKey;
    Map<Integer, byte[]> pdfAsByteForChat;

    public OCRTool(RestTemplate restTemplate, @Value("${mistralai.chat.apikey}") String mistralApiKey) {
        this.restTemplate = restTemplate;
        this.mistralApiKey = mistralApiKey;
        pdfAsByteForChat = new HashMap<>();
    }

    @Tool("Extracts text from a PDF. Call if text extraction is requested. Use the result in your answer. You always have to provide the chatId you received as the method argument.")
    public String extractTextFromPDF(String chatId) {
        // --- Step 1: Upload file ---
        String uploadUrl = "https://api.mistral.ai/v1/files";
        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.set("Authorization", "Bearer " + mistralApiKey);
        uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> uploadBody = new LinkedMultiValueMap<>();
        uploadBody.add("purpose", "ocr");

        // Wrap the file in a ByteArrayResource to be sent with RestTemplate
        ByteArrayResource fileAsResource = new ByteArrayResource(pdfAsByteForChat.get(Integer.parseInt(chatId))) {
            @Override
            public String getFilename() {
                return "file.pdf";
            }
        };
        uploadBody.add("file", fileAsResource);

        HttpEntity<MultiValueMap<String, Object>> uploadRequest = new HttpEntity<>(uploadBody, uploadHeaders);
        ResponseEntity<Map> uploadResponse = restTemplate.postForEntity(uploadUrl, uploadRequest, Map.class);
        // Extract file ID from the upload response
        Map<String, Object> uploadResponseBody = uploadResponse.getBody();
        String fileId = uploadResponseBody.get("id").toString();

        // --- Step 2: Retrieve file details (optional) ---
        String retrieveUrl = "https://api.mistral.ai/v1/files/" + fileId;
        HttpHeaders retrieveHeaders = new HttpHeaders();
        retrieveHeaders.set("Authorization", "Bearer " + mistralApiKey);
        retrieveHeaders.set("Accept", "application/json");
        HttpEntity<Void> retrieveRequest = new HttpEntity<>(retrieveHeaders);
        ResponseEntity<Map> retrieveResponse = restTemplate.exchange(retrieveUrl, HttpMethod.GET, retrieveRequest, Map.class);
        // (Optional) Log or inspect retrieveResponse.getBody() if needed

        // --- Step 3: Get signed URL ---
        String signedUrlEndpoint = "https://api.mistral.ai/v1/files/" + fileId + "/url?expiry=24";
        HttpHeaders signedUrlHeaders = new HttpHeaders();
        signedUrlHeaders.set("Authorization", "Bearer " + mistralApiKey);
        signedUrlHeaders.set("Accept", "application/json");
        HttpEntity<Void> signedUrlRequest = new HttpEntity<>(signedUrlHeaders);
        ResponseEntity<Map> signedUrlResponse = restTemplate.exchange(signedUrlEndpoint, HttpMethod.GET, signedUrlRequest, Map.class);
        // Assume the signed URL is returned in a field called "url"
        Map<String, Object> signedUrlBody = signedUrlResponse.getBody();
        String signedUrl = signedUrlBody.get("url").toString();

        // --- Step 4: Request OCR results using the signed URL ---
        String ocrUrl = "https://api.mistral.ai/v1/ocr";
        HttpHeaders ocrHeaders = new HttpHeaders();
        ocrHeaders.setContentType(MediaType.APPLICATION_JSON);
        ocrHeaders.set("Authorization", "Bearer " + mistralApiKey);

        Map<String, Object> ocrPayload = new HashMap<>();
        ocrPayload.put("model", "mistral-ocr-latest");
        Map<String, String> documentMap = new HashMap<>();
        documentMap.put("type", "document_url");
        documentMap.put("document_url", signedUrl);
        ocrPayload.put("document", documentMap);
        ocrPayload.put("include_image_base64", true);

        HttpEntity<Map<String, Object>> ocrRequest = new HttpEntity<>(ocrPayload, ocrHeaders);
        ResponseEntity<String> ocrResponse = restTemplate.postForEntity(ocrUrl, ocrRequest, String.class);
        String responseText = extractMarkdown(ocrResponse.getBody());
        return responseText.substring(0, Math.min(responseText.length(), 1000000));
    }

    public String extractMarkdown(String responseText) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseText);
            StringBuilder markdownContent = new StringBuilder();

            for (JsonNode page : rootNode.path("pages")) {
                if (page.has("markdown")) {
                    markdownContent.append(page.get("markdown").asText());
                }
            }

            return markdownContent.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setPdfAsBytes(Integer chatId, byte[] pdfAsBytes) {
        pdfAsByteForChat.put(chatId, pdfAsBytes);
    }
}
