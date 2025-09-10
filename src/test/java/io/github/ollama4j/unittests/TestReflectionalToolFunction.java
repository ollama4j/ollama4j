package io.github.ollama4j.unittests;

import io.github.ollama4j.tools.ReflectionalToolFunction;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestReflectionalToolFunction {

    public static class SampleToolHolder {
        public String combine(Integer i, Boolean b, BigDecimal d, String s) {
            return String.format("i=%s,b=%s,d=%s,s=%s", i, b, d, s);
        }

        public void alwaysThrows() {
            throw new IllegalStateException("boom");
        }
    }

    @Test
    void testApplyInvokesMethodWithTypeCasting() throws Exception {
        SampleToolHolder holder = new SampleToolHolder();
        Method method = SampleToolHolder.class.getMethod("combine", Integer.class, Boolean.class, BigDecimal.class, String.class);

        LinkedHashMap<String, String> propDef = new LinkedHashMap<>();
        // preserve order to match method parameters
        propDef.put("i", "java.lang.Integer");
        propDef.put("b", "java.lang.Boolean");
        propDef.put("d", "java.math.BigDecimal");
        propDef.put("s", "java.lang.String");

        ReflectionalToolFunction fn = new ReflectionalToolFunction(holder, method, propDef);

        Map<String, Object> args = Map.of(
                "i", "42",
                "b", "true",
                "d", "3.14",
                "s", 123 // not a string; should be toString()'d by implementation
        );

        Object result = fn.apply(args);
        assertEquals("i=42,b=true,d=3.14,s=123", result);
    }

    @Test
    void testTypeCastNullsWhenClassOrValueIsNull() throws Exception {
        SampleToolHolder holder = new SampleToolHolder();
        Method method = SampleToolHolder.class.getMethod("combine", Integer.class, Boolean.class, BigDecimal.class, String.class);

        LinkedHashMap<String, String> propDef = new LinkedHashMap<>();
        propDef.put("i", null); // className null -> expect null passed
        propDef.put("b", "java.lang.Boolean");
        propDef.put("d", "java.math.BigDecimal");
        propDef.put("s", "java.lang.String");

        ReflectionalToolFunction fn = new ReflectionalToolFunction(holder, method, propDef);

        Map<String, Object> args = new LinkedHashMap<>();
        args.put("i", "100"); // ignored -> becomes null due to null className
        args.put("b", null); // value null -> expect null passed
        args.put("d", "1.00");
        args.put("s", "ok");

        Object result = fn.apply(args);
        assertEquals("i=null,b=null,d=1.00,s=ok", result);
    }

    @Test
    void testExceptionsAreWrappedWithMeaningfulMessage() throws Exception {
        SampleToolHolder holder = new SampleToolHolder();
        Method throwsMethod = SampleToolHolder.class.getMethod("alwaysThrows");

        LinkedHashMap<String, String> propDef = new LinkedHashMap<>();

        ReflectionalToolFunction fn = new ReflectionalToolFunction(holder, throwsMethod, propDef);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> fn.apply(Map.of()));
        assertTrue(ex.getMessage().contains("Failed to invoke tool: alwaysThrows"));
        assertNotNull(ex.getCause());
    }
}
