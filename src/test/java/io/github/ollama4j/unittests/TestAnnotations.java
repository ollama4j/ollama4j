package io.github.ollama4j.unittests;

import io.github.ollama4j.tools.annotations.OllamaToolService;
import io.github.ollama4j.tools.annotations.ToolProperty;
import io.github.ollama4j.tools.annotations.ToolSpec;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.junit.jupiter.api.Assertions.*;

class TestAnnotations {

    @OllamaToolService(providers = {SampleProvider.class})
    static class SampleToolService {
    }

    static class SampleProvider {
        @ToolSpec(name = "sum", desc = "adds two numbers")
        public int sum(@ToolProperty(name = "a", desc = "first addend") int a,
                       @ToolProperty(name = "b", desc = "second addend", required = false) int b) {
            return a + b;
        }
    }

    @Test
    void testOllamaToolServiceProvidersPresent() throws Exception {
        OllamaToolService ann = SampleToolService.class.getAnnotation(OllamaToolService.class);
        assertNotNull(ann);
        assertArrayEquals(new Class<?>[]{SampleProvider.class}, ann.providers());
    }

    @Test
    void testToolPropertyMetadataOnParameters() throws Exception {
        Method m = SampleProvider.class.getDeclaredMethod("sum", int.class, int.class);
        Parameter[] params = m.getParameters();
        ToolProperty p0 = params[0].getAnnotation(ToolProperty.class);
        ToolProperty p1 = params[1].getAnnotation(ToolProperty.class);
        assertNotNull(p0);
        assertEquals("a", p0.name());
        assertEquals("first addend", p0.desc());
        assertTrue(p0.required());

        assertNotNull(p1);
        assertEquals("b", p1.name());
        assertEquals("second addend", p1.desc());
        assertFalse(p1.required());
    }
}
