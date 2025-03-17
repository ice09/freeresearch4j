package indus340.tech.freeresearch4j.tools;


import dev.langchain4j.agent.tool.Tool;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//@Service
public class JSFunctionExecutionGraalVMTool {

    private static final Logger logger = LoggerFactory.getLogger(JSFunctionExecutionGraalVMTool.class);

    @Tool("Executes arbitrary JavaScript code without dependencies. Can use a Map<Object, Object> kvStore to store data for other steps and read data from other steps.")
    public String executeJs(String jsCode) {
        Map<Object, Object> kvStore = new HashMap<>();
        logger.info("EXECUTE JS: {}", jsCode);
        try (Context jsContext = Context.newBuilder().allowAllAccess(true).build()) {
            jsContext.getBindings("js").putMember("kvStore", kvStore);
            Value value = jsContext.eval("js", jsCode);
            String res = value.toString();
            logger.info("JS RESULT: {}", res);
            return res;
        }
    }
}
