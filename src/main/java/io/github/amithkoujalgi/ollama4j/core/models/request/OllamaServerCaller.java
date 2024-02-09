package io.github.amithkoujalgi.ollama4j.core.models.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.BasicAuth;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaErrorResponseModel;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResponseModel;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.utils.OllamaRequestBody;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;

public abstract class OllamaServerCaller {
    
    private static final Logger LOG = LoggerFactory.getLogger(OllamaAPI.class);

    private String host;
    private BasicAuth basicAuth;
    private long requestTimeoutSeconds;
    private boolean verbose;

    public OllamaServerCaller(String host, BasicAuth basicAuth, long requestTimeoutSeconds, boolean verbose) {
        this.host = host;
        this.basicAuth = basicAuth;
        this.requestTimeoutSeconds = requestTimeoutSeconds;
        this.verbose = verbose;
    }

    protected abstract String getEndpointSuffix();
    
    public OllamaResult generateSync(OllamaRequestBody body)  throws OllamaBaseException, IOException, InterruptedException{

        // Create Request
    long startTime = System.currentTimeMillis();
    HttpClient httpClient = HttpClient.newHttpClient();
    URI uri = URI.create(this.host + getEndpointSuffix());
    HttpRequest.Builder requestBuilder =
        getRequestBuilderDefault(uri)
            .POST(
                body.getBodyPublisher());
    HttpRequest request = requestBuilder.build();
    if (this.verbose) LOG.info("Asking model: " + body.getBodyPublisher());
    HttpResponse<InputStream> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
    
        
        int statusCode = response.statusCode();
    InputStream responseBodyStream = response.body();
    StringBuilder responseBuffer = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (statusCode == 404) {
            LOG.warn("Status code: 404 (Not Found)");
          OllamaErrorResponseModel ollamaResponseModel =
              Utils.getObjectMapper().readValue(line, OllamaErrorResponseModel.class);
          responseBuffer.append(ollamaResponseModel.getError());
        } else if (statusCode == 401) {
            LOG.warn("Status code: 401 (Unauthorized)");
          OllamaErrorResponseModel ollamaResponseModel =
              Utils.getObjectMapper()
                  .readValue("{\"error\":\"Unauthorized\"}", OllamaErrorResponseModel.class);
          responseBuffer.append(ollamaResponseModel.getError());
        } else {
          OllamaResponseModel ollamaResponseModel =
              Utils.getObjectMapper().readValue(line, OllamaResponseModel.class);
          if (!ollamaResponseModel.isDone()) {
            responseBuffer.append(ollamaResponseModel.getResponse());
          }
        }
      }
    }

    if (statusCode != 200) {
        LOG.error("Status code " + statusCode);
      throw new OllamaBaseException(responseBuffer.toString());
    } else {
      long endTime = System.currentTimeMillis();
      OllamaResult ollamaResult =
          new OllamaResult(responseBuffer.toString().trim(), endTime - startTime, statusCode);
      if (verbose) LOG.info("Model response: " + ollamaResult);
      return ollamaResult;
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
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(this.requestTimeoutSeconds));
    if (isBasicAuthCredentialsSet()) {
      requestBuilder.header("Authorization", getBasicAuthHeaderValue());
    }
    return requestBuilder;
  }

  /**
   * Get basic authentication header value.
   *
   * @return basic authentication header value (encoded credentials)
   */
  private String getBasicAuthHeaderValue() {
    String credentialsToEncode = this.basicAuth.getUsername() + ":" + this.basicAuth.getPassword();
    return "Basic " + Base64.getEncoder().encodeToString(credentialsToEncode.getBytes());
  }

  /**
   * Check if Basic Auth credentials set.
   *
   * @return true when Basic Auth credentials set
   */
  private boolean isBasicAuthCredentialsSet() {
    return this.basicAuth != null;
  }
    
}