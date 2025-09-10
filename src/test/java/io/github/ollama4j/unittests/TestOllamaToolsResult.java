package io.github.ollama4j.unittests;

import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.tools.OllamaToolsResult;
import io.github.ollama4j.tools.ToolFunctionCallSpec;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestOllamaToolsResult {

    @Test
    public void testGetToolResultsTransformsMapToList() {
        ToolFunctionCallSpec spec1 = new ToolFunctionCallSpec("fn1", Map.of("a", 1));
        ToolFunctionCallSpec spec2 = new ToolFunctionCallSpec("fn2", Map.of("b", 2));

        Map<ToolFunctionCallSpec, Object> toolMap = new LinkedHashMap<>();
        toolMap.put(spec1, "r1");
        toolMap.put(spec2, 123);

        OllamaToolsResult tr = new OllamaToolsResult(new OllamaResult("", null, 0L, 200), toolMap);

        List<OllamaToolsResult.ToolResult> list = tr.getToolResults();
        assertEquals(2, list.size());
        assertEquals("fn1", list.get(0).getFunctionName());
        assertEquals(Map.of("a", 1), list.get(0).getFunctionArguments());
        assertEquals("r1", list.get(0).getResult());

        assertEquals("fn2", list.get(1).getFunctionName());
        assertEquals(Map.of("b", 2), list.get(1).getFunctionArguments());
        assertEquals(123, list.get(1).getResult());
    }

    @Test
    public void testGetToolResultsReturnsEmptyListWhenNull() {
        OllamaToolsResult tr = new OllamaToolsResult();
        tr.setToolResults(null);
        List<OllamaToolsResult.ToolResult> list = tr.getToolResults();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}
