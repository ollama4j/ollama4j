package io.github.ollama4j.models.request;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ollama4j.OllamaAPI;
import lombok.Getter;

/**
 * Abstract helperclass to call the ollama api server.
 */
@Getter
public abstract class OllamaEndpointCaller {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaAPI.class);

    private final String host;
    private final BasicAuth basicAuth;
    private final long requestTimeoutSeconds;
    private final boolean verbose;

    public OllamaEndpointCaller(String host, BasicAuth basicAuth, long requestTimeoutSeconds, boolean verbose) {
        this.host = host;
        this.basicAuth = basicAuth;
        this.requestTimeoutSeconds = requestTimeoutSeconds;
        this.verbose = verbose;
    }

    protected abstract String getEndpointSuffix();

    protected abstract boolean parseResponseAndAddToBuffer(String line, StringBuilder responseBuffer);


    /**
     * Get default request builder.
     *
     * @param uri URI to get a HttpRequest.Builder
     * @return HttpRequest.Builder
     */
    protected HttpRequest.Builder getRequestBuilderDefault(URI uri) {
        HttpRequest.Builder requestBuilder =
                HttpRequest.newBuilder(uri)
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(this.requestTimeoutSeconds));
        if (isBasicAuthCredentialsSet()) {
            requestBuilder.header("Authorization", this.basicAuth.getBasicAuthHeaderValue());
        }
        return requestBuilder;
    }

    /**
     * Check if Basic Auth credentials set.
     *
     * @return true when Basic Auth credentials set
     */
    protected boolean isBasicAuthCredentialsSet() {
        return this.basicAuth != null;
    }

}
