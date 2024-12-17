package io.github.ollama4j.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ToolRegistry {
    private final Map<String, Tools.ToolSpecification> tools = new HashMap<>();

    public ToolFunction getToolFunction(String name) {
        final Tools.ToolSpecification toolSpecification = tools.get(name);
        return toolSpecification !=null ? toolSpecification.getToolFunction() : null ;
    }

    public void addTool (String name, Tools.ToolSpecification specification) {
        tools.put(name, specification);
    }

    public Collection<Tools.ToolSpecification> getRegisteredSpecs(){
        return tools.values();
    }
}
