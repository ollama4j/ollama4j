package io.github.amithkoujalgi.ollama4j.core;

import com.google.gson.Gson;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.*;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The base Ollama API class.
 */
@SuppressWarnings({"DuplicatedCode", "ExtractMethodRecommender"})
public class OllamaAPI {
    private static final Logger logger = LoggerFactory.getLogger(OllamaAPI.class);
    private final String host;
    private boolean verbose = false;

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

    /**
     * Set/unset logging of responses
     * @param verbose true/false
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * List available models from Ollama server.
     *
     * @return the list
     * @throws IOException
     * @throws OllamaBaseException
     * @throws ParseException
     */
    public List<Model> listModels() throws IOException, OllamaBaseException, ParseException {
        String url = this.host + "/api/tags";
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(httpGet)) {
            final int statusCode = response.getCode();
            HttpEntity responseEntity = response.getEntity();
            String responseString = "";
            if (responseEntity != null) {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
            }
            if (statusCode == 200) {
                Models m = new Gson().fromJson(responseString, Models.class);
                return m.getModels();
            } else {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        }
    }

    /**
     * Gets model details from the Ollama server.
     *
     * @param modelName the model
     * @return the model details
     * @throws IOException
     * @throws OllamaBaseException
     * @throws ParseException
     */
    public ModelDetail getModelDetails(String modelName) throws IOException, OllamaBaseException, ParseException {
        String url = this.host + "/api/show";
        String jsonData = String.format("{\"name\": \"%s\"}", modelName);
        final HttpPost httpPost = new HttpPost(url);
        final StringEntity entity = new StringEntity(jsonData);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(httpPost)) {
            final int statusCode = response.getCode();
            HttpEntity responseEntity = response.getEntity();
            String responseString = "";
            if (responseEntity != null) {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
            }
            if (statusCode == 200) {
                return new Gson().fromJson(responseString, ModelDetail.class);
            } else {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        }
    }

    /**
     * Pull a model on the Ollama server from the list of <a href="https://ollama.ai/library">available models</a>.
     *
     * @param model the name of the model
     * @throws IOException
     * @throws ParseException
     * @throws OllamaBaseException
     */
    public void pullModel(String model) throws IOException, ParseException, OllamaBaseException {
        List<Model> models = listModels().stream().filter(m -> m.getModelName().split(":")[0].equals(model)).collect(Collectors.toList());
        if (!models.isEmpty()) {
            return;
        }
        String url = this.host + "/api/pull";
        String jsonData = String.format("{\"name\": \"%s\"}", model);
        final HttpPost httpPost = new HttpPost(url);
        final StringEntity entity = new StringEntity(jsonData);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(httpPost)) {
            final int statusCode = response.getCode();
            HttpEntity responseEntity = response.getEntity();
            String responseString = "";
            if (responseEntity != null) {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
            }
            if (statusCode != 200) {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        }
    }

    /**
     * Create a custom model from a model file.
     * Read more about custom model file creation <a href="https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md">here</a>.
     *
     * @param modelName the name of the custom model to be created.
     * @param modelFilePath the path to model file that exists on the Ollama server.
     * @throws IOException
     * @throws ParseException
     * @throws OllamaBaseException
     */
    public void createModel(String modelName, String modelFilePath) throws IOException, ParseException, OllamaBaseException {
        String url = this.host + "/api/create";
        String jsonData = String.format("{\"name\": \"%s\", \"path\": \"%s\"}", modelName, modelFilePath);
        final HttpPost httpPost = new HttpPost(url);
        final StringEntity entity = new StringEntity(jsonData);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(httpPost)) {
            final int statusCode = response.getCode();
            HttpEntity responseEntity = response.getEntity();
            String responseString = "";
            if (responseEntity != null) {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
                // FIXME: Ollama API returns HTTP status code 200 for model creation failure cases. Correct this if the issue is fixed in the Ollama API server.
                if (responseString.contains("error")) {
                    throw new OllamaBaseException(responseString);
                }
                if (verbose) {
                    logger.info(responseString);
                }
            }
            if (statusCode != 200) {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        }
    }

    /**
     * Delete a model from Ollama server.
     *
     * @param name the name of the model to be deleted.
     * @param ignoreIfNotPresent - ignore errors if the specified model is not present on Ollama server.
     * @throws IOException
     * @throws ParseException
     * @throws OllamaBaseException
     */
    public void deleteModel(String name, boolean ignoreIfNotPresent) throws IOException, ParseException, OllamaBaseException {
        String url = this.host + "/api/delete";
        String jsonData = String.format("{\"name\": \"%s\"}", name);
        final HttpDelete httpDelete = new HttpDelete(url);
        final StringEntity entity = new StringEntity(jsonData);
        httpDelete.setEntity(entity);
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(httpDelete)) {
            final int statusCode = response.getCode();
            HttpEntity responseEntity = response.getEntity();
            String responseString = "";
            if (responseEntity != null) {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
                if (verbose) {
                    logger.info(responseString);
                }
            }
            if (statusCode == 404 && responseString.contains("model") && responseString.contains("not found")) {
                return;
            }
            if (statusCode != 200) {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        }
    }


    /**
     * Ask a question to a model running on Ollama server. This is a sync/blocking call.
     *
     * @param ollamaModelType the ollama model to ask the question to
     * @param promptText the prompt/question text
     * @return the response text from the model
     * @throws OllamaBaseException
     * @throws IOException
     */
    public String ask(String ollamaModelType, String promptText) throws OllamaBaseException, IOException {
        Gson gson = new Gson();
        OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(ollamaModelType, promptText);
        URL obj = new URL(this.host + "/api/generate");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(ollamaRequestModel.toString());
        }
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    OllamaResponseModel ollamaResponseModel = gson.fromJson(inputLine, OllamaResponseModel.class);
                    if (!ollamaResponseModel.getDone()) {
                        response.append(ollamaResponseModel.getResponse());
                    }
                }
                in.close();
                return response.toString();
            }
        } else {
            throw new OllamaBaseException(con.getResponseCode() + " - " + con.getResponseMessage());
        }
    }

    /**
     * Ask a question to a model running on Ollama server and get a callback handle that can be used to check for status and get the response from the model later.
     * This would be a async/non-blocking call.
     *
     * @param ollamaModelType the ollama model to ask the question to
     * @param promptText the prompt/question text
     * @return the ollama async result callback handle
     * @throws IOException
     */
    public OllamaAsyncResultCallback askAsync(String ollamaModelType, String promptText) throws IOException {
        OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(ollamaModelType, promptText);
        URL obj = new URL(this.host + "/api/generate");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(ollamaRequestModel.toString());
        }
        OllamaAsyncResultCallback ollamaAsyncResultCallback = new OllamaAsyncResultCallback(con);
        ollamaAsyncResultCallback.start();
        return ollamaAsyncResultCallback;
    }
}
