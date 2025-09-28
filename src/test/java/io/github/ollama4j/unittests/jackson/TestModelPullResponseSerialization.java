/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.response.ModelPullResponse;
import org.junit.jupiter.api.Test;

/**
 * Test serialization and deserialization of ModelPullResponse,
 * This test verifies that the ModelPullResponse class can properly parse
 * error responses from Ollama server that return HTTP 200 with error messages
 * in the JSON body.
 */
class TestModelPullResponseSerialization extends AbstractSerializationTest<ModelPullResponse> {

    /**
     * Test the specific error case reported in GitHub issue #138.
     * Ollama sometimes returns HTTP 200 with error details in JSON.
     */
    @Test
    public void testDeserializationWithErrorFromGitHubIssue138() {
        // This is the exact error JSON from GitHub issue #138
        String errorJson =
                "{\"error\":\"pull model manifest: 412: \\n"
                    + "\\n"
                    + "The model you are attempting to pull requires a newer version of Ollama.\\n"
                    + "\\n"
                    + "Please download the latest version at:\\n"
                    + "\\n"
                    + "\\thttps://ollama.com/download\\n"
                    + "\\n"
                    + "\"}";

        ModelPullResponse response = deserialize(errorJson, ModelPullResponse.class);

        assertNotNull(response);
        assertNotNull(response.getError());
        assertTrue(response.getError().contains("newer version of Ollama"));
        assertTrue(response.getError().contains("https://ollama.com/download"));
        assertNull(response.getStatus());
        assertNull(response.getDigest());
        assertEquals(0, response.getTotal());
        assertEquals(0, response.getCompleted());
    }

    /**
     * Test deserialization of ModelPullResponse with only status field present.
     * Verifies that the response can handle minimal JSON with just status information.
     */
    @Test
    public void testDeserializationWithStatusField() {
        String statusJson = "{\"status\":\"pulling manifest\"}";

        ModelPullResponse response = deserialize(statusJson, ModelPullResponse.class);

        assertNotNull(response);
        assertEquals("pulling manifest", response.getStatus());
        assertNull(response.getError());
        assertNull(response.getDigest());
        assertEquals(0, response.getTotal());
        assertEquals(0, response.getCompleted());
    }

    /**
     * Test deserialization of ModelPullResponse with progress tracking fields.
     * Verifies that status, digest, total, and completed fields are properly parsed
     * when downloading/pulling model data.
     */
    @Test
    public void testDeserializationWithProgressFields() {
        String progressJson =
                "{\"status\":\"pulling"
                    + " digestname\",\"digest\":\"sha256:abc123\",\"total\":2142590208,\"completed\":241970}";

        ModelPullResponse response = deserialize(progressJson, ModelPullResponse.class);

        assertNotNull(response);
        assertEquals("pulling digestname", response.getStatus());
        assertEquals("sha256:abc123", response.getDigest());
        assertEquals(2142590208L, response.getTotal());
        assertEquals(241970L, response.getCompleted());
        assertNull(response.getError());
    }

    /**
     * Test deserialization of ModelPullResponse with success status.
     * Verifies that successful completion responses are properly handled.
     */
    @Test
    public void testDeserializationWithSuccessStatus() {
        String successJson = "{\"status\":\"success\"}";

        ModelPullResponse response = deserialize(successJson, ModelPullResponse.class);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNull(response.getError());
        assertNull(response.getDigest());
        assertEquals(0, response.getTotal());
        assertEquals(0, response.getCompleted());
    }

    /**
     * Test deserialization of ModelPullResponse with all possible fields populated.
     * Verifies that complete JSON responses with all fields are handled correctly.
     */
    @Test
    public void testDeserializationWithAllFields() {
        String completeJson =
                "{\"status\":\"downloading\",\"digest\":\"sha256:def456\",\"total\":1000000,\"completed\":500000,\"error\":null}";

        ModelPullResponse response = deserialize(completeJson, ModelPullResponse.class);

        assertNotNull(response);
        assertEquals("downloading", response.getStatus());
        assertEquals("sha256:def456", response.getDigest());
        assertEquals(1000000L, response.getTotal());
        assertEquals(500000L, response.getCompleted());
        assertNull(response.getError());
    }

    /**
     * Test deserialization of ModelPullResponse with unknown JSON fields.
     * Verifies that unknown fields are ignored due to @JsonIgnoreProperties(ignoreUnknown = true)
     * annotation without causing deserialization errors.
     */
    @Test
    public void testDeserializationWithUnknownFields() {
        // Test that unknown fields are ignored due to @JsonIgnoreProperties(ignoreUnknown = true)
        String jsonWithUnknownFields =
                "{\"status\":\"pulling\",\"unknown_field\":\"should_be_ignored\",\"error\":\"test"
                    + " error\",\"another_unknown\":123,\"nested_unknown\":{\"key\":\"value\"}}";

        ModelPullResponse response = deserialize(jsonWithUnknownFields, ModelPullResponse.class);

        assertNotNull(response);
        assertEquals("pulling", response.getStatus());
        assertEquals("test error", response.getError());
        assertNull(response.getDigest());
        assertEquals(0, response.getTotal());
        assertEquals(0, response.getCompleted());
        // Unknown fields should be ignored
    }

    /**
     * Test deserialization of ModelPullResponse with empty string error field.
     * Verifies that empty error strings are preserved as empty strings, not converted to null.
     */
    @Test
    public void testEmptyErrorFieldIsNull() {
        String emptyErrorJson = "{\"error\":\"\",\"status\":\"pulling manifest\"}";

        ModelPullResponse response = deserialize(emptyErrorJson, ModelPullResponse.class);

        assertNotNull(response);
        assertEquals("pulling manifest", response.getStatus());
        assertEquals("", response.getError()); // Empty string, not null
    }

    /**
     * Test deserialization of ModelPullResponse with whitespace-only error field.
     * Verifies that whitespace characters in error fields are preserved during JSON parsing.
     */
    @Test
    public void testWhitespaceOnlyErrorField() {
        String whitespaceErrorJson = "{\"error\":\"   \\n\\t  \",\"status\":\"pulling manifest\"}";

        ModelPullResponse response = deserialize(whitespaceErrorJson, ModelPullResponse.class);

        assertNotNull(response);
        assertEquals("pulling manifest", response.getStatus());
        assertEquals("   \n\t  ", response.getError()); // Whitespace preserved in JSON parsing
        assertTrue(response.getError().trim().isEmpty()); // But trimmed version is empty
    }

    /**
     * Test serialization of ModelPullResponse with error field to ensure round-trip compatibility.
     * Verifies that objects can be properly serialized to JSON format.
     */
    @Test
    public void testSerializationWithErrorField() {
        ModelPullResponse response = new ModelPullResponse();
        response.setError("Test error message");
        response.setStatus("failed");

        String jsonString = serialize(response);

        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"error\":\"Test error message\""));
        assertTrue(jsonString.contains("\"status\":\"failed\""));
    }

    /**
     * Test round-trip serialization and deserialization of ModelPullResponse with error data.
     * Verifies that objects maintain integrity through serialize -> deserialize cycle.
     */
    @Test
    public void testRoundTripSerializationWithError() {
        ModelPullResponse original = new ModelPullResponse();
        original.setError("Round trip test error");
        original.setStatus("error");

        String json = serialize(original);
        ModelPullResponse deserialized = deserialize(json, ModelPullResponse.class);

        assertEqualsAfterUnmarshalling(deserialized, original);
        assertEquals("Round trip test error", deserialized.getError());
        assertEquals("error", deserialized.getStatus());
    }

    /**
     * Test round-trip serialization and deserialization of ModelPullResponse with progress data.
     * Verifies that progress tracking information is preserved through serialize -> deserialize cycle.
     */
    @Test
    public void testRoundTripSerializationWithProgress() {
        ModelPullResponse original = new ModelPullResponse();
        original.setStatus("downloading");
        original.setDigest("sha256:roundtrip");
        original.setTotal(2000000L);
        original.setCompleted(1500000L);

        String json = serialize(original);
        ModelPullResponse deserialized = deserialize(json, ModelPullResponse.class);

        assertEqualsAfterUnmarshalling(deserialized, original);
        assertEquals("downloading", deserialized.getStatus());
        assertEquals("sha256:roundtrip", deserialized.getDigest());
        assertEquals(2000000L, deserialized.getTotal());
        assertEquals(1500000L, deserialized.getCompleted());
        assertNull(deserialized.getError());
    }

    /**
     * Test that verifies the error handling logic that would be used in doPullModel method.
     * This simulates the actual error detection logic.
     */
    @Test
    public void testErrorHandlingLogic() {
        // Error case - should trigger error handling
        String errorJson = "{\"error\":\"test error\"}";
        ModelPullResponse errorResponse = deserialize(errorJson, ModelPullResponse.class);

        assertTrue(
                errorResponse.getError() != null && !errorResponse.getError().trim().isEmpty(),
                "Error response should trigger error handling logic");

        // Normal case - should not trigger error handling
        String normalJson = "{\"status\":\"pulling\"}";
        ModelPullResponse normalResponse = deserialize(normalJson, ModelPullResponse.class);

        assertFalse(
                normalResponse.getError() != null && !normalResponse.getError().trim().isEmpty(),
                "Normal response should not trigger error handling logic");

        // Empty error case - should not trigger error handling
        String emptyErrorJson = "{\"error\":\"\",\"status\":\"pulling\"}";
        ModelPullResponse emptyErrorResponse = deserialize(emptyErrorJson, ModelPullResponse.class);

        assertFalse(
                emptyErrorResponse.getError() != null
                        && !emptyErrorResponse.getError().trim().isEmpty(),
                "Empty error response should not trigger error handling logic");
    }
}
