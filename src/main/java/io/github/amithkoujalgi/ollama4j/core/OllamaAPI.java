package io.github.amithkoujalgi.ollama4j.core;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.*;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base Ollama API class.
 */
@SuppressWarnings("DuplicatedCode")
public class OllamaAPI {

  private static final Logger logger = LoggerFactory.getLogger(OllamaAPI.class);
  private final String host;
  private long requestTimeoutSeconds = 3;
  private boolean verbose = true;

  /**
   * Instantiates the Ollama API.
   *
   * @param host the host address of Ollama server
   */
  public OllamaAPI(String host) {
    if (host.endsWith("/")) {
      this.host = host.substring(0, host.length() - 1);
    } else {
      this.host = host;
    }
  }

  public void setRequestTimeoutSeconds(long requestTimeoutSeconds) {
    this.requestTimeoutSeconds = requestTimeoutSeconds;
  }

  /**
   * Set/unset logging of responses
   *
   * @param verbose true/false
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * List available models from Ollama server.
   *
   * @return the list
   */
  public List<Model> listModels()
      throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
    String url = this.host + "/api/tags";
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(url))
        .header("Accept", "application/json").header("Content-type", "application/json")
        .timeout(Duration.ofSeconds(requestTimeoutSeconds)).GET().build();
    HttpResponse<String> response = httpClient.send(httpRequest,
        HttpResponse.BodyHandlers.ofString());
    int statusCode = response.statusCode();
    String responseString = response.body();
    if (statusCode == 200) {
      return Utils.getObjectMapper().readValue(responseString, ListModelsResponse.class)
          .getModels();
    } else {
      throw new OllamaBaseException(statusCode + " - " + responseString);
    }
  }

  /**
   * Pull a model on the Ollama server from the list of <a
   * href="https://ollama.ai/library">available models</a>.
   *
   * @param model the name of the model
   */
  public void pullModel(String model)
      throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
    String url = this.host + "/api/pull";
    String jsonData = String.format("{\"name\": \"%s\"}", model);
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
        .POST(HttpRequest.BodyPublishers.ofString(jsonData)).header("Accept", "application/json")
        .header("Content-type", "application/json")
        .timeout(Duration.ofSeconds(requestTimeoutSeconds)).build();
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<InputStream> response = client.send(request,
        HttpResponse.BodyHandlers.ofInputStream());
    int statusCode = response.statusCode();
    InputStream responseBodyStream = response.body();
    String responseString = "";
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        ModelPullResponse modelPullResponse = Utils.getObjectMapper()
            .readValue(line, ModelPullResponse.class);
        if (verbose) {
          logger.info(modelPullResponse.getStatus());
        }
      }
    }
    if (statusCode != 200) {
      throw new OllamaBaseException(statusCode + " - " + responseString);
    }
  }

  /**
   * Gets model details from the Ollama server.
   *
   * @param modelName the model
   * @return the model details
   */
  public ModelDetail getModelDetails(String modelName)
      throws IOException, OllamaBaseException, InterruptedException {
    String url = this.host + "/api/show";
    String jsonData = String.format("{\"name\": \"%s\"}", modelName);
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Accept", "application/json").header("Content-type", "application/json")
        .timeout(Duration.ofSeconds(requestTimeoutSeconds))
        .POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    int statusCode = response.statusCode();
    String responseBody = response.body();
    if (statusCode == 200) {
      return Utils.getObjectMapper().readValue(responseBody, ModelDetail.class);
    } else {
      throw new OllamaBaseException(statusCode + " - " + responseBody);
    }
  }

  /**
   * Create a custom model from a model file. Read more about custom model file creation <a
   * href="https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md">here</a>.
   *
   * @param modelName     the name of the custom model to be created.
   * @param modelFilePath the path to model file that exists on the Ollama server.
   */
  public void createModel(String modelName, String modelFilePath)
      throws IOException, InterruptedException, OllamaBaseException {
    String url = this.host + "/api/create";
    String jsonData = String.format("{\"name\": \"%s\", \"path\": \"%s\"}", modelName,
        modelFilePath);
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Accept", "application/json").header("Content-Type", "application/json")
        .timeout(Duration.ofSeconds(requestTimeoutSeconds))
        .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8)).build();
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    int statusCode = response.statusCode();
    String responseString = response.body();
    if (statusCode != 200) {
      throw new OllamaBaseException(statusCode + " - " + responseString);
    }
    // FIXME: Ollama API returns HTTP status code 200 for model creation failure cases. Correct this
    // if the issue is fixed in the Ollama API server.
    if (responseString.contains("error")) {
      throw new OllamaBaseException(responseString);
    }
    if (verbose) {
      logger.info(responseString);
    }
  }

  /**
   * Delete a model from Ollama server.
   *
   * @param name               the name of the model to be deleted.
   * @param ignoreIfNotPresent - ignore errors if the specified model is not present on Ollama
   *                           server.
   */
  public void deleteModel(String name, boolean ignoreIfNotPresent)
      throws IOException, InterruptedException, OllamaBaseException {
    String url = this.host + "/api/delete";
    String jsonData = String.format("{\"name\": \"%s\"}", name);
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8))
        .header("Accept", "application/json").header("Content-type", "application/json")
        .timeout(Duration.ofSeconds(requestTimeoutSeconds)).build();
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    int statusCode = response.statusCode();
    String responseBody = response.body();
    if (statusCode == 404 && responseBody.contains("model") && responseBody.contains("not found")) {
      return;
    }
    if (statusCode != 200) {
      throw new OllamaBaseException(statusCode + " - " + responseBody);
    }
  }

  /**
   * Generate embeddings for a given text from a model
   *
   * @param model  name of model to generate embeddings from
   * @param prompt text to generate embeddings for
   * @return embeddings
   */
  public List<Double> generateEmbeddings(String model, String prompt)
      throws IOException, InterruptedException, OllamaBaseException {
    String url = this.host + "/api/embeddings";
    String jsonData = String.format("{\"model\": \"%s\", \"prompt\": \"%s\"}", model, prompt);
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Accept", "application/json").header("Content-type", "application/json")
        .timeout(Duration.ofSeconds(requestTimeoutSeconds))
        .POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    int statusCode = response.statusCode();
    String responseBody = response.body();
    if (statusCode == 200) {
      EmbeddingResponse embeddingResponse = Utils.getObjectMapper()
          .readValue(responseBody, EmbeddingResponse.class);
      return embeddingResponse.getEmbedding();
    } else {
      throw new OllamaBaseException(statusCode + " - " + responseBody);
    }
  }

  /**
   * Ask a question to a model running on Ollama server. This is a sync/blocking call.
   *
   * @param model      the ollama model to ask the question to
   * @param promptText the prompt/question text
   * @return OllamaResult - that includes response text and time taken for response
   */
  public OllamaResult ask(String model, String promptText)
      throws OllamaBaseException, IOException, InterruptedException {
    OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(model, promptText);
    return askSync(ollamaRequestModel);
  }

  /**
   * With one or more image files, ask a question to a model running on Ollama server. This is a
   * sync/blocking call.
   *
   * @param model      the ollama model to ask the question to
   * @param promptText the prompt/question text
   * @param imageFiles the list of image files to use for the question
   * @return OllamaResult - that includes response text and time taken for response
   */
  public OllamaResult askWithImageFiles(String model, String promptText, List<File> imageFiles)
      throws OllamaBaseException, IOException, InterruptedException {
    List<String> images = new ArrayList<>();
    for (File imageFile : imageFiles) {
      images.add(encodeFileToBase64(imageFile));
    }
    OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(model, promptText, images);
    return askSync(ollamaRequestModel);
  }

  /**
   * With one or more image URLs, ask a question to a model running on Ollama server. This is a
   * sync/blocking call.
   *
   * @param model      the ollama model to ask the question to
   * @param promptText the prompt/question text
   * @param imageURLs  the list of image URLs to use for the question
   * @return OllamaResult - that includes response text and time taken for response
   */
  public OllamaResult askWithImageURLs(String model, String promptText, List<String> imageURLs)
      throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
    List<String> images = new ArrayList<>();
    for (String imageURL : imageURLs) {
      images.add(encodeByteArrayToBase64(loadImageBytesFromUrl(imageURL)));
    }
    OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(model, promptText, images);
    return askSync(ollamaRequestModel);
  }

  public static String encodeFileToBase64(File file) throws IOException {
    return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
  }

  public static String encodeByteArrayToBase64(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  public static byte[] loadImageBytesFromUrl(String imageUrl)
      throws IOException, URISyntaxException {
    URL url = new URI(imageUrl).toURL();
    try (InputStream in = url.openStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      return out.toByteArray();
    }
  }

  /**
   * Ask a question to a model running on Ollama server and get a callback handle that can be used
   * to check for status and get the response from the model later. This would be an
   * async/non-blocking call.
   *
   * @param model      the ollama model to ask the question to
   * @param promptText the prompt/question text
   * @return the ollama async result callback handle
   */
  public OllamaAsyncResultCallback askAsync(String model, String promptText) {
    OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(model, promptText);
    HttpClient httpClient = HttpClient.newHttpClient();
    URI uri = URI.create(this.host + "/api/generate");
    OllamaAsyncResultCallback ollamaAsyncResultCallback = new OllamaAsyncResultCallback(httpClient,
        uri, ollamaRequestModel, requestTimeoutSeconds);
    ollamaAsyncResultCallback.start();
    return ollamaAsyncResultCallback;
  }

  private OllamaResult askSync(OllamaRequestModel ollamaRequestModel)
      throws OllamaBaseException, IOException, InterruptedException {
    long startTime = System.currentTimeMillis();
    HttpClient httpClient = HttpClient.newHttpClient();
    URI uri = URI.create(this.host + "/api/generate");
    HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(
            Utils.getObjectMapper().writeValueAsString(ollamaRequestModel)))
        .header("Content-Type", "application/json")
        .timeout(Duration.ofSeconds(requestTimeoutSeconds)).build();
    HttpResponse<InputStream> response = httpClient.send(request,
        HttpResponse.BodyHandlers.ofInputStream());
    int statusCode = response.statusCode();
    InputStream responseBodyStream = response.body();
    StringBuilder responseBuffer = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (statusCode == 404) {
          OllamaErrorResponseModel ollamaResponseModel = Utils.getObjectMapper()
              .readValue(line, OllamaErrorResponseModel.class);
          responseBuffer.append(ollamaResponseModel.getError());
        } else {
          OllamaResponseModel ollamaResponseModel = Utils.getObjectMapper()
              .readValue(line, OllamaResponseModel.class);
          if (!ollamaResponseModel.isDone()) {
            responseBuffer.append(ollamaResponseModel.getResponse());
          }
        }
      }
    }
    if (statusCode != 200) {
      throw new OllamaBaseException(responseBuffer.toString());
    } else {
      long endTime = System.currentTimeMillis();
      return new OllamaResult(responseBuffer.toString().trim(), endTime - startTime, statusCode);
    }
  }
}
