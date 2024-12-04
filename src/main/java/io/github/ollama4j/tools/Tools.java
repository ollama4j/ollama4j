package io.github.ollama4j.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tools {
    @Data
    @Builder
    public static class ToolSpecification {
        private String functionName;
        private String functionDescription;
        private PromptFuncDefinition toolPrompt;
        private ToolFunction toolFunction;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptFuncDefinition {
        private String type;
        private PromptFuncSpec function;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PromptFuncSpec {
            private String name;
            private String description;
            private Parameters parameters;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Parameters {
            private String type;
            private Map<String, Property> properties;
            private List<String> required;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Property {
            private String type;
            private String description;
            @JsonProperty("enum")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private List<String> enumValues;
            @JsonIgnore
            private boolean required;
        }
    }

    public static class PropsBuilder {
        private final Map<String, PromptFuncDefinition.Property> props = new HashMap<>();

        public PropsBuilder withProperty(String key, PromptFuncDefinition.Property property) {
            props.put(key, property);
            return this;
        }

        public Map<String, PromptFuncDefinition.Property> build() {
            return props;
        }
    }

    public static class PromptBuilder {
        private final List<PromptFuncDefinition> tools = new ArrayList<>();

        private String promptText;

        public String build() throws JsonProcessingException {
            return "[AVAILABLE_TOOLS] " + Utils.getObjectMapper().writeValueAsString(tools) + "[/AVAILABLE_TOOLS][INST] " + promptText + " [/INST]";
        }

        public PromptBuilder withPrompt(String prompt) throws JsonProcessingException {
            promptText = prompt;
            return this;
        }

        public PromptBuilder withToolSpecification(ToolSpecification spec) {
            PromptFuncDefinition def = new PromptFuncDefinition();
            def.setType("function");

            PromptFuncDefinition.PromptFuncSpec functionDetail = new PromptFuncDefinition.PromptFuncSpec();
            functionDetail.setName(spec.getFunctionName());
            functionDetail.setDescription(spec.getFunctionDescription());

            PromptFuncDefinition.Parameters parameters = new PromptFuncDefinition.Parameters();
            parameters.setType("object");
            parameters.setProperties(spec.getToolPrompt().getFunction().parameters.getProperties());

            List<String> requiredValues = new ArrayList<>();
            for (Map.Entry<String, PromptFuncDefinition.Property> p : spec.getToolPrompt().getFunction().getParameters().getProperties().entrySet()) {
                if (p.getValue().isRequired()) {
                    requiredValues.add(p.getKey());
                }
            }
            parameters.setRequired(requiredValues);
            functionDetail.setParameters(parameters);
            def.setFunction(functionDetail);

            tools.add(def);
            return this;
        }
    }
}
