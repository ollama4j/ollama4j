package io.github.ollama4j.models.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.response.OllamaErrorResponse;
import io.github.ollama4j.models.chat.OllamaChatResponseModel;
import io.github.ollama4j.models.chat.OllamaChatStreamObserver;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Specialization class for requests
 */
public class OllamaChatEndpointCaller extends OllamaEndpointCaller {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaChatEndpointCaller.class);

    private OllamaChatStreamObserver streamObserver;

    public OllamaChatEndpointCaller(String host, BasicAuth basicAuth, long requestTimeoutSeconds, boolean verbose) {
        super(host, basicAuth, requestTimeoutSeconds, verbose);
    }

    @Override
    protected String getEndpointSuffix() {
        return "/api/chat";
    }

    /**
     * Parses streamed Response line from ollama chat.
     * Using {@link com.fasterxml.jackson.databind.ObjectMapper#readValue(String, TypeReference)} should throw
     * {@link IllegalArgumentException} in case of null line or {@link com.fasterxml.jackson.core.JsonParseException}
     * in case the JSON Object cannot be parsed to a {@link OllamaChatResponseModel}. Thus, the ResponseModel should
     * never be null.
     *
     * @param line streamed line of ollama stream response
     * @param responseBuffer Stringbuffer to add latest response message part to
     * @return TRUE, if ollama-Response has 'done' state
     */
    @Override
    protected boolean parseResponseAndAddToBuffer(String line, StringBuilder responseBuffer) {
        try {
            OllamaChatResponseModel ollamaResponseModel = Utils.getObjectMapper().readValue(line, OllamaChatResponseModel.class);
            // it seems that under heavy load ollama responds with an empty chat message part in the streamed response
            // thus, we null check the message and hope that the next streamed response has some message content again
            OllamaChatMessage message = ollamaResponseModel.getMessage();
            if(message != null) {
                responseBuffer.append(message.getContent());
                if (streamObserver != null) {
                    streamObserver.notify(ollamaResponseModel);
                }
            }
            return ollamaResponseModel.isDone();
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing the Ollama chat response!", e);
            return true;
        }
    }

    public OllamaChatResult call(OllamaChatRequest body, OllamaStreamHandler streamHandler)
            throws OllamaBaseException, IOException, InterruptedException {
        streamObserver = new OllamaChatStreamObserver(streamHandler);
        return callSync(body);
    }

    public OllamaChatResult callSync(OllamaChatRequest body) throws OllamaBaseException, IOException, InterruptedException {
        // Create Request
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(getHost() + getEndpointSuffix());
        HttpRequest.Builder requestBuilder =
                getRequestBuilderDefault(uri)
                        .POST(
                                body.getBodyPublisher());
        HttpRequest request = requestBuilder.build();
        if (isVerbose()) LOG.info("Asking model: " + body.toString());
        HttpResponse<InputStream> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int statusCode = response.statusCode();
        InputStream responseBodyStream = response.body();
        StringBuilder responseBuffer = new StringBuilder();
        OllamaChatResponseModel ollamaChatResponseModel = null;
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (statusCode == 404) {
                    LOG.warn("Status code: 404 (Not Found)");
                    OllamaErrorResponse ollamaResponseModel =
                            Utils.getObjectMapper().readValue(line, OllamaErrorResponse.class);
                    responseBuffer.append(ollamaResponseModel.getError());
                } else if (statusCode == 401) {
                    LOG.warn("Status code: 401 (Unauthorized)");
                    OllamaErrorResponse ollamaResponseModel =
                            Utils.getObjectMapper()
                                    .readValue("{\"error\":\"Unauthorized\"}", OllamaErrorResponse.class);
                    responseBuffer.append(ollamaResponseModel.getError());
                } else if (statusCode == 400) {
                    LOG.warn("Status code: 400 (Bad Request)");
                    OllamaErrorResponse ollamaResponseModel = Utils.getObjectMapper().readValue(line,
                            OllamaErrorResponse.class);
                    responseBuffer.append(ollamaResponseModel.getError());
                } else {
                    boolean finished = parseResponseAndAddToBuffer(line, responseBuffer);
                        ollamaChatResponseModel = Utils.getObjectMapper().readValue(line, OllamaChatResponseModel.class);
                    if (finished && body.stream) {
                        ollamaChatResponseModel.getMessage().setContent(responseBuffer.toString());
                        break;
                    }
                }
            }
        }
        if (statusCode != 200) {
            LOG.error("Status code " + statusCode);
            throw new OllamaBaseException(responseBuffer.toString());
        } else {
            OllamaChatResult ollamaResult =
                    new OllamaChatResult(ollamaChatResponseModel,body.getMessages());
            if (isVerbose()) LOG.info("Model response: " + ollamaResult);
            return ollamaResult;
        }
    }
}
