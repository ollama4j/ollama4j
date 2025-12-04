/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.metrics.MetricsRecorder;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatTokenHandler;
import io.github.ollama4j.models.embed.OllamaEmbedRequest;
import io.github.ollama4j.models.embed.OllamaEmbedResult;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import io.github.ollama4j.models.ps.ModelProcessesResult;
import io.github.ollama4j.models.request.*;
import io.github.ollama4j.models.response.*;
import io.github.ollama4j.tools.*;
import io.github.ollama4j.tools.annotations.OllamaToolService;
import io.github.ollama4j.tools.annotations.ToolProperty;
import io.github.ollama4j.tools.annotations.ToolSpec;
import io.github.ollama4j.utils.Constants;
import io.github.ollama4j.utils.Utils;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import java.io.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main API class for interacting with the Ollama server.
 *
 * <p>This class provides methods for model management, chat, embeddings, tool registration, and
 * more.
 */
@SuppressWarnings({"DuplicatedCode", "resource", "SpellCheckingInspection"})
public class Ollama {

    private static final Logger LOG = LoggerFactory.getLogger(Ollama.class);

    private final String host;
    private Auth auth;

    private final ToolRegistry toolRegistry = new ToolRegistry();

    /**
     * The request timeout in seconds for API calls.
     *
     * <p>Default is 10 seconds. This value determines how long the client will wait for a response
     * from the Ollama server before timing out.
     */
    @Setter private long requestTimeoutSeconds = 10;

    /** The read timeout in seconds for image URLs. */
    @Setter private int imageURLReadTimeoutSeconds = 10;

    /** The connect timeout in seconds for image URLs. */
    @Setter private int imageURLConnectTimeoutSeconds = 10;

    /**
     * The maximum number of retries for tool calls during chat interactions.
     *
     * <p>This value controls how many times the API will attempt to call a tool in the event of a
     * failure. Default is 3.
     */
    @Setter private int maxChatToolCallRetries = 3;

    /**
     * The number of retries to attempt when pulling a model from the Ollama server.
     *
     * <p>If set to 0, no retries will be performed. If greater than 0, the API will retry pulling the
     * model up to the specified number of times in case of failure.
     *
     * <p>Default is 0 (no retries).
     */
    @Setter
    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private int numberOfRetriesForModelPull = 0;

    /**
     * Enable or disable Prometheus metrics collection.
     *
     * <p>When enabled, the API will collect and expose metrics for request counts, durations, model
     * usage, and other operational statistics. Default is false.
     */
    @Setter private boolean metricsEnabled = false;

    /** Instantiates the Ollama API with the default Ollama host: {@code http://localhost:11434} */
    public Ollama() {
        this.host = "http://localhost:11434";
    }

    /**
     * Instantiates the Ollama API with a specified Ollama host address.
     *
     * @param host the host address of the Ollama server
     */
    public Ollama(String host) {
        if (host.endsWith("/")) {
            this.host = host.substring(0, host.length() - 1);
        } else {
            this.host = host;
        }
        LOG.info("Ollama4j client initialized. Connected to Ollama server at: {}", this.host);
    }

    /**
     * Set basic authentication for accessing an Ollama server that's behind a reverse-proxy/gateway.
     *
     * @param username the username
     * @param password the password
     */
    public void setBasicAuth(String username, String password) {
        this.auth = new BasicAuth(username, password);
    }

    /**
     * Set Bearer authentication for accessing an Ollama server that's behind a reverse-proxy/gateway.
     *
     * @param bearerToken the Bearer authentication token to provide
     */
    public void setBearerAuth(String bearerToken) {
        this.auth = new BearerAuth(bearerToken);
    }

    /**
     * Checks the reachability of the Ollama server.
     *
     * @return true if the server is reachable, false otherwise
     * @throws OllamaException if the ping fails
     */
    public boolean ping() throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/tags";
        int statusCode = -1;
        Object out = null;
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest;
            HttpResponse<String> response;
            httpRequest =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .GET()
                            .build();
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            return statusCode == 200;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Ping interrupted", ie);
        } catch (Exception e) {
            throw new OllamaException("Ping failed", e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Provides a list of running models and details about each model currently loaded into memory.
     *
     * @return ModelsProcessResult containing details about the running models
     * @throws OllamaException if the response indicates an error status
     */
    public ModelProcessesResult ps() throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/ps";
        int statusCode = -1;
        Object out = null;
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = null;
            try {
                httpRequest =
                        getRequestBuilderDefault(new URI(this.host + url))
                                .header(
                                        Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                        Constants.HttpConstants.APPLICATION_JSON)
                                .header(
                                        Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                        Constants.HttpConstants.APPLICATION_JSON)
                                .GET()
                                .build();
            } catch (URISyntaxException e) {
                throw new OllamaException(e.getMessage(), e);
            }
            HttpResponse<String> response = null;
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseString = response.body();
            if (statusCode == 200) {
                return Utils.getObjectMapper()
                        .readValue(responseString, ModelProcessesResult.class);
            } else {
                throw new OllamaException(statusCode + " - " + responseString);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OllamaException("ps interrupted", ie);
        } catch (Exception e) {
            throw new OllamaException("ps failed", e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Lists available models from the Ollama server.
     *
     * @return a list of models available on the server
     * @throws OllamaException if the response indicates an error status
     */
    public List<Model> listModels() throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/tags";
        int statusCode = -1;
        Object out = null;
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .GET()
                            .build();
            HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseString = response.body();
            if (statusCode == 200) {
                return Utils.getObjectMapper()
                        .readValue(responseString, ListModelsResponse.class)
                        .getModels();
            } else {
                throw new OllamaException(statusCode + " - " + responseString);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OllamaException("listModels interrupted", ie);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Handles retry backoff for pullModel.
     *
     * @param modelName the name of the model being pulled
     * @param currentRetry the current retry attempt (zero-based)
     * @param maxRetries the maximum number of retries allowed
     * @param baseDelayMillis the base delay in milliseconds for exponential backoff
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private void handlePullRetry(
            String modelName, int currentRetry, int maxRetries, long baseDelayMillis)
            throws InterruptedException {
        int attempt = currentRetry + 1;
        if (attempt < maxRetries) {
            long backoffMillis = baseDelayMillis * (1L << currentRetry);
            LOG.error(
                    "Failed to pull model {}, retrying in {}s... (attempt {}/{})",
                    modelName,
                    backoffMillis / 1000,
                    attempt,
                    maxRetries);
            try {
                Thread.sleep(backoffMillis);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw ie;
            }
        } else {
            LOG.error(
                    "Failed to pull model {} after {} attempts, no more retries.",
                    modelName,
                    maxRetries);
        }
    }

    /**
     * Internal method to pull a model from the Ollama server.
     *
     * @param modelName the name of the model to pull
     * @throws OllamaException if the pull fails
     */
    private void doPullModel(String modelName) throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/pull";
        int statusCode = -1;
        Object out = null;
        try {
            String jsonData = new ModelRequest(modelName).toString();
            HttpRequest request =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            statusCode = response.statusCode();
            InputStream responseBodyStream = response.body();
            String responseString = "";
            boolean success = false; // Flag to check the pull success.

            try (BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    ModelPullResponse modelPullResponse =
                            Utils.getObjectMapper().readValue(line, ModelPullResponse.class);
                    success = processModelPullResponse(modelPullResponse, modelName) || success;
                }
            }
            if (!success) {
                LOG.error("Model pull failed or returned invalid status.");
                throw new OllamaException("Model pull failed or returned invalid status.");
            }
            if (statusCode != 200) {
                throw new OllamaException(statusCode + " - " + responseString);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted during model pull.", ie);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Processes a single ModelPullResponse, handling errors and logging status. Returns true if the
     * response indicates a successful pull.
     *
     * @param modelPullResponse the response from the model pull
     * @param modelName the name of the model
     * @return true if the pull was successful, false otherwise
     * @throws OllamaException if the response contains an error
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean processModelPullResponse(ModelPullResponse modelPullResponse, String modelName)
            throws OllamaException {
        if (modelPullResponse == null) {
            LOG.error("Received null response for model pull.");
            return false;
        }
        String error = modelPullResponse.getError();
        if (error != null && !error.trim().isEmpty()) {
            throw new OllamaException("Model pull failed: " + error);
        }
        String status = modelPullResponse.getStatus();
        if (status != null) {
            LOG.debug("{}: {}", modelName, status);
            if ("success".equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the Ollama server version.
     *
     * @return the version string
     * @throws OllamaException if the request fails
     */
    public String getVersion() throws OllamaException {
        String url = "/api/version";
        long startTime = System.currentTimeMillis();
        int statusCode = -1;
        Object out = null;
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .GET()
                            .build();
            HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseString = response.body();
            if (statusCode == 200) {
                return Utils.getObjectMapper()
                        .readValue(responseString, OllamaVersion.class)
                        .getVersion();
            } else {
                throw new OllamaException(statusCode + " - " + responseString);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", ie);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Pulls a model using the specified Ollama library model tag. The model is identified by a name
     * and a tag, which are combined into a single identifier in the format "name:tag" to pull the
     * corresponding model.
     *
     * @param modelName the name/tag of the model to be pulled. Ex: llama3:latest
     * @throws OllamaException if the response indicates an error status
     */
    public void pullModel(String modelName) throws OllamaException {
        try {
            if (numberOfRetriesForModelPull == 0) {
                this.doPullModel(modelName);
                return;
            }
            int numberOfRetries = 0;
            long baseDelayMillis = 3000L; // 3 seconds base delay
            while (numberOfRetries < numberOfRetriesForModelPull) {
                try {
                    this.doPullModel(modelName);
                    return;
                } catch (OllamaException e) {
                    handlePullRetry(
                            modelName,
                            numberOfRetries,
                            numberOfRetriesForModelPull,
                            baseDelayMillis);
                    numberOfRetries++;
                }
            }
            throw new OllamaException(
                    "Failed to pull model "
                            + modelName
                            + " after "
                            + numberOfRetriesForModelPull
                            + " retries");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", ie);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        }
    }

    /**
     * Gets model details from the Ollama server.
     *
     * @param modelName the model name
     * @return the model details
     * @throws OllamaException if the response indicates an error status
     */
    public ModelDetail getModelDetails(String modelName) throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/show";
        int statusCode = -1;
        Object out = null;
        try {
            String jsonData = new ModelRequest(modelName).toString();
            HttpRequest request =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                            .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseBody = response.body();
            if (statusCode == 200) {
                return Utils.getObjectMapper().readValue(responseBody, ModelDetail.class);
            } else {
                throw new OllamaException(statusCode + " - " + responseBody);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", ie);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Creates a custom model. Read more about custom model creation <a href=
     * "https://github.com/ollama/ollama/blob/main/docs/api.md#create-a-model">here</a>.
     *
     * @param customModelRequest custom model spec
     * @throws OllamaException if the response indicates an error status
     */
    public void createModel(CustomModelRequest customModelRequest) throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/create";
        int statusCode = -1;
        Object out = null;
        try {
            String jsonData = customModelRequest.toString();
            HttpRequest request =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            jsonData, StandardCharsets.UTF_8))
                            .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            statusCode = response.statusCode();
            if (statusCode != 200) {
                String errorBody =
                        new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                out = errorBody;
                throw new OllamaException(statusCode + " - " + errorBody);
            }
            try (BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                String line;
                StringBuilder lines = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    ModelPullResponse res =
                            Utils.getObjectMapper().readValue(line, ModelPullResponse.class);
                    lines.append(line);
                    LOG.debug(res.getStatus());
                    if (res.getError() != null) {
                        out = res.getError();
                        throw new OllamaException(res.getError());
                    }
                }
                out = lines;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", e);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Deletes a model from the Ollama server.
     *
     * @param modelName the name of the model to be deleted
     * @param ignoreIfNotPresent ignore errors if the specified model is not present on the Ollama
     *     server
     * @throws OllamaException if the response indicates an error status
     */
    public void deleteModel(String modelName, boolean ignoreIfNotPresent) throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/delete";
        int statusCode = -1;
        Object out = null;
        try {
            String jsonData = new ModelRequest(modelName).toString();
            HttpRequest request =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .method(
                                    "DELETE",
                                    HttpRequest.BodyPublishers.ofString(
                                            jsonData, StandardCharsets.UTF_8))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseBody = response.body();
            out = responseBody;
            if (statusCode == 404
                    && responseBody.contains("model")
                    && responseBody.contains("not found")) {
                return;
            }
            if (statusCode != 200) {
                throw new OllamaException(statusCode + " - " + responseBody);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", e);
        } catch (Exception e) {
            throw new OllamaException(statusCode + " - " + out, e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Unloads a model from memory.
     *
     * <p>If an empty prompt is provided and the keep_alive parameter is set to 0, a model will be
     * unloaded from memory.
     *
     * @param modelName the name of the model to unload
     * @throws OllamaException if the response indicates an error status
     */
    public void unloadModel(String modelName) throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/generate";
        int statusCode = -1;
        Object out = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = new java.util.HashMap<>();
            jsonMap.put("model", modelName);
            jsonMap.put("keep_alive", 0);
            String jsonData = objectMapper.writeValueAsString(jsonMap);
            HttpRequest request =
                    getRequestBuilderDefault(new URI(this.host + url))
                            .method(
                                    "POST",
                                    HttpRequest.BodyPublishers.ofString(
                                            jsonData, StandardCharsets.UTF_8))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .build();
            LOG.debug("Unloading model with request: {}", jsonData);
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseBody = response.body();
            if (statusCode == 404
                    && responseBody.contains("model")
                    && responseBody.contains("not found")) {
                LOG.debug("Unload response: {} - {}", statusCode, responseBody);
                return;
            }
            if (statusCode != 200) {
                LOG.debug("Unload response: {} - {}", statusCode, responseBody);
                throw new OllamaException(statusCode + " - " + responseBody);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.debug("Unload interrupted: {} - {}", statusCode, out);
            throw new OllamaException(statusCode + " - " + out, e);
        } catch (Exception e) {
            LOG.debug("Unload failed: {} - {}", statusCode, out);
            throw new OllamaException(statusCode + " - " + out, e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Generate embeddings using a {@link OllamaEmbedRequest}.
     *
     * @param modelRequest request for '/api/embed' endpoint
     * @return embeddings
     * @throws OllamaException if the response indicates an error status
     */
    public OllamaEmbedResult embed(OllamaEmbedRequest modelRequest) throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/embed";
        int statusCode = -1;
        Object out = null;
        try {
            String jsonData = Utils.getObjectMapper().writeValueAsString(modelRequest);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request =
                    HttpRequest.newBuilder(new URI(this.host + url))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_ACCEPT,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                            .build();
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseBody = response.body();
            if (statusCode == 200) {
                return Utils.getObjectMapper().readValue(responseBody, OllamaEmbedResult.class);
            } else {
                throw new OllamaException(statusCode + " - " + responseBody);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", e);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url,
                    "",
                    false,
                    ThinkMode.DISABLED,
                    false,
                    null,
                    null,
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Generates a response from a model using the specified parameters and stream observer. If {@code
     * streamObserver} is provided, streaming is enabled; otherwise, a synchronous call is made.
     *
     * @param request the generation request
     * @param streamObserver the stream observer for streaming responses, or null for synchronous
     * @return the result of the generation
     * @throws OllamaException if the request fails
     */
    public OllamaResult generate(
            OllamaGenerateRequest request, OllamaGenerateStreamObserver streamObserver)
            throws OllamaException {
        try {
            if (request.isUseTools()) {
                return generateWithToolsInternal(request, streamObserver);
            }

            if (streamObserver != null) {
                if (!request.getThink().equals(ThinkMode.DISABLED)) {
                    return generateSyncForOllamaRequestModel(
                            request,
                            streamObserver.getThinkingStreamHandler(),
                            streamObserver.getResponseStreamHandler());
                } else {
                    return generateSyncForOllamaRequestModel(
                            request, null, streamObserver.getResponseStreamHandler());
                }
            }
            return generateSyncForOllamaRequestModel(request, null, null);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        }
    }

    // (No javadoc for private helper, as is standard)
    private OllamaResult generateWithToolsInternal(
            OllamaGenerateRequest request, OllamaGenerateStreamObserver streamObserver)
            throws OllamaException {
        ArrayList<OllamaChatMessage> msgs = new ArrayList<>();
        OllamaChatRequest chatRequest = new OllamaChatRequest();
        chatRequest.setModel(request.getModel());
        OllamaChatMessage ocm = new OllamaChatMessage();
        ocm.setRole(OllamaChatMessageRole.USER);
        ocm.setResponse(request.getPrompt());
        chatRequest.setMessages(msgs);
        msgs.add(ocm);

        // Merge request's tools and globally registered tools into a new list to avoid
        // mutating the
        // original request
        List<Tools.Tool> allTools = new ArrayList<>();
        if (request.getTools() != null) {
            allTools.addAll(request.getTools());
        }
        List<Tools.Tool> registeredTools = this.getRegisteredTools();
        if (registeredTools != null) {
            allTools.addAll(registeredTools);
        }

        OllamaChatTokenHandler hdlr = null;
        chatRequest.setUseTools(true);
        chatRequest.setTools(allTools);
        if (streamObserver != null) {
            chatRequest.setStream(true);
            if (streamObserver.getResponseStreamHandler() != null) {
                hdlr =
                        chatResponseModel ->
                                streamObserver
                                        .getResponseStreamHandler()
                                        .accept(chatResponseModel.getMessage().getResponse());
            }
        }
        OllamaChatResult res = chat(chatRequest, hdlr);
        return new OllamaResult(
                res.getResponseModel().getMessage().getResponse(),
                res.getResponseModel().getMessage().getThinking(),
                res.getResponseModel().getTotalDuration(),
                -1);
    }

    /**
     * Generates a response from a model asynchronously, returning a streamer for results.
     *
     * @param model the model name
     * @param prompt the prompt to send
     * @param raw whether to use raw mode
     * @param think whether to use "think" mode
     * @return an OllamaAsyncResultStreamer for streaming results
     * @throws OllamaException if the request fails
     */
    public OllamaAsyncResultStreamer generateAsync(
            String model, String prompt, boolean raw, ThinkMode think) throws OllamaException {
        long startTime = System.currentTimeMillis();
        String url = "/api/generate";
        int statusCode = -1;
        try {
            OllamaGenerateRequest ollamaRequestModel = new OllamaGenerateRequest(model, prompt);
            ollamaRequestModel.setRaw(raw);
            ollamaRequestModel.setThink(think);
            OllamaAsyncResultStreamer ollamaAsyncResultStreamer =
                    new OllamaAsyncResultStreamer(
                            getRequestBuilderDefault(new URI(this.host + url)),
                            ollamaRequestModel,
                            requestTimeoutSeconds);
            ollamaAsyncResultStreamer.start();
            statusCode = ollamaAsyncResultStreamer.getHttpStatusCode();
            return ollamaAsyncResultStreamer;
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, model, raw, think, true, null, null, startTime, statusCode, null);
        }
    }

    /**
     * Sends a chat request to a model using an {@link OllamaChatRequest} and sets up streaming
     * response. This can be constructed using an {@link OllamaChatRequest#builder()}.
     *
     * <p>Note: the OllamaChatRequestModel#getStream() property is not implemented.
     *
     * @param request request object to be sent to the server
     * @param tokenHandler callback handler to handle the last token from stream (caution: the
     *     previous tokens from stream will not be concatenated)
     * @return {@link OllamaChatResult}
     * @throws OllamaException if the response indicates an error status
     */
    public OllamaChatResult chat(OllamaChatRequest request, OllamaChatTokenHandler tokenHandler)
            throws OllamaException {
        try {
            OllamaChatEndpointCaller requestCaller =
                    new OllamaChatEndpointCaller(host, auth, requestTimeoutSeconds);
            OllamaChatResult result;

            // only add tools if tools flag is set
            if (request.isUseTools()) {
                // add all registered tools to request
                request.getTools().addAll(toolRegistry.getRegisteredTools());
            }

            if (tokenHandler != null) {
                request.setStream(true);
                result = requestCaller.call(request, tokenHandler);
            } else {
                result = requestCaller.callSync(request);
            }

            // check if toolCallIsWanted
            List<OllamaChatToolCalls> toolCalls =
                    result.getResponseModel().getMessage().getToolCalls();

            int toolCallTries = 0;
            while (toolCalls != null
                    && !toolCalls.isEmpty()
                    && toolCallTries < maxChatToolCallRetries) {
                for (OllamaChatToolCalls toolCall : toolCalls) {
                    String toolName = toolCall.getFunction().getName();
                    for (Tools.Tool t : request.getTools()) {
                        if (t.getToolSpec().getName().equals(toolName)) {
                            ToolFunction toolFunction = t.getToolFunction();
                            if (toolFunction == null) {
                                throw new ToolInvocationException(
                                        "Tool function not found: " + toolName);
                            }
                            LOG.debug(
                                    "Invoking tool {} with arguments: {}",
                                    toolCall.getFunction().getName(),
                                    toolCall.getFunction().getArguments());
                            Map<String, Object> arguments = toolCall.getFunction().getArguments();
                            Object res = toolFunction.apply(arguments);
                            String argumentKeys =
                                    arguments.keySet().stream()
                                            .map(Object::toString)
                                            .collect(Collectors.joining(", "));
                            request.getMessages()
                                    .add(
                                            new OllamaChatMessage(
                                                    OllamaChatMessageRole.TOOL,
                                                    "[TOOL_RESULTS] "
                                                            + toolName
                                                            + "("
                                                            + argumentKeys
                                                            + "): "
                                                            + res
                                                            + " [/TOOL_RESULTS]"));
                        }
                    }
                }
                if (tokenHandler != null) {
                    result = requestCaller.call(request, tokenHandler);
                } else {
                    result = requestCaller.callSync(request);
                }
                toolCalls = result.getResponseModel().getMessage().getToolCalls();
                toolCallTries++;
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", e);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        }
    }

    /**
     * Registers a single tool in the tool registry.
     *
     * @param tool the tool to register. Contains the tool's specification and function.
     */
    public void registerTool(Tools.Tool tool) {
        toolRegistry.addTool(tool);
        LOG.debug("Registered tool: {}", tool.getToolSpec().getName());
    }

    /**
     * Registers multiple tools in the tool registry.
     *
     * @param tools a list of {@link Tools.Tool} objects to register. Each tool contains its
     *     specification and function.
     */
    public void registerTools(List<Tools.Tool> tools) {
        toolRegistry.addTools(tools);
    }

    public List<Tools.Tool> getRegisteredTools() {
        return toolRegistry.getRegisteredTools();
    }

    /**
     * Deregisters all tools from the tool registry. This method removes all registered tools,
     * effectively clearing the registry.
     */
    public void deregisterTools() {
        toolRegistry.clear();
        LOG.debug("All tools have been deregistered.");
    }

    /**
     * Registers tools based on the annotations found on the methods of the caller's class and its
     * providers. This method scans the caller's class for the {@link OllamaToolService} annotation
     * and recursively registers annotated tools from all the providers specified in the annotation.
     *
     * @throws OllamaException if the caller's class is not annotated with {@link OllamaToolService}
     *     or if reflection-based instantiation or invocation fails
     */
    public void registerAnnotatedTools() throws OllamaException {
        try {
            Class<?> callerClass = null;
            try {
                callerClass =
                        Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            } catch (ClassNotFoundException e) {
                throw new OllamaException(e.getMessage(), e);
            }

            OllamaToolService ollamaToolServiceAnnotation =
                    callerClass.getDeclaredAnnotation(OllamaToolService.class);
            if (ollamaToolServiceAnnotation == null) {
                throw new IllegalStateException(
                        callerClass + " is not annotated as " + OllamaToolService.class);
            }

            Class<?>[] providers = ollamaToolServiceAnnotation.providers();
            for (Class<?> provider : providers) {
                registerAnnotatedTools(provider.getDeclaredConstructor().newInstance());
            }
        } catch (InstantiationException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new OllamaException(e.getMessage());
        }
    }

    /**
     * Registers tools based on the annotations found on the methods of the provided object. This
     * method scans the methods of the given object and registers tools using the {@link ToolSpec}
     * annotation and associated {@link ToolProperty} annotations. It constructs tool specifications
     * and stores them in a tool registry.
     *
     * @param object the object whose methods are to be inspected for annotated tools
     * @throws RuntimeException if any reflection-based instantiation or invocation fails
     */
    public void registerAnnotatedTools(Object object) {
        Class<?> objectClass = object.getClass();
        Method[] methods = objectClass.getMethods();
        for (Method m : methods) {
            ToolSpec toolSpec = m.getDeclaredAnnotation(ToolSpec.class);
            if (toolSpec == null) {
                continue;
            }
            String operationName = !toolSpec.name().isBlank() ? toolSpec.name() : m.getName();
            String operationDesc = !toolSpec.desc().isBlank() ? toolSpec.desc() : operationName;

            final Map<String, Tools.Property> params = new HashMap<String, Tools.Property>() {};
            LinkedHashMap<String, String> methodParams = new LinkedHashMap<>();
            for (Parameter parameter : m.getParameters()) {
                final ToolProperty toolPropertyAnn =
                        parameter.getDeclaredAnnotation(ToolProperty.class);
                String propType = parameter.getType().getTypeName();
                if (toolPropertyAnn == null) {
                    methodParams.put(parameter.getName(), null);
                    continue;
                }
                String propName =
                        !toolPropertyAnn.name().isBlank()
                                ? toolPropertyAnn.name()
                                : parameter.getName();
                methodParams.put(propName, propType);
                params.put(
                        propName,
                        Tools.Property.builder()
                                .type(propType)
                                .description(toolPropertyAnn.desc())
                                .required(toolPropertyAnn.required())
                                .build());
            }
            Tools.ToolSpec toolSpecification =
                    Tools.ToolSpec.builder()
                            .name(operationName)
                            .description(operationDesc)
                            .parameters(Tools.Parameters.of(params))
                            .build();
            ReflectionalToolFunction reflectionalToolFunction =
                    new ReflectionalToolFunction(object, m, methodParams);
            toolRegistry.addTool(
                    Tools.Tool.builder()
                            .toolFunction(reflectionalToolFunction)
                            .toolSpec(toolSpecification)
                            .build());
        }
    }

    /**
     * Adds a custom role.
     *
     * @param roleName the name of the custom role to be added
     * @return the newly created OllamaChatMessageRole
     */
    public OllamaChatMessageRole addCustomRole(String roleName) {
        return OllamaChatMessageRole.newCustomRole(roleName);
    }

    /**
     * Lists all available roles.
     *
     * @return a list of available OllamaChatMessageRole objects
     */
    public List<OllamaChatMessageRole> listRoles() {
        return OllamaChatMessageRole.getRoles();
    }

    /**
     * Retrieves a specific role by name.
     *
     * @param roleName the name of the role to retrieve
     * @return the OllamaChatMessageRole associated with the given name
     * @throws RoleNotFoundException if the role with the specified name does not exist
     */
    public OllamaChatMessageRole getRole(String roleName) throws RoleNotFoundException {
        return OllamaChatMessageRole.getRole(roleName);
    }

    public void loadMCPToolsFromJson(String mcpConfigJsonFilePath) throws IOException {
        // List<OllamaMCPTool> ollamaMCPTools = new java.util.ArrayList<>();

        String jsonContent =
                java.nio.file.Files.readString(java.nio.file.Paths.get(mcpConfigJsonFilePath));
        MCPToolsConfig config =
                McpJsonMapper.getDefault().readValue(jsonContent, MCPToolsConfig.class);

        if (config.mcpServers != null && !config.mcpServers.isEmpty()) {
            for (Map.Entry<String, MCPToolConfig> tool : config.mcpServers.entrySet()) {
                ServerParameters.Builder serverParamsBuilder =
                        ServerParameters.builder(tool.getValue().command);
                if (tool.getValue().args != null && !tool.getValue().args.isEmpty()) {
                    LOG.debug(
                            "Runnable MCP Tool command: \n\n\t{} {}\n\n",
                            tool.getValue().command,
                            String.join(" ", tool.getValue().args));
                    serverParamsBuilder.args(tool.getValue().args.toArray(new String[0]));
                }
                ServerParameters serverParameters = serverParamsBuilder.build();
                StdioClientTransport transport =
                        new StdioClientTransport(serverParameters, McpJsonMapper.getDefault());

                int mcpToolRequestTimeoutSeconds = 30;
                McpSyncClient client =
                        McpClient.sync(transport)
                                .requestTimeout(Duration.ofSeconds(mcpToolRequestTimeoutSeconds))
                                .build();
                client.initialize();

                ListToolsResult result = client.listTools();
                for (io.modelcontextprotocol.spec.McpSchema.Tool mcpTool : result.tools()) {
                    Tools.Tool mcpToolAsOllama4jTool =
                            createOllamaToolFromMCPTool(tool.getKey(), mcpTool, serverParameters);
                    toolRegistry.addTool(mcpToolAsOllama4jTool);
                }
                client.close();
            }
        }
    }

    /**
     * Calls a specific MCP (Model Context Protocol) tool registered with the Ollama instance.
     * This method locates the tool by its server name and tool name, then executes it with the provided arguments.
     *
     * @param mcpServerName The name of the MCP server where the tool is registered.
     * @param toolName The name of the tool to be called.
     * @param arguments A map of arguments to be passed to the tool.
     * @return The result of the tool call, encapsulated in a {@link CallToolResult} object.
     * @throws IllegalArgumentException If no MCP tool is found for the specified server name and tool name.
     */
    private CallToolResult callMCPTool(
            String mcpServerName, String toolName, Map<String, Object> arguments) {
        for (Tools.Tool tool : getRegisteredTools()) {
            if (tool.isMCPTool() && tool.getMcpServerName().equals(mcpServerName)) {
                if (tool.getToolSpec().getName().equals(toolName)) {
                    ServerParameters serverParameters = tool.getMcpServerParameters();
                    StdioClientTransport stdioTransport =
                            new StdioClientTransport(serverParameters, McpJsonMapper.getDefault());
                    LOG.info(
                            "Calling MCP Tool: '"
                                    + mcpServerName
                                    + "."
                                    + toolName
                                    + "' with arguments: "
                                    + arguments);
                    McpSyncClient client =
                            McpClient.sync(stdioTransport)
                                    .requestTimeout(Duration.ofSeconds(requestTimeoutSeconds))
                                    .build();
                    client.initialize();
                    CallToolRequest request = new CallToolRequest(toolName, arguments);
                    CallToolResult result = client.callTool(request);
                    client.close();
                    return result;
                }
            }
        }
        throw new IllegalArgumentException(
                "No MCP tool found for server name: "
                        + mcpServerName
                        + " and tool name: "
                        + toolName);
    }

    // technical private methods //

    /**
     * Utility method to encode a file into a Base64 encoded string.
     *
     * @param file the file to be encoded into Base64
     * @return a Base64 encoded string representing the contents of the file
     * @throws IOException if an I/O error occurs during reading the file
     */
    private static String encodeFileToBase64(File file) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }

    /**
     * Utility method to encode a byte array into a Base64 encoded string.
     *
     * @param bytes the byte array to be encoded into Base64
     * @return a Base64 encoded string representing the byte array
     */
    private static String encodeByteArrayToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Generates a request for the Ollama API and returns the result. This method synchronously calls
     * the Ollama API. If a stream handler is provided, the request will be streamed; otherwise, a
     * regular synchronous request will be made.
     *
     * @param ollamaRequestModel the request model containing necessary parameters for the Ollama API
     *     request
     * @param thinkingStreamHandler the stream handler for "thinking" tokens, or null if not used
     * @param responseStreamHandler the stream handler to process streaming responses, or null for
     *     non-streaming requests
     * @return the result of the Ollama API request
     * @throws OllamaException if the request fails due to an issue with the Ollama API
     */
    private OllamaResult generateSyncForOllamaRequestModel(
            OllamaGenerateRequest ollamaRequestModel,
            OllamaGenerateTokenHandler thinkingStreamHandler,
            OllamaGenerateTokenHandler responseStreamHandler)
            throws OllamaException {
        long startTime = System.currentTimeMillis();
        int statusCode = -1;
        Object out = null;
        try {
            OllamaGenerateEndpointCaller requestCaller =
                    new OllamaGenerateEndpointCaller(host, auth, requestTimeoutSeconds);
            OllamaResult result;
            if (responseStreamHandler != null) {
                ollamaRequestModel.setStream(true);
                result =
                        requestCaller.call(
                                ollamaRequestModel, thinkingStreamHandler, responseStreamHandler);
            } else {
                result = requestCaller.callSync(ollamaRequestModel);
            }
            statusCode = result.getHttpStatusCode();
            out = result;
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OllamaException("Thread was interrupted", e);
        } catch (Exception e) {
            throw new OllamaException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    OllamaGenerateEndpointCaller.endpoint,
                    ollamaRequestModel.getModel(),
                    ollamaRequestModel.isRaw(),
                    ollamaRequestModel.getThink(),
                    ollamaRequestModel.isStream(),
                    ollamaRequestModel.getOptions(),
                    ollamaRequestModel.getFormat(),
                    startTime,
                    statusCode,
                    out);
        }
    }

    /**
     * Get default request builder.
     *
     * @param uri URI to get a HttpRequest.Builder
     * @return HttpRequest.Builder
     */
    private HttpRequest.Builder getRequestBuilderDefault(URI uri) {
        HttpRequest.Builder requestBuilder =
                HttpRequest.newBuilder(uri)
                        .header(
                                Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                Constants.HttpConstants.APPLICATION_JSON)
                        .timeout(Duration.ofSeconds(requestTimeoutSeconds));
        if (isAuthSet()) {
            requestBuilder.header("Authorization", auth.getAuthHeaderValue());
        }
        return requestBuilder;
    }

    /**
     * Check if auth param is set.
     *
     * @return true when auth param is set
     */
    private boolean isAuthSet() {
        return auth != null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaMCPTool {
        private String mcpServerName;
        private List<MCPToolInfo> toolInfos;
        private StdioClientTransport transport;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MCPToolInfo {
        private String toolName;
        private String toolDescription;
    }

    public static class MCPToolConfig {
        @JsonProperty("command")
        public String command;

        @JsonProperty("args")
        public List<String> args;
    }

    public static class MCPToolsConfig {
        @JsonProperty("mcpServers")
        public Map<String, MCPToolConfig> mcpServers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaMCPToolMatchResponse {
        @JsonProperty("mcpServerName")
        public String mcpServerName;

        @JsonProperty("toolName")
        public String toolName;

        @JsonProperty("arguments")
        public Map<String, Object> arguments;
    }

    /**
     * Creates an Ollama-compatible {@link Tools.Tool} object from an MCP tool definition. This
     * involves parsing the MCP tool's input schema to define the tool's parameters and providing a
     * function to call the MCP tool when executed.
     *
     * @param mcpServerName The name of the MCP server associated with this tool.
     * @param mcpTool The MCP tool definition from which to create the Ollama tool.
     * @param serverParameters The server parameters associated with the MCP server.
     * @return A {@link Tools.Tool} object configured for Ollama, representing the MCP tool.
     */
    private Tools.Tool createOllamaToolFromMCPTool(
            String mcpServerName,
            io.modelcontextprotocol.spec.McpSchema.Tool mcpTool,
            ServerParameters serverParameters) {
        Map<String, Tools.Property> properties = new java.util.HashMap<>();
        java.util.List<String> requiredList = new java.util.ArrayList<>();

        if (mcpTool.inputSchema() != null && mcpTool.inputSchema().properties() != null) {
            // Prepare set for fast required lookup (since original is List<String>)
            java.util.Set<String> requiredSet = new java.util.HashSet<>();
            if (mcpTool.inputSchema().required() != null) {
                requiredSet.addAll(mcpTool.inputSchema().required());
            }
            for (Map.Entry<String, Object> entry : mcpTool.inputSchema().properties().entrySet()) {
                String propName = entry.getKey();
                Object propertyValue = entry.getValue();
                Map<String, Object> propertyMap = null;

                if (propertyValue instanceof Map) {
                    propertyMap = (Map<String, Object>) propertyValue;
                } else {
                    // Defensive fallback, unexpected schema
                    continue;
                }

                // Extract standard fields; fallback to empty/defaults
                String type =
                        propertyMap.get("type") != null ? propertyMap.get("type").toString() : null;

                String description = null;
                if (propertyMap.get("description") != null) {
                    description = propertyMap.get("description").toString();
                } else if (propertyMap.get("title") != null) {
                    // Use 'title' as fallback for description if 'description' is missing
                    description = propertyMap.get("title").toString();
                }

                // 'required' is determined from the parent 'required' list
                boolean propRequired = requiredSet.contains(propName);

                Tools.Property property =
                        Tools.Property.builder()
                                .type(type)
                                .description(description)
                                .required(propRequired)
                                .build();

                properties.put(propName, property);
                if (propRequired) {
                    requiredList.add(propName);
                }
            }
        }

        Tools.Parameters params = new Tools.Parameters();
        params.setProperties(properties);
        params.setRequired(requiredList);

        return Tools.Tool.builder()
                .toolSpec(
                        Tools.ToolSpec.builder()
                                .name(mcpTool.name())
                                .description(mcpTool.description())
                                .parameters(params)
                                .build())
                .toolFunction(
                        arguments -> {
                            CallToolResult result =
                                    this.callMCPTool(mcpServerName, mcpTool.name(), arguments);
                            return result.toString();
                        })
                .isMCPTool(true)
                .mcpServerName(mcpServerName)
                .mcpServerParameters(serverParameters)
                .build();
    }
}
