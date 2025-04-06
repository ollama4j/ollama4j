package io.github.ollama4j;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.exceptions.ToolNotFoundException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
import io.github.ollama4j.models.embeddings.OllamaEmbeddingResponseModel;
import io.github.ollama4j.models.embeddings.OllamaEmbeddingsRequestModel;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.models.generate.OllamaTokenHandler;
import io.github.ollama4j.models.ps.ModelsProcessResponse;
import io.github.ollama4j.models.request.*;
import io.github.ollama4j.models.response.*;
import io.github.ollama4j.tools.*;
import io.github.ollama4j.tools.annotations.OllamaToolService;
import io.github.ollama4j.tools.annotations.ToolProperty;
import io.github.ollama4j.tools.annotations.ToolSpec;
import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.Utils;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The base Ollama API class.
 */
@SuppressWarnings({ "DuplicatedCode", "resource" })
public class OllamaAPI {

    private static final Logger logger = LoggerFactory.getLogger(OllamaAPI.class);
    private final String host;
    /**
     * -- SETTER --
     * Set request timeout in seconds. Default is 3 seconds.
     */
    @Setter
    private long requestTimeoutSeconds = 10;
    /**
     * -- SETTER --
     * Set/unset logging of responses
     */
    @Setter
    private boolean verbose = true;

    @Setter
    private int maxChatToolCallRetries = 3;

    private Auth auth;

    private int numberOfRetriesForModelPull = 0;

    public void setNumberOfRetriesForModelPull(int numberOfRetriesForModelPull) {
        this.numberOfRetriesForModelPull = numberOfRetriesForModelPull;
    }

    private final ToolRegistry toolRegistry = new ToolRegistry();

    /**
     * Instantiates the Ollama API with default Ollama host:
     * <a href="http://localhost:11434">http://localhost:11434</a>
     **/
    public OllamaAPI() {
        this.host = "http://localhost:11434";
    }

    /**
     * Instantiates the Ollama API with specified Ollama host address.
     *
     * @param host the host address of Ollama server
     */
    public OllamaAPI(String host) {
        if (host.endsWith("/")) {
            this.host = host.substring(0, host.length() - 1);
        } else {
            this.host = host;
        }
        if (this.verbose) {
            logger.info("Ollama API initialized with host: " + this.host);
        }
    }

    /**
     * Set basic authentication for accessing Ollama server that's behind a
     * reverse-proxy/gateway.
     *
     * @param username the username
     * @param password the password
     */
    public void setBasicAuth(String username, String password) {
        this.auth = new BasicAuth(username, password);
    }

    /**
     * Set Bearer authentication for accessing Ollama server that's behind a
     * reverse-proxy/gateway.
     *
     * @param bearerToken the Bearer authentication token to provide
     */
    public void setBearerAuth(String bearerToken) {
        this.auth = new BearerAuth(bearerToken);
    }

    /**
     * API to check the reachability of Ollama server.
     *
     * @return true if the server is reachable, false otherwise.
     */
    public boolean ping() {
        String url = this.host + "/api/tags";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = null;
        try {
            httpRequest = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                    .header("Content-type", "application/json").GET().build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (HttpConnectTimeoutException e) {
            return false;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        int statusCode = response.statusCode();
        return statusCode == 200;
    }

    /**
     * Provides a list of running models and details about each model currently
     * loaded into memory.
     *
     * @return ModelsProcessResponse containing details about the running models
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws OllamaBaseException  if the response indicates an error status
     */
    public ModelsProcessResponse ps() throws IOException, InterruptedException, OllamaBaseException {
        String url = this.host + "/api/ps";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = null;
        try {
            httpRequest = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                    .header("Content-type", "application/json").GET().build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpResponse<String> response = null;
        response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();
        if (statusCode == 200) {
            return Utils.getObjectMapper().readValue(responseString, ModelsProcessResponse.class);
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
    }

    /**
     * Lists available models from the Ollama server.
     *
     * @return a list of models available on the server
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public List<Model> listModels() throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        String url = this.host + "/api/tags";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-type", "application/json").GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();
        if (statusCode == 200) {
            return Utils.getObjectMapper().readValue(responseString, ListModelsResponse.class).getModels();
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
    }

    /**
     * Retrieves a list of models from the Ollama library. This method fetches the
     * available models directly from Ollama
     * library page, including model details such as the name, pull count, popular
     * tags, tag count, and the time when model was updated.
     *
     * @return A list of {@link LibraryModel} objects representing the models
     *         available in the Ollama library.
     * @throws OllamaBaseException  If the HTTP request fails or the response is not
     *                              successful (non-200 status code).
     * @throws IOException          If an I/O error occurs during the HTTP request
     *                              or response processing.
     * @throws InterruptedException If the thread executing the request is
     *                              interrupted.
     * @throws URISyntaxException   If there is an error creating the URI for the
     *                              HTTP request.
     */
    public List<LibraryModel> listModelsFromLibrary()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        String url = "https://ollama.com/library";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-type", "application/json").GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();
        List<LibraryModel> models = new ArrayList<>();
        if (statusCode == 200) {
            Document doc = Jsoup.parse(responseString);
            Elements modelSections = doc.selectXpath("//*[@id='repo']/ul/li/a");
            for (Element e : modelSections) {
                LibraryModel model = new LibraryModel();
                Elements names = e.select("div > h2 > div > span");
                Elements desc = e.select("div > p");
                Elements pullCounts = e.select("div:nth-of-type(2) > p > span:first-of-type > span:first-of-type");
                Elements popularTags = e.select("div > div > span");
                Elements totalTags = e.select("div:nth-of-type(2) > p > span:nth-of-type(2) > span:first-of-type");
                Elements lastUpdatedTime = e
                        .select("div:nth-of-type(2) > p > span:nth-of-type(3) > span:nth-of-type(2)");

                if (names.first() == null || names.isEmpty()) {
                    // if name cannot be extracted, skip.
                    continue;
                }
                Optional.ofNullable(names.first()).map(Element::text).ifPresent(model::setName);
                model.setDescription(Optional.ofNullable(desc.first()).map(Element::text).orElse(""));
                model.setPopularTags(Optional.of(popularTags)
                        .map(tags -> tags.stream().map(Element::text).collect(Collectors.toList()))
                        .orElse(new ArrayList<>()));
                model.setPullCount(Optional.ofNullable(pullCounts.first()).map(Element::text).orElse(""));
                model.setTotalTags(
                        Optional.ofNullable(totalTags.first()).map(Element::text).map(Integer::parseInt).orElse(0));
                model.setLastUpdated(Optional.ofNullable(lastUpdatedTime.first()).map(Element::text).orElse(""));

                models.add(model);
            }
            return models;
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
    }

    /**
     * Fetches the tags associated with a specific model from Ollama library.
     * This method fetches the available model tags directly from Ollama library
     * model page, including model tag name, size and time when model was last
     * updated
     * into a list of {@link LibraryModelTag} objects.
     *
     * @param libraryModel the {@link LibraryModel} object which contains the name
     *                     of the library model
     *                     for which the tags need to be fetched.
     * @return a list of {@link LibraryModelTag} objects containing the extracted
     *         tags and their associated metadata.
     * @throws OllamaBaseException  if the HTTP response status code indicates an
     *                              error (i.e., not 200 OK),
     *                              or if there is any other issue during the
     *                              request or response processing.
     * @throws IOException          if an input/output exception occurs during the
     *                              HTTP request or response handling.
     * @throws InterruptedException if the thread is interrupted while waiting for
     *                              the HTTP response.
     * @throws URISyntaxException   if the URI format is incorrect or invalid.
     */
    public LibraryModelDetail getLibraryModelDetails(LibraryModel libraryModel)
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        String url = String.format("https://ollama.com/library/%s/tags", libraryModel.getName());
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-type", "application/json").GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();

        List<LibraryModelTag> libraryModelTags = new ArrayList<>();
        if (statusCode == 200) {
            Document doc = Jsoup.parse(responseString);
            Elements tagSections = doc
                    .select("html > body > main > div > section > div > div > div:nth-child(n+2) > div");
            for (Element e : tagSections) {
                Elements tags = e.select("div > a > div");
                Elements tagsMetas = e.select("div > span");

                LibraryModelTag libraryModelTag = new LibraryModelTag();

                if (tags.first() == null || tags.isEmpty()) {
                    // if tag cannot be extracted, skip.
                    continue;
                }
                libraryModelTag.setName(libraryModel.getName());
                Optional.ofNullable(tags.first()).map(Element::text).ifPresent(libraryModelTag::setTag);
                libraryModelTag.setSize(Optional.ofNullable(tagsMetas.first()).map(element -> element.text().split("•"))
                        .filter(parts -> parts.length > 1).map(parts -> parts[1].trim()).orElse(""));
                libraryModelTag
                        .setLastUpdated(Optional.ofNullable(tagsMetas.first()).map(element -> element.text().split("•"))
                                .filter(parts -> parts.length > 1).map(parts -> parts[2].trim()).orElse(""));
                libraryModelTags.add(libraryModelTag);
            }
            LibraryModelDetail libraryModelDetail = new LibraryModelDetail();
            libraryModelDetail.setModel(libraryModel);
            libraryModelDetail.setTags(libraryModelTags);
            return libraryModelDetail;
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
    }

    /**
     * Finds a specific model using model name and tag from Ollama library.
     * <p>
     * This method retrieves the model from the Ollama library by its name, then
     * fetches its tags.
     * It searches through the tags of the model to find one that matches the
     * specified tag name.
     * If the model or the tag is not found, it throws a
     * {@link NoSuchElementException}.
     *
     * @param modelName The name of the model to search for in the library.
     * @param tag       The tag name to search for within the specified model.
     * @return The {@link LibraryModelTag} associated with the specified model and
     *         tag.
     * @throws OllamaBaseException    If there is a problem with the Ollama library
     *                                operations.
     * @throws IOException            If an I/O error occurs during the operation.
     * @throws URISyntaxException     If there is an error with the URI syntax.
     * @throws InterruptedException   If the operation is interrupted.
     * @throws NoSuchElementException If the model or the tag is not found.
     */
    public LibraryModelTag findModelTagFromLibrary(String modelName, String tag)
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        List<LibraryModel> libraryModels = this.listModelsFromLibrary();
        LibraryModel libraryModel = libraryModels.stream().filter(model -> model.getName().equals(modelName))
                .findFirst().orElseThrow(
                        () -> new NoSuchElementException(String.format("Model by name '%s' not found", modelName)));
        LibraryModelDetail libraryModelDetail = this.getLibraryModelDetails(libraryModel);
        LibraryModelTag libraryModelTag = libraryModelDetail.getTags().stream()
                .filter(tagName -> tagName.getTag().equals(tag)).findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Tag '%s' for model '%s' not found", tag, modelName)));
        return libraryModelTag;
    }

    /**
     * Pull a model on the Ollama server from the list of <a
     * href="https://ollama.ai/library">available models</a>.
     *
     * @param modelName the name of the model
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public void pullModel(String modelName)
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        if (numberOfRetriesForModelPull == 0) {
            this.doPullModel(modelName);
        } else {
            int numberOfRetries = 0;
            while (numberOfRetries < numberOfRetriesForModelPull) {
                try {
                    this.doPullModel(modelName);
                    return;
                } catch (OllamaBaseException e) {
                    logger.error("Failed to pull model " + modelName + ", retrying...");
                    numberOfRetries++;
                }
            }
            throw new OllamaBaseException(
                    "Failed to pull model " + modelName + " after " + numberOfRetriesForModelPull + " retries");
        }
    }

    private void doPullModel(String modelName)
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String url = this.host + "/api/pull";
        String jsonData = new ModelRequest(modelName).toString();
        HttpRequest request = getRequestBuilderDefault(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        int statusCode = response.statusCode();
        InputStream responseBodyStream = response.body();
        String responseString = "";
        boolean success = false; // Flag to check the pull success.
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ModelPullResponse modelPullResponse = Utils.getObjectMapper().readValue(line, ModelPullResponse.class);
                if (modelPullResponse != null && modelPullResponse.getStatus() != null) {
                    if (verbose) {
                        logger.info(modelName + ": " + modelPullResponse.getStatus());
                    }
                    // Check if status is "success" and set success flag to true.
                    if ("success".equalsIgnoreCase(modelPullResponse.getStatus())) {
                        success = true;
                    }
                } else {
                    logger.error("Received null or invalid status for model pull.");
                }
            }
        }
        if (!success) {
            logger.error("Model pull failed or returned invalid status.");
            throw new OllamaBaseException("Model pull failed or returned invalid status.");
        }
        if (statusCode != 200) {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
    }

    public String getVersion() throws URISyntaxException, IOException, InterruptedException, OllamaBaseException {
        String url = this.host + "/api/version";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-type", "application/json").GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();
        if (statusCode == 200) {
            return Utils.getObjectMapper().readValue(responseString, OllamaVersion.class).getVersion();
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
    }

    /**
     * Pulls a model using the specified Ollama library model tag.
     * The model is identified by a name and a tag, which are combined into a single
     * identifier
     * in the format "name:tag" to pull the corresponding model.
     *
     * @param libraryModelTag the {@link LibraryModelTag} object containing the name
     *                        and tag
     *                        of the model to be pulled.
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public void pullModel(LibraryModelTag libraryModelTag)
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String tagToPull = String.format("%s:%s", libraryModelTag.getName(), libraryModelTag.getTag());
        pullModel(tagToPull);
    }

    /**
     * Gets model details from the Ollama server.
     *
     * @param modelName the model
     * @return the model details
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public ModelDetail getModelDetails(String modelName)
            throws IOException, OllamaBaseException, InterruptedException, URISyntaxException {
        String url = this.host + "/api/show";
        String jsonData = new ModelRequest(modelName).toString();
        HttpRequest request = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
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
     * Create a custom model from a model file. Read more about custom model file
     * creation <a
     * href=
     * "https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md">here</a>.
     *
     * @param modelName     the name of the custom model to be created.
     * @param modelFilePath the path to model file that exists on the Ollama server.
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    @Deprecated
    public void createModelWithFilePath(String modelName, String modelFilePath)
            throws IOException, InterruptedException, OllamaBaseException, URISyntaxException {
        String url = this.host + "/api/create";
        String jsonData = new CustomModelFilePathRequest(modelName, modelFilePath).toString();
        HttpRequest request = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8)).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();
        if (statusCode != 200) {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
        // FIXME: Ollama API returns HTTP status code 200 for model creation failure
        // cases. Correct this
        // if the issue is fixed in the Ollama API server.
        if (responseString.contains("error")) {
            throw new OllamaBaseException(responseString);
        }
        if (verbose) {
            logger.info(responseString);
        }
    }

    /**
     * Create a custom model from a model file. Read more about custom model file
     * creation <a
     * href=
     * "https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md">here</a>.
     *
     * @param modelName         the name of the custom model to be created.
     * @param modelFileContents the path to model file that exists on the Ollama
     *                          server.
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    @Deprecated
    public void createModelWithModelFileContents(String modelName, String modelFileContents)
            throws IOException, InterruptedException, OllamaBaseException, URISyntaxException {
        String url = this.host + "/api/create";
        String jsonData = new CustomModelFileContentsRequest(modelName, modelFileContents).toString();
        HttpRequest request = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8)).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();
        if (statusCode != 200) {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
        if (responseString.contains("error")) {
            throw new OllamaBaseException(responseString);
        }
        if (verbose) {
            logger.info(responseString);
        }
    }

    /**
     * Create a custom model. Read more about custom model creation <a
     * href=
     * "https://github.com/ollama/ollama/blob/main/docs/api.md#create-a-model">here</a>.
     *
     * @param customModelRequest custom model spec
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public void createModel(CustomModelRequest customModelRequest)
            throws IOException, InterruptedException, OllamaBaseException, URISyntaxException {
        String url = this.host + "/api/create";
        String jsonData = customModelRequest.toString();
        HttpRequest request = getRequestBuilderDefault(new URI(url)).header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8)).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseString = response.body();
        if (statusCode != 200) {
            throw new OllamaBaseException(statusCode + " - " + responseString);
        }
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
     * @param modelName          the name of the model to be deleted.
     * @param ignoreIfNotPresent ignore errors if the specified model is not present
     *                           on Ollama server.
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public void deleteModel(String modelName, boolean ignoreIfNotPresent)
            throws IOException, InterruptedException, OllamaBaseException, URISyntaxException {
        String url = this.host + "/api/delete";
        String jsonData = new ModelRequest(modelName).toString();
        HttpRequest request = getRequestBuilderDefault(new URI(url))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8))
                .header("Accept", "application/json").header("Content-type", "application/json").build();
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
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @deprecated Use {@link #embed(String, List)} instead.
     */
    @Deprecated
    public List<Double> generateEmbeddings(String model, String prompt)
            throws IOException, InterruptedException, OllamaBaseException {
        return generateEmbeddings(new OllamaEmbeddingsRequestModel(model, prompt));
    }

    /**
     * Generate embeddings using a {@link OllamaEmbeddingsRequestModel}.
     *
     * @param modelRequest request for '/api/embeddings' endpoint
     * @return embeddings
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @deprecated Use {@link #embed(OllamaEmbedRequestModel)} instead.
     */
    @Deprecated
    public List<Double> generateEmbeddings(OllamaEmbeddingsRequestModel modelRequest)
            throws IOException, InterruptedException, OllamaBaseException {
        URI uri = URI.create(this.host + "/api/embeddings");
        String jsonData = modelRequest.toString();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = getRequestBuilderDefault(uri).header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData));
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (statusCode == 200) {
            OllamaEmbeddingResponseModel embeddingResponse = Utils.getObjectMapper().readValue(responseBody,
                    OllamaEmbeddingResponseModel.class);
            return embeddingResponse.getEmbedding();
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseBody);
        }
    }

    /**
     * Generate embeddings for a given text from a model
     *
     * @param model  name of model to generate embeddings from
     * @param inputs text/s to generate embeddings for
     * @return embeddings
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaEmbedResponseModel embed(String model, List<String> inputs)
            throws IOException, InterruptedException, OllamaBaseException {
        return embed(new OllamaEmbedRequestModel(model, inputs));
    }

    /**
     * Generate embeddings using a {@link OllamaEmbedRequestModel}.
     *
     * @param modelRequest request for '/api/embed' endpoint
     * @return embeddings
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaEmbedResponseModel embed(OllamaEmbedRequestModel modelRequest)
            throws IOException, InterruptedException, OllamaBaseException {
        URI uri = URI.create(this.host + "/api/embed");
        String jsonData = Utils.getObjectMapper().writeValueAsString(modelRequest);
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(uri).header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode == 200) {
            return Utils.getObjectMapper().readValue(responseBody, OllamaEmbedResponseModel.class);
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseBody);
        }
    }

    /**
     * Generate response for a question to a model running on Ollama server. This is
     * a sync/blocking
     * call.
     *
     * @param model         the ollama model to ask the question to
     * @param prompt        the prompt/question text
     * @param options       the Options object - <a
     *                      href=
     *                      "https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">More
     *                      details on the options</a>
     * @param streamHandler optional callback consumer that will be applied every
     *                      time a streamed response is received. If not set, the
     *                      stream parameter of the request is set to false.
     * @return OllamaResult that includes response text and time taken for response
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaResult generate(String model, String prompt, boolean raw, Options options,
            OllamaStreamHandler streamHandler) throws OllamaBaseException, IOException, InterruptedException {
        OllamaGenerateRequest ollamaRequestModel = new OllamaGenerateRequest(model, prompt);
        ollamaRequestModel.setRaw(raw);
        ollamaRequestModel.setOptions(options.getOptionsMap());
        return generateSyncForOllamaRequestModel(ollamaRequestModel, streamHandler);
    }

    /**
     * Generates structured output from the specified AI model and prompt.
     *
     * @param model  The name or identifier of the AI model to use for generating
     *               the response.
     * @param prompt The input text or prompt to provide to the AI model.
     * @param format A map containing the format specification for the structured
     *               output.
     * @return An instance of {@link OllamaResult} containing the structured
     *         response.
     * @throws OllamaBaseException  if the response indicates an error status.
     * @throws IOException          if an I/O error occurs during the HTTP request.
     * @throws InterruptedException if the operation is interrupted.
     */
    public OllamaResult generate(String model, String prompt, Map<String, Object> format)
            throws OllamaBaseException, IOException, InterruptedException {
        URI uri = URI.create(this.host + "/api/generate");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);
        requestBody.put("format", format);

        String jsonData = Utils.getObjectMapper().writeValueAsString(requestBody);
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode == 200) {
            OllamaStructuredResult structuredResult = Utils.getObjectMapper().readValue(responseBody,
                    OllamaStructuredResult.class);
            OllamaResult ollamaResult = new OllamaResult(structuredResult.getResponse(),
                    structuredResult.getResponseTime(), statusCode);
            return ollamaResult;
        } else {
            throw new OllamaBaseException(statusCode + " - " + responseBody);
        }
    }

    /**
     * Generates response using the specified AI model and prompt (in blocking
     * mode).
     * <p>
     * Uses {@link #generate(String, String, boolean, Options, OllamaStreamHandler)}
     *
     * @param model   The name or identifier of the AI model to use for generating
     *                the response.
     * @param prompt  The input text or prompt to provide to the AI model.
     * @param raw     In some cases, you may wish to bypass the templating system
     *                and provide a full prompt. In this case, you can use the raw
     *                parameter to disable templating. Also note that raw mode will
     *                not return a context.
     * @param options Additional options or configurations to use when generating
     *                the response.
     * @return {@link OllamaResult}
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaResult generate(String model, String prompt, boolean raw, Options options)
            throws OllamaBaseException, IOException, InterruptedException {
        return generate(model, prompt, raw, options, null);
    }

    /**
     * Generates response using the specified AI model and prompt (in blocking
     * mode), and then invokes a set of tools
     * on the generated response.
     *
     * @param model   The name or identifier of the AI model to use for generating
     *                the response.
     * @param prompt  The input text or prompt to provide to the AI model.
     * @param options Additional options or configurations to use when generating
     *                the response.
     * @return {@link OllamaToolsResult} An OllamaToolsResult object containing the
     *         response from the AI model and the results of invoking the tools on
     *         that output.
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaToolsResult generateWithTools(String model, String prompt, Options options)
            throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        boolean raw = true;
        OllamaToolsResult toolResult = new OllamaToolsResult();
        Map<ToolFunctionCallSpec, Object> toolResults = new HashMap<>();

        if (!prompt.startsWith("[AVAILABLE_TOOLS]")) {
            final Tools.PromptBuilder promptBuilder = new Tools.PromptBuilder();
            for (Tools.ToolSpecification spec : toolRegistry.getRegisteredSpecs()) {
                promptBuilder.withToolSpecification(spec);
            }
            promptBuilder.withPrompt(prompt);
            prompt = promptBuilder.build();
        }

        OllamaResult result = generate(model, prompt, raw, options, null);
        toolResult.setModelResult(result);

        String toolsResponse = result.getResponse();
        if (toolsResponse.contains("[TOOL_CALLS]")) {
            toolsResponse = toolsResponse.replace("[TOOL_CALLS]", "");
        }

        List<ToolFunctionCallSpec> toolFunctionCallSpecs = new ArrayList<>();
        ObjectMapper objectMapper = Utils.getObjectMapper();

        if (!toolsResponse.isEmpty()) {
            try {
                // Try to parse the string to see if it's a valid JSON
                JsonNode jsonNode = objectMapper.readTree(toolsResponse);
            } catch (JsonParseException e) {
                logger.warn("Response from model does not contain any tool calls. Returning the response as is.");
                return toolResult;
            }
            toolFunctionCallSpecs = objectMapper.readValue(
                    toolsResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ToolFunctionCallSpec.class));
        }
        for (ToolFunctionCallSpec toolFunctionCallSpec : toolFunctionCallSpecs) {
            toolResults.put(toolFunctionCallSpec, invokeTool(toolFunctionCallSpec));
        }
        toolResult.setToolResults(toolResults);
        return toolResult;
    }

    /**
     * Generate response for a question to a model running on Ollama server and get
     * a callback handle
     * that can be used to check for status and get the response from the model
     * later. This would be
     * an async/non-blocking call.
     *
     * @param model  the ollama model to ask the question to
     * @param prompt the prompt/question text
     * @return the ollama async result callback handle
     */
    public OllamaAsyncResultStreamer generateAsync(String model, String prompt, boolean raw) {
        OllamaGenerateRequest ollamaRequestModel = new OllamaGenerateRequest(model, prompt);
        ollamaRequestModel.setRaw(raw);
        URI uri = URI.create(this.host + "/api/generate");
        OllamaAsyncResultStreamer ollamaAsyncResultStreamer = new OllamaAsyncResultStreamer(
                getRequestBuilderDefault(uri), ollamaRequestModel, requestTimeoutSeconds);
        ollamaAsyncResultStreamer.start();
        return ollamaAsyncResultStreamer;
    }

    /**
     * With one or more image files, ask a question to a model running on Ollama
     * server. This is a
     * sync/blocking call.
     *
     * @param model         the ollama model to ask the question to
     * @param prompt        the prompt/question text
     * @param imageFiles    the list of image files to use for the question
     * @param options       the Options object - <a
     *                      href=
     *                      "https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">More
     *                      details on the options</a>
     * @param streamHandler optional callback consumer that will be applied every
     *                      time a streamed response is received. If not set, the
     *                      stream parameter of the request is set to false.
     * @return OllamaResult that includes response text and time taken for response
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaResult generateWithImageFiles(String model, String prompt, List<File> imageFiles, Options options,
            OllamaStreamHandler streamHandler) throws OllamaBaseException, IOException, InterruptedException {
        List<String> images = new ArrayList<>();
        for (File imageFile : imageFiles) {
            images.add(encodeFileToBase64(imageFile));
        }
        OllamaGenerateRequest ollamaRequestModel = new OllamaGenerateRequest(model, prompt, images);
        ollamaRequestModel.setOptions(options.getOptionsMap());
        return generateSyncForOllamaRequestModel(ollamaRequestModel, streamHandler);
    }

    /**
     * Convenience method to call Ollama API without streaming responses.
     * <p>
     * Uses
     * {@link #generateWithImageFiles(String, String, List, Options, OllamaStreamHandler)}
     *
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaResult generateWithImageFiles(String model, String prompt, List<File> imageFiles, Options options)
            throws OllamaBaseException, IOException, InterruptedException {
        return generateWithImageFiles(model, prompt, imageFiles, options, null);
    }

    /**
     * With one or more image URLs, ask a question to a model running on Ollama
     * server. This is a
     * sync/blocking call.
     *
     * @param model         the ollama model to ask the question to
     * @param prompt        the prompt/question text
     * @param imageURLs     the list of image URLs to use for the question
     * @param options       the Options object - <a
     *                      href=
     *                      "https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">More
     *                      details on the options</a>
     * @param streamHandler optional callback consumer that will be applied every
     *                      time a streamed response is received. If not set, the
     *                      stream parameter of the request is set to false.
     * @return OllamaResult that includes response text and time taken for response
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public OllamaResult generateWithImageURLs(String model, String prompt, List<String> imageURLs, Options options,
            OllamaStreamHandler streamHandler)
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        List<String> images = new ArrayList<>();
        for (String imageURL : imageURLs) {
            images.add(encodeByteArrayToBase64(Utils.loadImageBytesFromUrl(imageURL)));
        }
        OllamaGenerateRequest ollamaRequestModel = new OllamaGenerateRequest(model, prompt, images);
        ollamaRequestModel.setOptions(options.getOptionsMap());
        return generateSyncForOllamaRequestModel(ollamaRequestModel, streamHandler);
    }

    /**
     * Convenience method to call Ollama API without streaming responses.
     * <p>
     * Uses
     * {@link #generateWithImageURLs(String, String, List, Options, OllamaStreamHandler)}
     *
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws URISyntaxException   if the URI for the request is malformed
     */
    public OllamaResult generateWithImageURLs(String model, String prompt, List<String> imageURLs, Options options)
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        return generateWithImageURLs(model, prompt, imageURLs, options, null);
    }

    /**
     * Synchronously generates a response using a list of image byte arrays.
     * <p>
     * This method encodes the provided byte arrays into Base64 and sends them to the Ollama server.
     *
     * @param model         the Ollama model to use for generating the response
     * @param prompt        the prompt or question text to send to the model
     * @param images        the list of image data as byte arrays
     * @param options       the Options object - <a href="https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">More details on the options</a>
     * @param streamHandler optional callback that will be invoked with each streamed response; if null, streaming is disabled
     * @return OllamaResult containing the response text and the time taken for the response
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaResult generateWithImages(String model, String prompt, List<byte[]> images, Options options, OllamaStreamHandler streamHandler) throws OllamaBaseException, IOException, InterruptedException {
        List<String> encodedImages = new ArrayList<>();
        for (byte[] image : images) {
            encodedImages.add(encodeByteArrayToBase64(image));
        }
        OllamaGenerateRequest ollamaRequestModel = new OllamaGenerateRequest(model, prompt, encodedImages);
        ollamaRequestModel.setOptions(options.getOptionsMap());
        return generateSyncForOllamaRequestModel(ollamaRequestModel, streamHandler);
    }

    /**
     * Convenience method to call the Ollama API using image byte arrays without streaming responses.
     * <p>
     * Uses {@link #generateWithImages(String, String, List, Options, OllamaStreamHandler)}
     *
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaResult generateWithImages(String model, String prompt, List<byte[]> images, Options options) throws OllamaBaseException, IOException, InterruptedException {
        return generateWithImages(model, prompt, images, options, null);
    }

    /**
     * Ask a question to a model based on a given message stack (i.e. a chat
     * history). Creates a synchronous call to the api
     * 'api/chat'.
     *
     * @param model    the ollama model to ask the question to
     * @param messages chat history / message stack to send to the model
     * @return {@link OllamaChatResult} containing the api response and the message
     *         history including the newly acquired assistant response.
     * @throws OllamaBaseException  any response code than 200 has been returned
     * @throws IOException          in case the responseStream can not be read
     * @throws InterruptedException in case the server is not reachable or network
     *                              issues happen
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws ToolInvocationException if the tool invocation fails
     */
    public OllamaChatResult chat(String model, List<OllamaChatMessage> messages)
            throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(model);
        return chat(builder.withMessages(messages).build());
    }

    /**
     * Ask a question to a model using an {@link OllamaChatRequest}. This can be
     * constructed using an {@link OllamaChatRequestBuilder}.
     * <p>
     * Hint: the OllamaChatRequestModel#getStream() property is not implemented.
     *
     * @param request request object to be sent to the server
     * @return {@link OllamaChatResult}
     * @throws OllamaBaseException  any response code than 200 has been returned
     * @throws IOException          in case the responseStream can not be read
     * @throws InterruptedException in case the server is not reachable or network
     *                              issues happen
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws ToolInvocationException if the tool invocation fails
     */
    public OllamaChatResult chat(OllamaChatRequest request)
            throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        return chat(request, null);
    }

    /**
     * Ask a question to a model using an {@link OllamaChatRequest}. This can be
     * constructed using an {@link OllamaChatRequestBuilder}.
     * <p>
     * Hint: the OllamaChatRequestModel#getStream() property is not implemented.
     *
     * @param request       request object to be sent to the server
     * @param streamHandler callback handler to handle the last message from stream
     *                      (caution: all previous tokens from stream will be
     *                      concatenated)
     * @return {@link OllamaChatResult}
     * @throws OllamaBaseException  any response code than 200 has been returned
     * @throws IOException          in case the responseStream can not be read
     * @throws InterruptedException in case the server is not reachable or network
     *                              issues happen
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     * @throws ToolInvocationException if the tool invocation fails
     */
    public OllamaChatResult chat(OllamaChatRequest request, OllamaStreamHandler streamHandler)
            throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        return chatStreaming(request, new OllamaChatStreamObserver(streamHandler));
    }

    /**
     * Ask a question to a model using an {@link OllamaChatRequest}. This can be
     * constructed using an {@link OllamaChatRequestBuilder}.
     * <p>
     * Hint: the OllamaChatRequestModel#getStream() property is not implemented.
     *
     * @param request      request object to be sent to the server
     * @param tokenHandler callback handler to handle the last token from stream
     *                     (caution: the previous tokens from stream will not be
     *                     concatenated)
     * @return {@link OllamaChatResult}
     * @throws OllamaBaseException  any response code than 200 has been returned
     * @throws IOException          in case the responseStream can not be read
     * @throws InterruptedException in case the server is not reachable or network
     *                              issues happen
     * @throws OllamaBaseException  if the response indicates an error status
     * @throws IOException          if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the operation is interrupted
     */
    public OllamaChatResult chatStreaming(OllamaChatRequest request, OllamaTokenHandler tokenHandler)
            throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        OllamaChatEndpointCaller requestCaller = new OllamaChatEndpointCaller(host, auth, requestTimeoutSeconds,
                verbose);
        OllamaChatResult result;

        // add all registered tools to Request
        request.setTools(toolRegistry.getRegisteredSpecs().stream().map(Tools.ToolSpecification::getToolPrompt)
                .collect(Collectors.toList()));

        if (tokenHandler != null) {
            request.setStream(true);
            result = requestCaller.call(request, tokenHandler);
        } else {
            result = requestCaller.callSync(request);
        }

        // check if toolCallIsWanted
        List<OllamaChatToolCalls> toolCalls = result.getResponseModel().getMessage().getToolCalls();
        int toolCallTries = 0;
        while (toolCalls != null && !toolCalls.isEmpty() && toolCallTries < maxChatToolCallRetries) {
            for (OllamaChatToolCalls toolCall : toolCalls) {
                String toolName = toolCall.getFunction().getName();
                ToolFunction toolFunction = toolRegistry.getToolFunction(toolName);
                if (toolFunction == null) {
                    throw new ToolInvocationException("Tool function not found: " + toolName);
                }
                Map<String, Object> arguments = toolCall.getFunction().getArguments();
                Object res = toolFunction.apply(arguments);
                request.getMessages().add(new OllamaChatMessage(OllamaChatMessageRole.TOOL,
                        "[TOOL_RESULTS]" + toolName + "(" + arguments.keySet() + ") : " + res + "[/TOOL_RESULTS]"));
            }

            if (tokenHandler != null) {
                result = requestCaller.call(request, tokenHandler);
            } else {
                result = requestCaller.callSync(request);
            }
            toolCalls = result.getResponseModel().getMessage().getToolCalls();
            toolCallTries++;
        }

        return result;
    }

    /**
     * Registers a single tool in the tool registry using the provided tool
     * specification.
     *
     * @param toolSpecification the specification of the tool to register. It
     *                          contains the
     *                          tool's function name and other relevant information.
     */
    public void registerTool(Tools.ToolSpecification toolSpecification) {
        toolRegistry.addTool(toolSpecification.getFunctionName(), toolSpecification);
        if (this.verbose) {
            logger.debug("Registered tool: {}", toolSpecification.getFunctionName());
        }
    }

    /**
     * Registers multiple tools in the tool registry using a list of tool
     * specifications.
     * Iterates over the list and adds each tool specification to the registry.
     *
     * @param toolSpecifications a list of tool specifications to register. Each
     *                           specification
     *                           contains information about a tool, such as its
     *                           function name.
     */
    public void registerTools(List<Tools.ToolSpecification> toolSpecifications) {
        for (Tools.ToolSpecification toolSpecification : toolSpecifications) {
            toolRegistry.addTool(toolSpecification.getFunctionName(), toolSpecification);
        }
    }

    /**
     * Registers tools based on the annotations found on the methods of the caller's
     * class and its providers.
     * This method scans the caller's class for the {@link OllamaToolService}
     * annotation and recursively registers
     * annotated tools from all the providers specified in the annotation.
     *
     * @throws IllegalStateException if the caller's class is not annotated with
     *                               {@link OllamaToolService}.
     * @throws RuntimeException      if any reflection-based instantiation or
     *                               invocation fails.
     */
    public void registerAnnotatedTools() {
        try {
            Class<?> callerClass = null;
            try {
                callerClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            OllamaToolService ollamaToolServiceAnnotation = callerClass.getDeclaredAnnotation(OllamaToolService.class);
            if (ollamaToolServiceAnnotation == null) {
                throw new IllegalStateException(callerClass + " is not annotated as " + OllamaToolService.class);
            }

            Class<?>[] providers = ollamaToolServiceAnnotation.providers();
            for (Class<?> provider : providers) {
                registerAnnotatedTools(provider.getDeclaredConstructor().newInstance());
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers tools based on the annotations found on the methods of the provided
     * object.
     * This method scans the methods of the given object and registers tools using
     * the {@link ToolSpec} annotation
     * and associated {@link ToolProperty} annotations. It constructs tool
     * specifications and stores them in a tool registry.
     *
     * @param object the object whose methods are to be inspected for annotated
     *               tools.
     * @throws RuntimeException if any reflection-based instantiation or invocation
     *                          fails.
     */
    public void registerAnnotatedTools(Object object) {
        Class<?> objectClass = object.getClass();
        Method[] methods = objectClass.getMethods();
        for (Method m : methods) {
            ToolSpec toolSpec = m.getDeclaredAnnotation(ToolSpec.class);
            if (toolSpec == null) {
                continue;
            }
            String operationName = !toolSpec.name().isBlank() ? toolSpec.name() : m.getName();
            String operationDesc = !toolSpec.desc().isBlank() ? toolSpec.desc() : operationName;

            final Tools.PropsBuilder propsBuilder = new Tools.PropsBuilder();
            LinkedHashMap<String, String> methodParams = new LinkedHashMap<>();
            for (Parameter parameter : m.getParameters()) {
                final ToolProperty toolPropertyAnn = parameter.getDeclaredAnnotation(ToolProperty.class);
                String propType = parameter.getType().getTypeName();
                if (toolPropertyAnn == null) {
                    methodParams.put(parameter.getName(), null);
                    continue;
                }
                String propName = !toolPropertyAnn.name().isBlank() ? toolPropertyAnn.name() : parameter.getName();
                methodParams.put(propName, propType);
                propsBuilder.withProperty(propName, Tools.PromptFuncDefinition.Property.builder().type(propType)
                        .description(toolPropertyAnn.desc()).required(toolPropertyAnn.required()).build());
            }
            final Map<String, Tools.PromptFuncDefinition.Property> params = propsBuilder.build();
            List<String> reqProps = params.entrySet().stream().filter(e -> e.getValue().isRequired())
                    .map(Map.Entry::getKey).collect(Collectors.toList());

            Tools.ToolSpecification toolSpecification = Tools.ToolSpecification.builder().functionName(operationName)
                    .functionDescription(operationDesc)
                    .toolPrompt(Tools.PromptFuncDefinition.builder().type("function")
                            .function(Tools.PromptFuncDefinition.PromptFuncSpec.builder().name(operationName)
                                    .description(operationDesc).parameters(Tools.PromptFuncDefinition.Parameters
                                            .builder().type("object").properties(params).required(reqProps).build())
                                    .build())
                            .build())
                    .build();

            ReflectionalToolFunction reflectionalToolFunction = new ReflectionalToolFunction(object, m, methodParams);
            toolSpecification.setToolFunction(reflectionalToolFunction);
            toolRegistry.addTool(toolSpecification.getFunctionName(), toolSpecification);
        }

    }

    /**
     * Adds a custom role.
     *
     * @param roleName the name of the custom role to be added
     * @return the newly created OllamaChatMessageRole
     */
    public OllamaChatMessageRole addCustomRole(String roleName) {
        return OllamaChatMessageRole.newCustomRole(roleName);
    }

    /**
     * Lists all available roles.
     *
     * @return a list of available OllamaChatMessageRole objects
     */
    public List<OllamaChatMessageRole> listRoles() {
        return OllamaChatMessageRole.getRoles();
    }

    /**
     * Retrieves a specific role by name.
     *
     * @param roleName the name of the role to retrieve
     * @return the OllamaChatMessageRole associated with the given name
     * @throws RoleNotFoundException if the role with the specified name does not
     *                               exist
     */
    public OllamaChatMessageRole getRole(String roleName) throws RoleNotFoundException {
        return OllamaChatMessageRole.getRole(roleName);
    }

    // technical private methods //

    /**
     * Utility method to encode a file into a Base64 encoded string.
     *
     * @param file the file to be encoded into Base64.
     * @return a Base64 encoded string representing the contents of the file.
     * @throws IOException if an I/O error occurs during reading the file.
     */
    private static String encodeFileToBase64(File file) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }

    /**
     * Utility method to encode a byte array into a Base64 encoded string.
     *
     * @param bytes the byte array to be encoded into Base64.
     * @return a Base64 encoded string representing the byte array.
     */
    private static String encodeByteArrayToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Generates a request for the Ollama API and returns the result.
     * This method synchronously calls the Ollama API. If a stream handler is
     * provided,
     * the request will be streamed; otherwise, a regular synchronous request will
     * be made.
     *
     * @param ollamaRequestModel the request model containing necessary parameters
     *                           for the Ollama API request.
     * @param streamHandler      the stream handler to process streaming responses,
     *                           or null for non-streaming requests.
     * @return the result of the Ollama API request.
     * @throws OllamaBaseException  if the request fails due to an issue with the
     *                              Ollama API.
     * @throws IOException          if an I/O error occurs during the request
     *                              process.
     * @throws InterruptedException if the thread is interrupted during the request.
     */
    private OllamaResult generateSyncForOllamaRequestModel(OllamaGenerateRequest ollamaRequestModel,
            OllamaStreamHandler streamHandler) throws OllamaBaseException, IOException, InterruptedException {
        OllamaGenerateEndpointCaller requestCaller = new OllamaGenerateEndpointCaller(host, auth, requestTimeoutSeconds,
                verbose);
        OllamaResult result;
        if (streamHandler != null) {
            ollamaRequestModel.setStream(true);
            result = requestCaller.call(ollamaRequestModel, streamHandler);
        } else {
            result = requestCaller.callSync(ollamaRequestModel);
        }
        return result;
    }

    /**
     * Get default request builder.
     *
     * @param uri URI to get a HttpRequest.Builder
     * @return HttpRequest.Builder
     */
    private HttpRequest.Builder getRequestBuilderDefault(URI uri) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri).header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(requestTimeoutSeconds));
        if (isBasicAuthCredentialsSet()) {
            requestBuilder.header("Authorization", auth.getAuthHeaderValue());
        }
        return requestBuilder;
    }

    /**
     * Check if Basic Auth credentials set.
     *
     * @return true when Basic Auth credentials set
     */
    private boolean isBasicAuthCredentialsSet() {
        return auth != null;
    }

    private Object invokeTool(ToolFunctionCallSpec toolFunctionCallSpec) throws ToolInvocationException {
        try {
            String methodName = toolFunctionCallSpec.getName();
            Map<String, Object> arguments = toolFunctionCallSpec.getArguments();
            ToolFunction function = toolRegistry.getToolFunction(methodName);
            if (verbose) {
                logger.debug("Invoking function {} with arguments {}", methodName, arguments);
            }
            if (function == null) {
                throw new ToolNotFoundException(
                        "No such tool: " + methodName + ". Please register the tool before invoking it.");
            }
            return function.apply(arguments);
        } catch (Exception e) {
            throw new ToolInvocationException("Failed to invoke tool: " + toolFunctionCallSpec.getName(), e);
        }
    }
}
