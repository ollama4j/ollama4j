package io.github.amithkoujalgi.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;

public abstract class AbstractRequestSerializationTest<T> {

    protected ObjectMapper mapper = Utils.getObjectMapper();

    protected String serializeRequest(T req) {
        try {
            return mapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            fail("Could not serialize request!", e);
            return null;
        }
    }

    protected T deserializeRequest(String jsonRequest, Class<T> requestClass) {
        try {
            return mapper.readValue(jsonRequest, requestClass);
        } catch (JsonProcessingException e) {
            fail("Could not deserialize jsonRequest!", e);
            return null;
        }
    }

    protected void assertEqualsAfterUnmarshalling(T unmarshalledRequest,
        T req) {
        assertEquals(req, unmarshalledRequest);
    }
}
