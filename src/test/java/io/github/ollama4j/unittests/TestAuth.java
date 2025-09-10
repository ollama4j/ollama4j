package io.github.ollama4j.unittests;

import io.github.ollama4j.models.request.BasicAuth;
import io.github.ollama4j.models.request.BearerAuth;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAuth {

    @Test
    public void testBasicAuthHeaderEncoding() {
        BasicAuth auth = new BasicAuth("alice", "s3cr3t");
        String header = auth.getAuthHeaderValue();
        assertTrue(header.startsWith("Basic "));
        // "alice:s3cr3t" base64 is "YWxpY2U6czNjcjN0"
        assertEquals("Basic YWxpY2U6czNjcjN0", header);
    }

    @Test
    public void testBearerAuthHeaderFormat() {
        BearerAuth auth = new BearerAuth("abc.def.ghi");
        String header = auth.getAuthHeaderValue();
        assertEquals("Bearer abc.def.ghi", header);
    }
}
