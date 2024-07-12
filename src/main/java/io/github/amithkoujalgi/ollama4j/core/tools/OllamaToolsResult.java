package io.github.amithkoujalgi.ollama4j.core.tools;

import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaToolsResult {
    private OllamaResult modelResult;
    private Map<ToolDef, Object> toolResults;
}
