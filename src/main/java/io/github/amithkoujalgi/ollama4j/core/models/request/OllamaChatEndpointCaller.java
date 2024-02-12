package io.github.amithkoujalgi.ollama4j.core.models.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.amithkoujalgi.ollama4j.core.models.BasicAuth;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatResponseModel;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;

/**
 * Specialization class for requests
 */
public class OllamaChatEndpointCaller extends OllamaEndpointCaller{

    private static final Logger LOG = LoggerFactory.getLogger(OllamaChatEndpointCaller.class);

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
                    return ollamaResponseModel.isDone();
                } catch (JsonProcessingException e) {
                    LOG.error("Error parsing the Ollama chat response!",e);
                    return true;
                }         
    }



    

}
