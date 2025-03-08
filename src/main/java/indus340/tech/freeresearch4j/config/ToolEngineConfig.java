package indus340.tech.freeresearch4j.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import indus340.tech.freeresearch4j.engine.Assistant;
import indus340.tech.freeresearch4j.tools.GoogleSearchTool;
import indus340.tech.freeresearch4j.tools.JSFunctionExecutionTool;
import indus340.tech.freeresearch4j.tools.OCRTool;
import indus340.tech.freeresearch4j.tools.WebsiteCrawlTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.*;
import static java.time.Duration.ofSeconds;

@Configuration
public class ToolEngineConfig {

    @Value("${openai.apikey}")
    private String openrouterApiKey;

    private final GoogleSearchTool googleSearchTool;
    private final JSFunctionExecutionTool jsFunctionExecutionTool;
    private final WebsiteCrawlTool websiteCrawlTool;
    private final OCRTool ocrTool;

    public ToolEngineConfig(GoogleSearchTool googleSearchTool, JSFunctionExecutionTool jsFunctionExecutionTool, WebsiteCrawlTool websiteCrawlTool, OCRTool ocrTool) {
        this.googleSearchTool = googleSearchTool;
        this.jsFunctionExecutionTool = jsFunctionExecutionTool;
        this.websiteCrawlTool = websiteCrawlTool;
        this.ocrTool = ocrTool;
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        PersistentChatMemoryStore store = new PersistentChatMemoryStore();

        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();
    }

    @Bean
    public Assistant engineModel() {

        ChatLanguageModel llm = OpenAiChatModel.builder()
                .apiKey(openrouterApiKey)
                .modelName(GPT_4_O_MINI)
                .strictTools(true)
                .timeout(ofSeconds(360))
                .logRequests(false)
                .logResponses(false)
                .build();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(llm)
                .tools(googleSearchTool, jsFunctionExecutionTool, websiteCrawlTool, ocrTool)
                .chatMemoryProvider(chatMemoryProvider())
                .build();
    }

    static class PersistentChatMemoryStore implements ChatMemoryStore {

        private final Map<Integer, List<ChatMessage>> map = new HashMap<>();

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            List<ChatMessage> msgs =  map.get((int) memoryId);
            if (msgs == null) {
                msgs = new ArrayList<>();
            }
            return msgs;
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            map.put((int) memoryId, messages);
        }

        @Override
        public void deleteMessages(Object memoryId) {
            map.remove((int) memoryId);
        }
    }

}
