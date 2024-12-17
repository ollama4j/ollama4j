package io.github.ollama4j.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaToolCallsFunction
{
    private String name;
    private Map<String,Object> arguments;
}
