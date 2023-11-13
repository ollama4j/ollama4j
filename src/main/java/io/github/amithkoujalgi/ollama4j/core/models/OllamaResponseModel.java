package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaResponseModel {
    private String model;
    private @JsonProperty("created_at") String createdAt;
    private String response;
    private boolean done;
    private List<Integer> context;
    private @JsonProperty("total_duration") Long totalDuration;
    private @JsonProperty("load_duration") Long loadDuration;
    private @JsonProperty("prompt_eval_duration") Long promptEvalDuration;
    private @JsonProperty("eval_duration") Long evalDuration;
    private @JsonProperty("prompt_eval_count") Integer promptEvalCount;
    private @JsonProperty("eval_count") Integer evalCount;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public List<Integer> getContext() {
        return context;
    }

    public void setContext(List<Integer> context) {
        this.context = context;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(Long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public Long getPromptEvalDuration() {
        return promptEvalDuration;
    }

    public void setPromptEvalDuration(Long promptEvalDuration) {
        this.promptEvalDuration = promptEvalDuration;
    }

    public Long getEvalDuration() {
        return evalDuration;
    }

    public void setEvalDuration(Long evalDuration) {
        this.evalDuration = evalDuration;
    }

    public Integer getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(Integer promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public Integer getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(Integer eval_count) {
        this.evalCount = eval_count;
    }
}
