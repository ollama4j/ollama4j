package io.github.ollama4j.models.ps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelsProcessResponse {
    private List<ModelProcess> models;

    @Data
    @NoArgsConstructor
    public static class ModelProcess {
        private String name;
        private String model;
        private long size;
        private String digest;
        private ModelDetails details;
        private String expiresAt;
        private long sizeVram;
    }

    @Data
    @NoArgsConstructor
    public static class ModelDetails {
        private String parentModel;
        private String format;
        private String family;
        private List<String> families;
        private String parameterSize;
        private String quantizationLevel;
    }
}
