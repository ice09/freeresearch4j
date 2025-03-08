// src/main/java/com/example/demo/ChatCompletionResponse.java
package indus340.tech.freeresearch4j.controller;

import java.util.List;

// Represents the response returned by the chat completions endpoint
public record ChatCompletionResponse(
    String id,
    String object,
    long created,
    String model,
    List<Choice> choices
) {}
