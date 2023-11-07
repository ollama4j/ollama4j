package io.github.amithkoujalgi.ollama4j.core.models;

import com.google.gson.Gson;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class OllamaAsyncResultCallback extends Thread {
    private final HttpURLConnection connection;
    private String result;
    private boolean isDone;

    public OllamaAsyncResultCallback(HttpURLConnection con) {
        this.connection = con;
        this.isDone = false;
        this.result = "";
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        int responseCode = 0;
        try {
            responseCode = this.connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in =
                             new BufferedReader(new InputStreamReader(this.connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        OllamaResponseModel ollamaResponseModel =
                                gson.fromJson(inputLine, OllamaResponseModel.class);
                        if (!ollamaResponseModel.getDone()) {
                            response.append(ollamaResponseModel.getResponse());
                        }
                        //                        System.out.println("Streamed response line: " +
                        // responseModel.getResponse());
                    }
                    in.close();
                    this.isDone = true;
                    this.result = response.toString();
                }
            } else {
                throw new OllamaBaseException(
                        connection.getResponseCode() + " - " + connection.getResponseMessage());
            }
        } catch (IOException | OllamaBaseException e) {
            this.isDone = true;
            this.result = "FAILED! " + e.getMessage();
        }
    }

    public boolean isComplete() {
        return isDone;
    }

    public String getResponse() {
        return result;
    }
}
