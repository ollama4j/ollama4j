package io.github.ollama4j.models.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.models.chat.OllamaChatResponseModel;
import io.github.ollama4j.models.chat.OllamaChatStreamObserver;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.utils.OllamaRequestBody;
import io.github.ollama4j.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    @Override
    protected boolean parseResponseAndAddToBuffer(String line, StringBuilder responseBuffer) {
        try {
            OllamaChatResponseModel ollamaResponseModel = Utils.getObjectMapper().readValue(line, OllamaChatResponseModel.class);
            responseBuffer.append(ollamaResponseModel.getMessage().getContent());
            if (streamObserver != null) {
                streamObserver.notify(ollamaResponseModel);
            }
            return ollamaResponseModel.isDone();
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing the Ollama chat response!", e);
            return true;
        }
    }

    public OllamaResult call(OllamaRequestBody body, OllamaStreamHandler streamHandler)
            throws OllamaBaseException, IOException, InterruptedException {
        streamObserver = new OllamaChatStreamObserver(streamHandler);
        return super.callSync(body);
    }
}
