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
    private final Auth auth;
    private final long requestTimeoutSeconds;
    private final boolean verbose;

    public OllamaEndpointCaller(String host, Auth auth, long requestTimeoutSeconds, boolean verbose) {
        this.host = host;
        this.auth = auth;
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
        if (isAuthCredentialsSet()) {
            requestBuilder.header("Authorization", this.auth.getAuthHeaderValue());
        }
        return requestBuilder;
    }

    /**
     * Check if Auth credentials set.
     *
     * @return true when Auth credentials set
     */
    protected boolean isAuthCredentialsSet() {
        return this.auth != null;
    }

}
