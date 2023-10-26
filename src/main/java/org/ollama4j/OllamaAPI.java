package org.ollama4j;

import com.google.gson.Gson;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("deprecation")
public class OllamaAPI {
    private final String host;

    public OllamaAPI(String host) {
        if (host.endsWith("/")) {
            this.host = host.substring(0, host.length() - 1);
        } else {
            this.host = host;
        }
    }

    public void pullModel(OllamaModel model) throws IOException, ParseException, OllamaBaseException {
        String url = this.host + "/api/pull";
        String jsonData = String.format("{\"name\": \"%s\"}", model.getModel());
        final HttpPost httpPost = new HttpPost(url);
        final StringEntity entity = new StringEntity(jsonData);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(httpPost)) {
            final int statusCode = response.getCode();
            HttpEntity responseEntity = response.getEntity();
            String responseString = "";
            if (responseEntity != null) {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
            }
            if (statusCode == 200) {
                System.out.println(responseString);
            } else {
                throw new OllamaBaseException(statusCode + " - " + responseString);
            }
        }
    }

    public String runSync(OllamaModel ollamaModel, String promptText) throws OllamaBaseException, IOException {
        Gson gson = new Gson();
        OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(ollamaModel.getModel(), promptText);
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
                    System.out.println("Streamed response line: " + ollamaResponseModel.getResponse());
                }
                in.close();
                return response.toString();
            }
        } else {
            throw new OllamaBaseException(con.getResponseCode() + " - " + con.getResponseMessage());
        }
    }

    public OllamaAsyncResultCallback runAsync(OllamaModel ollamaModel, String promptText) throws OllamaBaseException, IOException {
        OllamaRequestModel ollamaRequestModel = new OllamaRequestModel(ollamaModel.getModel(), promptText);
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

