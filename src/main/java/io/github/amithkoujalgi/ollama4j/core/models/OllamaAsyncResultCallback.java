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

    public OllamaAsyncResultCallback(HttpClient client, URI uri, OllamaRequestModel ollamaRequestModel) {
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
            HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(Utils.getObjectMapper().writeValueAsString(ollamaRequestModel))).header("Content-Type", "application/json").build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int statusCode = response.statusCode();

            InputStream responseBodyStream = response.body();
            String responseString = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
                String line;
                StringBuilder responseBuffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    OllamaResponseModel ollamaResponseModel = Utils.getObjectMapper().readValue(line, OllamaResponseModel.class);
                    queue.add(ollamaResponseModel.getResponse());
                    if (!ollamaResponseModel.getDone()) {
                        responseBuffer.append(ollamaResponseModel.getResponse());
                    }
                }
                reader.close();
                this.isDone = true;
                this.result = responseBuffer.toString();
            }
            if (statusCode != 200) {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        } catch (IOException | InterruptedException | OllamaBaseException e) {
            this.isDone = true;
            this.result = "FAILED! " + e.getMessage();
        }
    }

    public boolean isComplete() {
        return isDone;
    }

    /**
     * Returns the final response when the execution completes. Does not return intermediate results.
     * @return response text
     */
    public String getResponse() {
        return result;
    }

    public Queue<String> getStream() {
        return queue;
    }
}
