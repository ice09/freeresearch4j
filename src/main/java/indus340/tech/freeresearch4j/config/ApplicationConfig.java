package indus340.tech.freeresearch4j.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import indus340.tech.freeresearch4j.engine.WebsiteCrawlAssistant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.time.Duration.ofSeconds;

@Configuration
public class ApplicationConfig {

    private final String openAiApiKey;
    private final String mistralAiKey;

    public ApplicationConfig(@Value("${openai.apikey}") String openAiApiKey,
                             @Value("${mistralai.chat.apikey}") String mistralAiKey) {
        this.openAiApiKey = openAiApiKey;
        this.mistralAiKey = mistralAiKey;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebsiteCrawlAssistant openAiWebsiteAssistant() {
        ChatLanguageModel llm = OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(GPT_4_O_MINI)
                .strictTools(true)
                .strictJsonSchema(true)
                .timeout(ofSeconds(360))
                .logRequests(false)
                .logResponses(false)
                .build();

        return AiServices.builder(WebsiteCrawlAssistant.class)
                .chatLanguageModel(llm)
                .build();
    }

    //@Bean
    public WebsiteCrawlAssistant mistralWebsiteAssistant() {
        MistralAiChatModel llm = MistralAiChatModel.builder()
                .apiKey(mistralAiKey)
                .modelName(MistralAiChatModelName.MISTRAL_SMALL_LATEST)
                .timeout(ofSeconds(360))
                .logRequests(false)
                .logResponses(false)
                .build();

        return AiServices.builder(WebsiteCrawlAssistant.class)
                .chatLanguageModel(llm)
                .build();
    }

}
