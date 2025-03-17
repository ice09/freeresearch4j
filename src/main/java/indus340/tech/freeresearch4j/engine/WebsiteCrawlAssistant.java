package indus340.tech.freeresearch4j.engine;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface WebsiteCrawlAssistant {

    @SystemMessage("""
           You are a web site assistant, you will review the user message (prefixed with "USERMESSAGE") and then read the website content (prefixed "WEBSITECONTENT").
           You should return the relevant content from the website and you can decide to return up to 10 links from the page.
           If enough information is already available, return user_message_answered as "true". If you think other links have better information, return "false".
           The return format should be JSON and ONLY the JSON like this:
           {
               "content": "RELEVANT_WEBSITE_CONTENT",
               "links": [
                "LINK1",
                "LINK2",
                "LINK3"
                ],
                "user_message_answered": "true"
           }
           """)
    String chat(@UserMessage String message);

}
