// src/main/java/com/example/demo/OpenAIMockController.java
package indus340.tech.freeresearch4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import indus340.tech.freeresearch4j.engine.Assistant;
import indus340.tech.freeresearch4j.tools.OCRTool;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class DeepResearch4jController {

    private final Assistant assistant;
    private final ChatMemoryProvider chatMemoryProvider;
    private final ObjectMapper objectMapper;
    private final OCRTool ocrTool;

    public DeepResearch4jController(Assistant assistant, ChatMemoryProvider chatMemoryProvider, ObjectMapper objectMapper, OCRTool ocrTool) {
        this.assistant = assistant;
        this.chatMemoryProvider = chatMemoryProvider;
        this.objectMapper = objectMapper;
        this.ocrTool = ocrTool;
    }

    @DeleteMapping("/chat/completions")
    public ResponseEntity<String> resetChatMemory(@RequestParam("chatId") String chatId) {
        chatMemoryProvider.get(chatId).clear();
        return ResponseEntity.ok("ok");
    }

    @PostMapping(value = "/chat/completions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatCompletionResponse> createChatCompletion(
            @RequestPart("message") String messagesJson,
            @RequestPart("chatId") String chatId,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Parse the incoming JSON messages
            Message request = objectMapper.readValue(messagesJson, Message.class);
            String requestMessage = request.content();
            if (file != null) {
                ocrTool.setPdfAsBytes(Integer.parseInt(chatId), file.getBytes());
            }
            String answer = assistant.chat(Integer.parseInt(chatId), requestMessage);

            // Create a mock response simulating an OpenAI completion.
            ChatCompletionResponse response = new ChatCompletionResponse(
                    UUID.randomUUID().toString(),
                    "chat.completion",
                    Instant.now().getEpochSecond(),
                    "gpt-4o-mini",
                    List.of(new Choice(new Message(answer, "assistant"), 0))
            );
            System.out.println("FINAL RESPONSE: \n" + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
