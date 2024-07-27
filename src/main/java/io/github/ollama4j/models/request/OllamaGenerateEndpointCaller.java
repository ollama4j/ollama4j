package io.github.ollama4j.models.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.models.generate.OllamaGenerateResponseModel;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.utils.OllamaRequestBody;
import io.github.ollama4j.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OllamaGenerateEndpointCaller extends OllamaEndpointCaller {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaGenerateEndpointCaller.class);

    private OllamaGenerateStreamObserver streamObserver;

    public OllamaGenerateEndpointCaller(String host, BasicAuth basicAuth, long requestTimeoutSeconds, boolean verbose) {
        super(host, basicAuth, requestTimeoutSeconds, verbose);
    }

    @Override
    protected String getEndpointSuffix() {
        return "/api/generate";
    }

    @Override
    protected boolean parseResponseAndAddToBuffer(String line, StringBuilder responseBuffer) {
        try {
            OllamaGenerateResponseModel ollamaResponseModel = Utils.getObjectMapper().readValue(line, OllamaGenerateResponseModel.class);
            responseBuffer.append(ollamaResponseModel.getResponse());
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
        streamObserver = new OllamaGenerateStreamObserver(streamHandler);
        return super.callSync(body);
    }
}
