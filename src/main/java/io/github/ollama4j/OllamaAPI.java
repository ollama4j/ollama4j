/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.exceptions.ToolNotFoundException;
import io.github.ollama4j.metrics.MetricsRecorder;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.chat.OllamaChatTokenHandler;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import io.github.ollama4j.models.ps.ModelsProcessResponse;
import io.github.ollama4j.models.request.*;
import io.github.ollama4j.models.response.*;
import io.github.ollama4j.tools.*;
import io.github.ollama4j.tools.annotations.OllamaToolService;
import io.github.ollama4j.tools.annotations.ToolProperty;
import io.github.ollama4j.tools.annotations.ToolSpec;
import io.github.ollama4j.utils.Constants;
import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.Utils;
import java.io.*;
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
import java.util.stream.Collectors;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base Ollama API class for interacting with the Ollama server.
 *
 * <p>This class provides methods for model management, chat, embeddings, tool registration, and
 * more.
 */
@SuppressWarnings({"DuplicatedCode", "resource", "SpellCheckingInspection"})
public class OllamaAPI {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaAPI.class);

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
     * <p>If set to 0, no retries will be performed. If greater than 0, the API will retry pulling
     * the model up to the specified number of times in case of failure.
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
    public OllamaAPI() {
        this.host = "http://localhost:11434";
        //        initializeMetrics();
    }

    /**
     * Instantiates the Ollama API with a specified Ollama host address.
     *
     * @param host the host address of the Ollama server
     */
    public OllamaAPI(String host) {
        if (host.endsWith("/")) {
            this.host = host.substring(0, host.length() - 1);
        } else {
            this.host = host;
        }
        LOG.info("Ollama4j client initialized. Connected to Ollama server at: {}", this.host);
        //        initializeMetrics();
    }

    /**
     * Set basic authentication for accessing an Ollama server that's behind a
     * reverse-proxy/gateway.
     *
     * @param username the username
     * @param password the password
     */
    public void setBasicAuth(String username, String password) {
        this.auth = new BasicAuth(username, password);
    }

    /**
     * Set Bearer authentication for accessing an Ollama server that's behind a
     * reverse-proxy/gateway.
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
     * @throws OllamaBaseException if the ping fails
     */
    public boolean ping() throws OllamaBaseException {
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
        } catch (Exception e) {
            throw new OllamaBaseException("Ping failed", e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Provides a list of running models and details about each model currently loaded into memory.
     *
     * @return ModelsProcessResponse containing details about the running models
     * @throws OllamaBaseException if the response indicates an error status
     */
    public ModelsProcessResponse ps() throws OllamaBaseException {
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
                throw new OllamaBaseException(e.getMessage(), e);
            }
            HttpResponse<String> response = null;
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseString = response.body();
            if (statusCode == 200) {
                return Utils.getObjectMapper()
                        .readValue(responseString, ModelsProcessResponse.class);
            } else {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        } catch (Exception e) {
            throw new OllamaBaseException("ps failed", e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Lists available models from the Ollama server.
     *
     * @return a list of models available on the server
     * @throws OllamaBaseException if the response indicates an error status
     */
    public List<Model> listModels() throws OllamaBaseException {
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
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
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
     * @throws OllamaBaseException if the pull fails
     */
    private void doPullModel(String modelName) throws OllamaBaseException {
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
                throw new OllamaBaseException("Model pull failed or returned invalid status.");
            }
            if (statusCode != 200) {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Processes a single ModelPullResponse, handling errors and logging status. Returns true if the
     * response indicates a successful pull.
     *
     * @param modelPullResponse the response from the model pull
     * @param modelName the name of the model
     * @return true if the pull was successful, false otherwise
     * @throws OllamaBaseException if the response contains an error
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean processModelPullResponse(ModelPullResponse modelPullResponse, String modelName)
            throws OllamaBaseException {
        if (modelPullResponse == null) {
            LOG.error("Received null response for model pull.");
            return false;
        }
        String error = modelPullResponse.getError();
        if (error != null && !error.trim().isEmpty()) {
            throw new OllamaBaseException("Model pull failed: " + error);
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
     * @throws OllamaBaseException if the request fails
     */
    public String getVersion() throws OllamaBaseException {
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
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Pulls a model using the specified Ollama library model tag. The model is identified by a name
     * and a tag, which are combined into a single identifier in the format "name:tag" to pull the
     * corresponding model.
     *
     * @param modelName the name/tag of the model to be pulled. Ex: llama3:latest
     * @throws OllamaBaseException if the response indicates an error status
     */
    public void pullModel(String modelName) throws OllamaBaseException {
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
                } catch (OllamaBaseException e) {
                    handlePullRetry(
                            modelName,
                            numberOfRetries,
                            numberOfRetriesForModelPull,
                            baseDelayMillis);
                    numberOfRetries++;
                }
            }
            throw new OllamaBaseException(
                    "Failed to pull model "
                            + modelName
                            + " after "
                            + numberOfRetriesForModelPull
                            + " retries");
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        }
    }

    /**
     * Gets model details from the Ollama server.
     *
     * @param modelName the model name
     * @return the model details
     * @throws OllamaBaseException if the response indicates an error status
     */
    public ModelDetail getModelDetails(String modelName) throws OllamaBaseException {
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
                throw new OllamaBaseException(statusCode + " - " + responseBody);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Creates a custom model. Read more about custom model creation <a
     * href="https://github.com/ollama/ollama/blob/main/docs/api.md#create-a-model">here</a>.
     *
     * @param customModelRequest custom model spec
     * @throws OllamaBaseException if the response indicates an error status
     */
    public void createModel(CustomModelRequest customModelRequest) throws OllamaBaseException {
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
                throw new OllamaBaseException(statusCode + " - " + errorBody);
            }
            try (BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                String line;
                StringBuffer lines = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    ModelPullResponse res =
                            Utils.getObjectMapper().readValue(line, ModelPullResponse.class);
                    lines.append(line);
                    LOG.debug(res.getStatus());
                    if (res.getError() != null) {
                        out = res.getError();
                        throw new OllamaBaseException(res.getError());
                    }
                }
                out = lines;
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Deletes a model from the Ollama server.
     *
     * @param modelName the name of the model to be deleted
     * @param ignoreIfNotPresent ignore errors if the specified model is not present on the Ollama
     *     server
     * @throws OllamaBaseException if the response indicates an error status
     */
    public void deleteModel(String modelName, boolean ignoreIfNotPresent)
            throws OllamaBaseException {
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
                throw new OllamaBaseException(statusCode + " - " + responseBody);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(statusCode + " - " + out, e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Unloads a model from memory.
     *
     * <p>If an empty prompt is provided and the keep_alive parameter is set to 0, a model will be
     * unloaded from memory.
     *
     * @param modelName the name of the model to unload
     * @throws OllamaBaseException if the response indicates an error status
     */
    public void unloadModel(String modelName) throws OllamaBaseException {
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
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseBody = response.body();
            if (statusCode == 404
                    && responseBody.contains("model")
                    && responseBody.contains("not found")) {
                return;
            }
            if (statusCode != 200) {
                throw new OllamaBaseException(statusCode + " - " + responseBody);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(statusCode + " - " + out, e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Generate embeddings using a {@link OllamaEmbedRequestModel}.
     *
     * @param modelRequest request for '/api/embed' endpoint
     * @return embeddings
     * @throws OllamaBaseException if the response indicates an error status
     */
    public OllamaEmbedResponseModel embed(OllamaEmbedRequestModel modelRequest)
            throws OllamaBaseException {
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
                return Utils.getObjectMapper()
                        .readValue(responseBody, OllamaEmbedResponseModel.class);
            } else {
                throw new OllamaBaseException(statusCode + " - " + responseBody);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Generates a response from a model using the specified parameters and stream observer.
     *
     * @param model the model name
     * @param prompt the prompt to send
     * @param raw whether to return the raw response
     * @param think whether to stream "thinking" tokens
     * @param options additional options
     * @param streamObserver the stream observer for handling streamed responses
     * @return the OllamaResult containing the response
     * @throws OllamaBaseException if the request fails
     */
    public OllamaResult generate(
            String model,
            String prompt,
            boolean raw,
            boolean think,
            Options options,
            OllamaGenerateStreamObserver streamObserver)
            throws OllamaBaseException {
        try {
            // Create the OllamaGenerateRequest and configure common properties
            OllamaGenerateRequest ollamaRequestModel = new OllamaGenerateRequest(model, prompt);
            ollamaRequestModel.setRaw(raw);
            ollamaRequestModel.setThink(think);
            ollamaRequestModel.setOptions(options.getOptionsMap());
            ollamaRequestModel.setKeepAlive("0m");

            // Based on 'think' flag, choose the appropriate stream handler(s)
            if (think) {
                // Call with thinking
                return generateSyncForOllamaRequestModel(
                        ollamaRequestModel,
                        streamObserver.getThinkingStreamHandler(),
                        streamObserver.getResponseStreamHandler());
            } else {
                // Call without thinking
                return generateSyncForOllamaRequestModel(
                        ollamaRequestModel, null, streamObserver.getResponseStreamHandler());
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        }
    }

    /**
     * Generates structured output from the specified AI model and prompt.
     *
     * <p>Note: When formatting is specified, the 'think' parameter is not allowed.
     *
     * @param model The name or identifier of the AI model to use for generating the response.
     * @param prompt The input text or prompt to provide to the AI model.
     * @param format A map containing the format specification for the structured output.
     * @return An instance of {@link OllamaResult} containing the structured response.
     * @throws OllamaBaseException if the response indicates an error status.
     */
    @SuppressWarnings("LoggingSimilarMessage")
    public OllamaResult generateWithFormat(String model, String prompt, Map<String, Object> format)
            throws OllamaBaseException {
        long startTime = System.currentTimeMillis();
        String url = "/api/generate";
        int statusCode = -1;
        Object out = null;
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            requestBody.put("format", format);

            String jsonData = Utils.getObjectMapper().writeValueAsString(requestBody);
            HttpClient httpClient = HttpClient.newHttpClient();

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

            try {
                String prettyJson =
                        Utils.toJSON(Utils.getObjectMapper().readValue(jsonData, Object.class));
                LOG.debug("Asking model:\n{}", prettyJson);
            } catch (Exception e) {
                LOG.debug("Asking model: {}", jsonData);
            }

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            String responseBody = response.body();
            if (statusCode == 200) {
                OllamaStructuredResult structuredResult =
                        Utils.getObjectMapper()
                                .readValue(responseBody, OllamaStructuredResult.class);
                OllamaResult ollamaResult =
                        new OllamaResult(
                                structuredResult.getResponse(),
                                structuredResult.getThinking(),
                                structuredResult.getResponseTime(),
                                statusCode);
                ollamaResult.setModel(structuredResult.getModel());
                ollamaResult.setCreatedAt(structuredResult.getCreatedAt());
                ollamaResult.setDone(structuredResult.isDone());
                ollamaResult.setDoneReason(structuredResult.getDoneReason());
                ollamaResult.setContext(structuredResult.getContext());
                ollamaResult.setTotalDuration(structuredResult.getTotalDuration());
                ollamaResult.setLoadDuration(structuredResult.getLoadDuration());
                ollamaResult.setPromptEvalCount(structuredResult.getPromptEvalCount());
                ollamaResult.setPromptEvalDuration(structuredResult.getPromptEvalDuration());
                ollamaResult.setEvalCount(structuredResult.getEvalCount());
                ollamaResult.setEvalDuration(structuredResult.getEvalDuration());
                LOG.debug("Model response:\n{}", ollamaResult);

                return ollamaResult;
            } else {
                String errorResponse = Utils.toJSON(responseBody);
                LOG.debug("Model response:\n{}", errorResponse);
                throw new OllamaBaseException(statusCode + " - " + responseBody);
            }
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, "", false, false, false, null, null, startTime, statusCode, out);
        }
    }

    /**
     * Generates a response using the specified AI model and prompt, then automatically detects and
     * invokes any tool calls present in the model's output.
     *
     * <p>This method operates in blocking mode. It first augments the prompt with all registered
     * tool specifications (unless the prompt already begins with {@code [AVAILABLE_TOOLS]}), sends
     * the prompt to the model, and parses the model's response for tool call instructions. If tool
     * calls are found, each is invoked using the registered tool implementations, and their results
     * are collected.
     *
     * <p>Typical usage:
     *
     * <pre>{@code
     * OllamaToolsResult result = ollamaAPI.generateWithTools(
     *     "my-model",
     *     "What is the weather in Bengaluru?",
     *     Options.defaultOptions(),
     *     null // or a custom OllamaStreamHandler for streaming
     * );
     * String modelResponse = result.getModelResult().getResponse();
     * Map<ToolFunctionCallSpec, Object> toolResults = result.getToolResults();
     * }</pre>
     *
     * @param model the name or identifier of the AI model to use for generating the response
     * @param prompt the input text or prompt to provide to the AI model
     * @param options additional options or configurations to use when generating the response
     * @param streamHandler handler for streaming responses; if {@code null}, streaming is disabled
     * @return an {@link OllamaToolsResult} containing the model's response and the results of any
     *     invoked tools. If the model does not request any tool calls, the tool results map will be
     *     empty.
     * @throws OllamaBaseException if the Ollama API returns an error status
     */
    public OllamaToolsResult generateWithTools(
            String model, String prompt, Options options, OllamaGenerateTokenHandler streamHandler)
            throws OllamaBaseException {
        try {
            boolean raw = true;
            OllamaToolsResult toolResult = new OllamaToolsResult();
            Map<ToolFunctionCallSpec, Object> toolResults = new HashMap<>();

            if (!prompt.startsWith("[AVAILABLE_TOOLS]")) {
                final Tools.PromptBuilder promptBuilder = new Tools.PromptBuilder();
                for (Tools.ToolSpecification spec : toolRegistry.getRegisteredSpecs()) {
                    promptBuilder.withToolSpecification(spec);
                }
                promptBuilder.withPrompt(prompt);
                prompt = promptBuilder.build();
            }

            OllamaResult result =
                    generate(
                            model,
                            prompt,
                            raw,
                            false,
                            options,
                            new OllamaGenerateStreamObserver(null, streamHandler));
            toolResult.setModelResult(result);

            String toolsResponse = result.getResponse();
            if (toolsResponse.contains("[TOOL_CALLS]")) {
                toolsResponse = toolsResponse.replace("[TOOL_CALLS]", "");
            }

            List<ToolFunctionCallSpec> toolFunctionCallSpecs = new ArrayList<>();
            ObjectMapper objectMapper = Utils.getObjectMapper();

            if (!toolsResponse.isEmpty()) {
                try {
                    // Try to parse the string to see if it's a valid JSON
                    objectMapper.readTree(toolsResponse);
                } catch (JsonParseException e) {
                    LOG.warn(
                            "Response from model does not contain any tool calls. Returning the"
                                    + " response as is.");
                    return toolResult;
                }
                toolFunctionCallSpecs =
                        objectMapper.readValue(
                                toolsResponse,
                                objectMapper
                                        .getTypeFactory()
                                        .constructCollectionType(
                                                List.class, ToolFunctionCallSpec.class));
            }
            for (ToolFunctionCallSpec toolFunctionCallSpec : toolFunctionCallSpecs) {
                toolResults.put(toolFunctionCallSpec, invokeTool(toolFunctionCallSpec));
            }
            toolResult.setToolResults(toolResults);
            return toolResult;
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        }
    }

    /**
     * Asynchronously generates a response for a prompt using a model running on the Ollama server.
     *
     * <p>This method returns an {@link OllamaAsyncResultStreamer} handle that can be used to poll
     * for status and retrieve streamed "thinking" and response tokens from the model. The call is
     * non-blocking.
     *
     * <p>Example usage:
     *
     * <pre>{@code
     * OllamaAsyncResultStreamer resultStreamer = ollamaAPI.generate("gpt-oss:20b", "Who are you", false, true);
     * int pollIntervalMilliseconds = 1000;
     * while (true) {
     *     String thinkingTokens = resultStreamer.getThinkingResponseStream().poll();
     *     String responseTokens = resultStreamer.getResponseStream().poll();
     *     System.out.print(thinkingTokens != null ? thinkingTokens.toUpperCase() : "");
     *     System.out.print(responseTokens != null ? responseTokens.toLowerCase() : "");
     *     Thread.sleep(pollIntervalMilliseconds);
     *     if (!resultStreamer.isAlive())
     *         break;
     * }
     * System.out.println("Complete thinking response: " + resultStreamer.getCompleteThinkingResponse());
     * System.out.println("Complete response: " + resultStreamer.getCompleteResponse());
     * }</pre>
     *
     * @param model the Ollama model to use for generating the response
     * @param prompt the prompt or question text to send to the model
     * @param raw if {@code true}, returns the raw response from the model
     * @param think if {@code true}, streams "thinking" tokens as well as response tokens
     * @return an {@link OllamaAsyncResultStreamer} handle for polling and retrieving streamed
     *     results
     * @throws OllamaBaseException if the request fails
     */
    public OllamaAsyncResultStreamer generate(
            String model, String prompt, boolean raw, boolean think) throws OllamaBaseException {
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
            throw new OllamaBaseException(e.getMessage(), e);
        } finally {
            MetricsRecorder.record(
                    url, model, raw, think, true, null, null, startTime, statusCode, null);
        }
    }

    /**
     * Generates a response from a model running on the Ollama server using one or more images as
     * input.
     *
     * <p>This method allows you to provide images (as {@link File}, {@code byte[]}, or image URL
     * {@link String}) along with a prompt to the specified model. The images are automatically
     * encoded as base64 before being sent. Additional model options can be specified via the {@link
     * Options} parameter.
     *
     * <p>If a {@code streamHandler} is provided, the response will be streamed and the handler will
     * be called for each streamed response chunk. If {@code streamHandler} is {@code null},
     * streaming is disabled and the full response is returned synchronously.
     *
     * @param model the name of the Ollama model to use for generating the response
     * @param prompt the prompt or question text to send to the model
     * @param images a list of images to use for the question; each element must be a {@link File},
     *     {@code byte[]}, or a URL {@link String}
     * @param options the {@link Options} object containing model parameters; see <a
     *     href="https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">Ollama
     *     model options documentation</a>
     * @param format a map specifying the output format, or null for default
     * @param streamHandler an optional callback that is invoked for each streamed response chunk;
     *     if {@code null}, disables streaming and returns the full response synchronously
     * @return an {@link OllamaResult} containing the response text and time taken for the response
     * @throws OllamaBaseException if the response indicates an error status or an invalid image
     *     type is provided
     */
    public OllamaResult generateWithImages(
            String model,
            String prompt,
            List<Object> images,
            Options options,
            Map<String, Object> format,
            OllamaGenerateTokenHandler streamHandler)
            throws OllamaBaseException {
        try {
            List<String> encodedImages = new ArrayList<>();
            for (Object image : images) {
                if (image instanceof File) {
                    LOG.debug("Using image file: {}", ((File) image).getAbsolutePath());
                    encodedImages.add(encodeFileToBase64((File) image));
                } else if (image instanceof byte[]) {
                    LOG.debug("Using image bytes: {} bytes", ((byte[]) image).length);
                    encodedImages.add(encodeByteArrayToBase64((byte[]) image));
                } else if (image instanceof String) {
                    LOG.debug("Using image URL: {}", image);
                    encodedImages.add(
                            encodeByteArrayToBase64(
                                    Utils.loadImageBytesFromUrl(
                                            (String) image,
                                            imageURLConnectTimeoutSeconds,
                                            imageURLReadTimeoutSeconds)));
                } else {
                    throw new OllamaBaseException(
                            "Unsupported image type. Please provide a File, byte[], or a URL"
                                    + " String.");
                }
            }
            OllamaGenerateRequest ollamaRequestModel =
                    new OllamaGenerateRequest(model, prompt, encodedImages);
            if (format != null) {
                ollamaRequestModel.setFormat(format);
            }
            ollamaRequestModel.setOptions(options.getOptionsMap());
            OllamaResult result =
                    generateSyncForOllamaRequestModel(ollamaRequestModel, null, streamHandler);
            return result;
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        }
    }

    /**
     * Ask a question to a model using an {@link OllamaChatRequest} and set up streaming response.
     * This can be constructed using an {@link OllamaChatRequestBuilder}.
     *
     * <p>Hint: the OllamaChatRequestModel#getStream() property is not implemented.
     *
     * @param request request object to be sent to the server
     * @param tokenHandler callback handler to handle the last token from stream (caution: the
     *     previous tokens from stream will not be concatenated)
     * @return {@link OllamaChatResult}
     * @throws OllamaBaseException if the response indicates an error status
     */
    public OllamaChatResult chat(OllamaChatRequest request, OllamaChatTokenHandler tokenHandler)
            throws OllamaBaseException {
        try {
            OllamaChatEndpointCaller requestCaller =
                    new OllamaChatEndpointCaller(host, auth, requestTimeoutSeconds);
            OllamaChatResult result;

            // only add tools if tools flag is set
            if (request.isUseTools()) {
                // add all registered tools to request
                request.setTools(
                        toolRegistry.getRegisteredSpecs().stream()
                                .map(Tools.ToolSpecification::getToolPrompt)
                                .collect(Collectors.toList()));
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
                    ToolFunction toolFunction = toolRegistry.getToolFunction(toolName);
                    if (toolFunction == null) {
                        throw new ToolInvocationException("Tool function not found: " + toolName);
                    }
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

                if (tokenHandler != null) {
                    result = requestCaller.call(request, tokenHandler);
                } else {
                    result = requestCaller.callSync(request);
                }
                toolCalls = result.getResponseModel().getMessage().getToolCalls();
                toolCallTries++;
            }
            return result;
        } catch (Exception e) {
            throw new OllamaBaseException(e.getMessage(), e);
        }
    }

    /**
     * Registers a single tool in the tool registry using the provided tool specification.
     *
     * @param toolSpecification the specification of the tool to register. It contains the tool's
     *     function name and other relevant information.
     */
    public void registerTool(Tools.ToolSpecification toolSpecification) {
        toolRegistry.addTool(toolSpecification.getFunctionName(), toolSpecification);
        LOG.debug("Registered tool: {}", toolSpecification.getFunctionName());
    }

    /**
     * Registers multiple tools in the tool registry using a list of tool specifications. Iterates
     * over the list and adds each tool specification to the registry.
     *
     * @param toolSpecifications a list of tool specifications to register. Each specification
     *     contains information about a tool, such as its function name.
     */
    public void registerTools(List<Tools.ToolSpecification> toolSpecifications) {
        for (Tools.ToolSpecification toolSpecification : toolSpecifications) {
            toolRegistry.addTool(toolSpecification.getFunctionName(), toolSpecification);
        }
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
     * @throws OllamaBaseException if the caller's class is not annotated with {@link
     *     OllamaToolService} or if reflection-based instantiation or invocation fails
     */
    public void registerAnnotatedTools() throws OllamaBaseException {
        try {
            Class<?> callerClass = null;
            try {
                callerClass =
                        Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            } catch (ClassNotFoundException e) {
                throw new OllamaBaseException(e.getMessage(), e);
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
            throw new OllamaBaseException(e.getMessage());
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

            final Tools.PropsBuilder propsBuilder = new Tools.PropsBuilder();
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
                propsBuilder.withProperty(
                        propName,
                        Tools.PromptFuncDefinition.Property.builder()
                                .type(propType)
                                .description(toolPropertyAnn.desc())
                                .required(toolPropertyAnn.required())
                                .build());
            }
            final Map<String, Tools.PromptFuncDefinition.Property> params = propsBuilder.build();
            List<String> reqProps =
                    params.entrySet().stream()
                            .filter(e -> e.getValue().isRequired())
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

            Tools.ToolSpecification toolSpecification =
                    Tools.ToolSpecification.builder()
                            .functionName(operationName)
                            .functionDescription(operationDesc)
                            .toolPrompt(
                                    Tools.PromptFuncDefinition.builder()
                                            .type("function")
                                            .function(
                                                    Tools.PromptFuncDefinition.PromptFuncSpec
                                                            .builder()
                                                            .name(operationName)
                                                            .description(operationDesc)
                                                            .parameters(
                                                                    Tools.PromptFuncDefinition
                                                                            .Parameters.builder()
                                                                            .type("object")
                                                                            .properties(params)
                                                                            .required(reqProps)
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();

            ReflectionalToolFunction reflectionalToolFunction =
                    new ReflectionalToolFunction(object, m, methodParams);
            toolSpecification.setToolFunction(reflectionalToolFunction);
            toolRegistry.addTool(toolSpecification.getFunctionName(), toolSpecification);
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
     * Generates a request for the Ollama API and returns the result. This method synchronously
     * calls the Ollama API. If a stream handler is provided, the request will be streamed;
     * otherwise, a regular synchronous request will be made.
     *
     * @param ollamaRequestModel the request model containing necessary parameters for the Ollama
     *     API request
     * @param thinkingStreamHandler the stream handler for "thinking" tokens, or null if not used
     * @param responseStreamHandler the stream handler to process streaming responses, or null for
     *     non-streaming requests
     * @return the result of the Ollama API request
     * @throws OllamaBaseException if the request fails due to an issue with the Ollama API
     * @throws IOException if an I/O error occurs during the request process
     * @throws InterruptedException if the thread is interrupted during the request
     */
    private OllamaResult generateSyncForOllamaRequestModel(
            OllamaGenerateRequest ollamaRequestModel,
            OllamaGenerateTokenHandler thinkingStreamHandler,
            OllamaGenerateTokenHandler responseStreamHandler)
            throws OllamaBaseException, IOException, InterruptedException {
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
        return result;
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

    /**
     * Invokes a registered tool function by name and arguments.
     *
     * @param toolFunctionCallSpec the tool function call specification
     * @return the result of the tool function
     * @throws ToolInvocationException if the tool is not found or invocation fails
     */
    private Object invokeTool(ToolFunctionCallSpec toolFunctionCallSpec)
            throws ToolInvocationException {
        try {
            String methodName = toolFunctionCallSpec.getName();
            Map<String, Object> arguments = toolFunctionCallSpec.getArguments();
            ToolFunction function = toolRegistry.getToolFunction(methodName);
            LOG.debug("Invoking function {} with arguments {}", methodName, arguments);
            if (function == null) {
                throw new ToolNotFoundException(
                        "No such tool: "
                                + methodName
                                + ". Please register the tool before invoking it.");
            }
            return function.apply(arguments);
        } catch (Exception e) {
            throw new ToolInvocationException(
                    "Failed to invoke tool: " + toolFunctionCallSpec.getName(), e);
        }
    }

    //    /**
    //     * Initialize metrics collection if enabled.
    //     */
    //    private void initializeMetrics() {
    //        if (metricsEnabled) {
    //            OllamaMetricsService.initialize();
    //            LOG.info("Prometheus metrics collection enabled for Ollama4j client");
    //        }
    //    }
    //
    //    /**
    //     * Record metrics for an API request.
    //     *
    //     * @param endpoint the API endpoint
    //     * @param method the HTTP method
    //     * @param durationSeconds the request duration
    //     * @param success whether the request was successful
    //     * @param errorType the error type if the request failed
    //     */
    //    private void recordMetrics(
    //            String endpoint,
    //            String method,
    //            double durationSeconds,
    //            boolean success,
    //            String errorType) {
    //        if (!metricsEnabled) {
    //            return;
    //        }
    //
    //        if (success) {
    //            OllamaMetricsService.recordRequest(endpoint, method, durationSeconds);
    //        } else {
    //            OllamaMetricsService.recordRequestError(endpoint, method, durationSeconds,
    // errorType);
    //        }
    //    }

    //    /**
    //     * Record metrics for model usage.
    //     *
    //     * @param modelName the model name
    //     * @param operation the operation performed
    //     * @param durationSeconds the operation duration
    //     */
    //    private void recordModelMetrics(String modelName, String operation, double
    // durationSeconds) {
    //        if (!metricsEnabled) {
    //            return;
    //        }
    //
    //        OllamaMetricsService.recordModelUsage(modelName, operation, durationSeconds);
    //    }

    //    /**
    //     * Record token generation metrics.
    //     *
    //     * @param modelName the model name
    //     * @param tokenCount the number of tokens generated
    //     */
    //    private void recordTokenMetrics(String modelName, int tokenCount) {
    //        if (!metricsEnabled) {
    //            return;
    //        }
    //
    //        OllamaMetricsService.recordTokensGenerated(modelName, tokenCount);
    //    }

    //    /**
    //     * Execute a method with metrics collection.
    //     *
    //     * @param endpoint the API endpoint
    //     * @param method the HTTP method
    //     * @param operation the operation name for model metrics
    //     * @param modelName the model name (can be null)
    //     * @param runnable the operation to execute
    //     * @return the result of the operation
    //     * @throws Exception if the operation fails
    //     */
    //    private <T> T executeWithMetrics(
    //            String endpoint,
    //            String method,
    //            String operation,
    //            String modelName,
    //            MetricsOperation<T> runnable)
    //            throws Exception {
    //        long startTime = System.nanoTime();
    //        boolean success = false;
    //        String errorType = null;
    //
    //        try {
    //            OllamaMetricsService.incrementActiveConnections();
    //            T result = runnable.execute();
    //            success = true;
    //            return result;
    //        } catch (OllamaBaseException e) {
    //            errorType = "ollama_error";
    //            throw e;
    //        } catch (IOException e) {
    //            errorType = "io_error";
    //            throw e;
    //        } catch (InterruptedException e) {
    //            errorType = "interrupted";
    //            throw e;
    //        } catch (Exception e) {
    //            errorType = "unknown_error";
    //            throw e;
    //        } finally {
    //            OllamaMetricsService.decrementActiveConnections();
    //            double durationSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
    //
    //            recordMetrics(endpoint, method, durationSeconds, success, errorType);
    //
    //            if (modelName != null) {
    //                recordModelMetrics(modelName, operation, durationSeconds);
    //            }
    //        }
    //    }

    //    /**
    //     * Functional interface for operations that need metrics collection.
    //     */
    //    @FunctionalInterface
    //    private interface MetricsOperation<T> {
    //        T execute() throws Exception;
    //    }
}
