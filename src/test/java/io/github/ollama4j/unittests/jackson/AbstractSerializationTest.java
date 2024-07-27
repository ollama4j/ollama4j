package io.github.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ollama4j.utils.Utils;

public abstract class AbstractSerializationTest<T> {

    protected ObjectMapper mapper = Utils.getObjectMapper();

    protected String serialize(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            fail("Could not serialize request!", e);
            return null;
        }
    }

    protected T deserialize(String jsonObject, Class<T> deserializationClass) {
        try {
            return mapper.readValue(jsonObject, deserializationClass);
        } catch (JsonProcessingException e) {
            fail("Could not deserialize jsonObject!", e);
            return null;
        }
    }

    protected void assertEqualsAfterUnmarshalling(T unmarshalledObject,
        T req) {
        assertEquals(req, unmarshalledObject);
    }
}
