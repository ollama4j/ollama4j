package io.github.amithkoujalgi.ollama4j.core.models;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.Queue;


@SuppressWarnings("DuplicatedCode")
public class OllamaAsyncResultCallback extends Thread {
    private final HttpURLConnection connection;
    private String result;
    private boolean isDone;
    private final Queue<String> queue = new LinkedList<>();

    public OllamaAsyncResultCallback(HttpURLConnection connection) {
        this.connection = connection;
        this.isDone = false;
        this.result = "";
        this.queue.add("");
    }

    @Override
    public void run() {
        int responseCode = 0;
        try {
            responseCode = this.connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(this.connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        OllamaResponseModel ollamaResponseModel = Utils.getObjectMapper().readValue(inputLine, OllamaResponseModel.class);
                        queue.add(ollamaResponseModel.getResponse());
                        if (!ollamaResponseModel.getDone()) {
                            response.append(ollamaResponseModel.getResponse());
                        }
                    }
                    in.close();
                    this.isDone = true;
                    this.result = response.toString();
                }
            } else {
                throw new OllamaBaseException(connection.getResponseCode() + " - " + connection.getResponseMessage());
            }
        } catch (IOException | OllamaBaseException e) {
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
