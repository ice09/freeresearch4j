package indus340.tech.freeresearch4j.tools;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JSFunctionExecutionTool {

    @Tool("Executes arbitrary JavaScript code without dependencies. Can use a Map<Object, Object> kvStore to store data for other steps and read data from other steps.")
    public String executeJs(String jsCode) {
        Map<Object, Object> kvStore = new HashMap<>();
        System.out.println(("EXECUTE JS: " + jsCode));
        try (Context jsContext = Context.newBuilder().allowAllAccess(true).build()) {
            jsContext.getBindings("js").putMember("kvStore", kvStore);
            Value value = jsContext.eval("js", jsCode);
            String res = value.toString();
            System.out.println("JS RESULT: " + res);
            return res;
        }
    }
}
