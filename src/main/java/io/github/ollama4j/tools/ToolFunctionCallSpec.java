package io.github.ollama4j.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolFunctionCallSpec {
    private String name;
    private Map<String, Object> arguments;
}

