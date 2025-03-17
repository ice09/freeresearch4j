package indus340.tech.freeresearch4j.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import indus340.tech.freeresearch4j.engine.Assistant;
import indus340.tech.freeresearch4j.tools.*;
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

    private final String openAiApiKey;
    private final String mistralApiKey;

    private final GoogleSearchTool googleSearchTool;
    private final JSFunctionExecutionExternalJSTool jsFunctionExecutionExternalJSTool;
    private final WebsiteCrawlTool websiteCrawlTool;
    private final OCRTool ocrTool;
    private final DynamicBrowserAgent dynamicBrowserAgent;

    public ToolEngineConfig(@Value("${openai.apikey}") String openAiApiKey,
                            @Value("${mistralai.chat.apikey}") String mistralApiKey,
                            GoogleSearchTool googleSearchTool, JSFunctionExecutionExternalJSTool jsFunctionExecutionExternalJSTool, WebsiteCrawlTool websiteCrawlTool, OCRTool ocrTool, DynamicBrowserAgent dynamicBrowserAgent) {
        this.openAiApiKey = openAiApiKey;
        this.mistralApiKey = mistralApiKey;
        this.googleSearchTool = googleSearchTool;
        this.jsFunctionExecutionExternalJSTool = jsFunctionExecutionExternalJSTool;
        this.websiteCrawlTool = websiteCrawlTool;
        this.ocrTool = ocrTool;
        this.dynamicBrowserAgent = dynamicBrowserAgent;
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
    public Assistant openAiEngineModel() {
        ChatLanguageModel llm = OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(GPT_4_O_MINI)
                .strictTools(true)
                .timeout(ofSeconds(360))
                .logRequests(false)
                .logResponses(false)
                .build();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(llm)
                .tools(googleSearchTool, jsFunctionExecutionExternalJSTool, dynamicBrowserAgent, ocrTool)
                .chatMemoryProvider(chatMemoryProvider())
                .build();
    }

    //@Bean
    public Assistant mistralEngineModel() {
        ChatLanguageModel llm = MistralAiChatModel.builder()
                .apiKey(mistralApiKey)
                .modelName(MistralAiChatModelName.MISTRAL_SMALL_LATEST)
                .timeout(ofSeconds(360))
                .logRequests(false)
                .logResponses(false)
                .build();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(llm)
                .tools(googleSearchTool, jsFunctionExecutionExternalJSTool, dynamicBrowserAgent, ocrTool)
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
