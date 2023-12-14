package io.github.amithkoujalgi.ollama4j.core.models;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("unused")
public class OllamaAsyncResultCallback extends Thread {
  private final HttpClient client;
  private final URI uri;
  private final OllamaRequestModel ollamaRequestModel;
  private final Queue<String> queue = new LinkedList<>();
  private String result;
  private boolean isDone;
  private boolean succeeded;

  private int httpStatusCode;
  private long responseTime = 0;

  public OllamaAsyncResultCallback(
      HttpClient client, URI uri, OllamaRequestModel ollamaRequestModel) {
    this.client = client;
    this.ollamaRequestModel = ollamaRequestModel;
    this.uri = uri;
    this.isDone = false;
    this.result = "";
    this.queue.add("");
  }

  @Override
  public void run() {
    try {
      long startTime = System.currentTimeMillis();
      HttpRequest request =
          HttpRequest.newBuilder(uri)
              .POST(
                  HttpRequest.BodyPublishers.ofString(
                      Utils.getObjectMapper().writeValueAsString(ollamaRequestModel)))
              .header("Content-Type", "application/json")
              .build();
      HttpResponse<InputStream> response =
          client.send(request, HttpResponse.BodyHandlers.ofInputStream());
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
   * Returns the HTTP response status code for the request that was made to Ollama server.
   *
   * @return int - the status code for the request
   */
  public int getHttpStatusCode() {
    return httpStatusCode;
  }

  /**
   * Returns the status of the request. Indicates if the request was successful or a failure. If the
   * request was a failure, the `getResponse()` method will return the error message.
   *
   * @return boolean - status
   */
  public boolean isSucceeded() {
    return succeeded;
  }

  /**
   * Returns the final response when the execution completes. Does not return intermediate results.
   *
   * @return String - response text
   */
  public String getResponse() {
    return result;
  }

  public Queue<String> getStream() {
    return queue;
  }

  /**
   * Returns the response time in milliseconds.
   *
   * @return long - response time in milliseconds.
   */
  public long getResponseTime() {
    return responseTime;
  }
}
