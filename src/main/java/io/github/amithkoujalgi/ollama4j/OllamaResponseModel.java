package io.github.amithkoujalgi.ollama4j;

import java.util.List;

public
class OllamaResponseModel {
    private String model;
    private String created_at;
    private String response;
    private Boolean done;
    private List<Integer> context;
    private Long total_duration;
    private Long load_duration;
    private Long prompt_eval_duration;
    private Long eval_duration;
    private Integer prompt_eval_count;
    private Integer eval_count;

    public String getModel() {
        return model;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getResponse() {
        return response;
    }

    public Boolean getDone() {
        return done;
    }

    public List<Integer> getContext() {
        return context;
    }

    public Long getTotal_duration() {
        return total_duration;
    }

    public Long getLoad_duration() {
        return load_duration;
    }

    public Long getPrompt_eval_duration() {
        return prompt_eval_duration;
    }

    public Long getEval_duration() {
        return eval_duration;
    }

    public Integer getPrompt_eval_count() {
        return prompt_eval_count;
    }

    public Integer getEval_count() {
        return eval_count;
    }
}
