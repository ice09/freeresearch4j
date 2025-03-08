package indus340.tech.freeresearch4j.controller;

import java.util.List;

// Using a record for the response model
public record OpenAIResponse(
    String id,
    String object,
    long created,
    String model,
    List<Choice> choices
) {}