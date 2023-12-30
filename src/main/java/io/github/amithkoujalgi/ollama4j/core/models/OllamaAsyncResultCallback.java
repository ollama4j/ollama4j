package io.github.amithkoujalgi.ollama4j.core.models;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
public class OllamaAsyncResultCallback extends Thread {
  private final HttpRequest.Builder requestBuilder;
  private final OllamaRequestModel ollamaRequestModel;
  private final Queue<String> queue = new LinkedList<>();
  private String result;
  private boolean isDone;

  /**
   * -- GETTER -- Returns the status of the request. Indicates if the request was successful or a
   * failure. If the request was a failure, the `getResponse()` method will return the error
   * message.
   */
  @Getter private boolean succeeded;

  private long requestTimeoutSeconds;

  /**
   * -- GETTER -- Returns the HTTP response status code for the request that was made to Ollama
   * server.
   */
  @Getter private int httpStatusCode;

  /** -- GETTER -- Returns the response time in milliseconds. */
  @Getter private long responseTime = 0;

  public OllamaAsyncResultCallback(
      HttpRequest.Builder requestBuilder,
      OllamaRequestModel ollamaRequestModel,
      long requestTimeoutSeconds) {
    this.requestBuilder = requestBuilder;
    this.ollamaRequestModel = ollamaRequestModel;
    this.isDone = false;
    this.result = "";
    this.queue.add("");
    this.requestTimeoutSeconds = requestTimeoutSeconds;
  }

  @Override
  public void run() {
    HttpClient httpClient = HttpClient.newHttpClient();
    try {
      long startTime = System.currentTimeMillis();
      HttpRequest request =
          requestBuilder
              .POST(
                  HttpRequest.BodyPublishers.ofString(
                      Utils.getObjectMapper().writeValueAsString(ollamaRequestModel)))
              .header("Content-Type", "application/json")
              .timeout(Duration.ofSeconds(requestTimeoutSeconds))
              .build();
      HttpResponse<InputStream> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
      int statusCode = response.statusCode();
      this.httpStatusCode = statusCode;

      InputStream responseBodyStream = response.body();
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
        String line;
        StringBuilder responseBuffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          if (statusCode == 404) {
            OllamaErrorResponseModel ollamaResponseModel =
                Utils.getObjectMapper().readValue(line, OllamaErrorResponseModel.class);
            queue.add(ollamaResponseModel.getError());
            responseBuffer.append(ollamaResponseModel.getError());
          } else {
            OllamaResponseModel ollamaResponseModel =
                Utils.getObjectMapper().readValue(line, OllamaResponseModel.class);
            queue.add(ollamaResponseModel.getResponse());
            if (!ollamaResponseModel.isDone()) {
              responseBuffer.append(ollamaResponseModel.getResponse());
            }
          }
        }

        this.isDone = true;
        this.succeeded = true;
        this.result = responseBuffer.toString();
        long endTime = System.currentTimeMillis();
        responseTime = endTime - startTime;
      }
      if (statusCode != 200) {
        throw new OllamaBaseException(this.result);
      }
    } catch (IOException | InterruptedException | OllamaBaseException e) {
      this.isDone = true;
      this.succeeded = false;
      this.result = "[FAILED] " + e.getMessage();
    }
  }

  /**
   * Returns the status of the thread. This does not indicate that the request was successful or a
   * failure, rather it is just a status flag to indicate if the thread is active or ended.
   *
   * @return boolean - status
   */
  public boolean isComplete() {
    return isDone;
  }

  /**
   * Returns the final completion/response when the execution completes. Does not return intermediate results.
   *
   * @return String completion/response text
   */
  public String getResponse() {
    return result;
  }

  public Queue<String> getStream() {
    return queue;
  }

  public void setRequestTimeoutSeconds(long requestTimeoutSeconds) {
    this.requestTimeoutSeconds = requestTimeoutSeconds;
  }
}
